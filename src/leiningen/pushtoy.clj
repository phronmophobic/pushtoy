(ns leiningen.pushtoy
  (:require [pallet.compute :as compute]
            [pallet.phase :as phase]
            [pallet.core :as core]
            [pallet.utils :as utils]
            
            [pallet.api :as api]
            [pallet.compute.node-list :as node-list]
            [pallet.configure :as configure]
            [pallet.actions :as actions]

            [pallet.crate.runit :as runit]
            [pallet.crate.java :as java]
            [pallet.crate.app-deploy :as deploy]))


(defn pushtoy [project]
  "this is help text.

more info"
  (let [options (:pushtoy project)
        app-name (get options :app-name (:name project))
        ips (:ips options)
        _ (assert ips "ips should be a vector of ip strings")
        user (get options :user
                  {:username "root"})
        user (if (every? nil? (select-keys user [:private-key :public-key :public-key-path :private-key-path]))
               (assoc user
                 :public-key-path (pallet.core.user/default-public-key-path)
                 :private-key-path (pallet.core.user/default-private-key-path))
               user)

        group-id (str "group-" app-name)
        service-spec {:provider "node-list"
                      :node-list (into []
                                       (for [[i ip] (map-indexed vector ips)
                                             :let [node-id (str "node-" app-name "-" i)]]
                                         (node-list/make-node node-id group-id ip :ubuntu)))
                      :environment {:user (pallet.core.user/map->User user)}}
        service (configure/compute-service-from-map service-spec)
        app-root (str "/opt/" app-name)
        app-jar (str app-name ".jar")
        project-path (str "target/" app-name "-%s-standalone.jar")
        run-command (str "java -jar " app-root "/" app-jar)
        deploy-spec (deploy/server-spec
                     {:app-root app-root
                      :artifacts {:from-lein
                                  [{:project-path project-path
                         
                                    :path app-jar
                                    }]}
                      :run-command run-command
                      }
                     :instance-id (keyword app-name)
                     )
        group (core/group-spec group-id
                               :extends [(java/server-spec {})
                                         (runit/server-spec {})
                                         deploy-spec])]
    (core/lift group
               :phase [:install :configure :deploy]
               :compute service
               :environment {:project project})))





