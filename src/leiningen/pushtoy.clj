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
            [pallet.crate.app-deploy :as deploy])
  (:use 
   clojure.tools.logging
   )

  [:require [clojure.tools.logging
             [commons-logging :as cl]
             [impl :as impl]
             [java-util-logging :as jul]
             [log4j :as log4j]
             [slf4j :as slf4j]]]
)

;; (set-logger! :level org.apache.log4j.Level/DEBUG)

;; (log :error 
;;      (pr-str {"(cl/load-factory)"  (cl/load-factory)
;;               "(slf4j/load-factory)" (class (slf4j/load-factory))
;;               "(log4j/load-factory)" (class (log4j/load-factory))
;;               "(jul/load-factory)" (class (jul/load-factory))}))
;; (defn set-log-level! [level]
;;   "Sets the root logger's level, and the level of all of its Handlers, to level.
;;    Level should be one of the constants defined in java.util.logging.Level."
;;   (let [logger (.getLogger (impl-get-log ""))]
;;     (.setLevel logger level)
;;     (doseq [handler (.getHandlers logger)]
;;       (. handler setLevel level))))


(log :debug (impl/get-logger clojure.tools.logging/*logger-factory* *ns*))
(log :info (impl/get-logger clojure.tools.logging/*logger-factory* *ns*))

 (log :warn (impl/get-logger clojure.tools.logging/*logger-factory* *ns*))
(log :error (.getParent (.getLogger (impl/get-logger clojure.tools.logging/*logger-factory* *ns*))))
;; (log :error (.getAttributeNames (impl/get-logger clojure.tools.logging/*logger-factory* *ns*)))

;; (alter-var-root (var clojure.tools.logging/*logger-factory*)
;;                 (fn [& args] (fn [& args])))

;; (def conn-map
;;   (let [node-id (str "node-" app-name "-01")
;;         group-id (str "group-" app-name "-01")
;;         ip "192.241.212.75"]
;;     {:provider "node-list"
;;     :node-list [(node-list/make-node node-id group-id ip :ubuntu)]
;;     :environment {:user (pallet.core.user/make-user "root"
;;                                          :public-key-path "/Users/adrian/.ssh/id_rsa.pub"
;;                                          :private-key-path "/Users/adrian/.ssh/id_rsa")}}))

;; (defn make-service [app-name ips user]
;;   {:provider "node-list"
;;     :node-list (into []
;;                      (for [[i ip] (map-indexed vector ips)
;;                            :let [node-id (str "node-" app-name "-" i)
;;                                  group-id (str "group-" app-name)]]
;;                        (node-list/make-node node-id group-id ip :ubuntu)))
;;     :environment {:user (pallet.core/user/map->User user)}})

(defn pushtoy [project]
  "this is help text.

more info"
  (info "Resource Paths" (:resource-paths project)) 
  (info "Resource Paths" (:dependencies project)) 
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
                                  [{:project-path project-path ;; "target/birthdayreminder-%s-standalone.jar"
                         
                                    :path app-jar ;; "birthdayreminder.jar"
                                    }]}
                      :run-command run-command ;;"java -jar /opt/birthdayreminder/birthdayreminder.jar"
                      }
                     :instance-id (keyword app-name) ;:birthdayreminder
                     )
        group (core/group-spec group-id
                               :extends [(java/server-spec {})
                                         (runit/server-spec {})
                                         deploy-spec])]
    (core/lift group
               :phase [:install :configure :deploy]
               :compute service
               :environment {:project project})))





