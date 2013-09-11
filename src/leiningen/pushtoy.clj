(ns leiningen.pushtoy
)



(def help-text
  "this is help text.

more info")

(def command-map
  {"install" [:install :configure]
   "deploy" [:deploy :restart]
   "start" [:start]
   "stop" [:stop]
   "restart" [:restart]})

(defn ^{:doc help-text} pushtoy

  ([project] (println help-text))
  ([project & commands]
     (assert (every? #(contains? command-map %) commands)
             (str "Invalid command list: " commands "\n\n" help-text))

     (require                             
                                        
      '[pallet.core :as core]

      '[pallet.compute.node-list :as node-list]
      '[pallet.configure :as configure]


      '[pallet.crate.runit :as runit]
      '[pallet.crate.java :as java]
      '[pallet.crate.app-deploy :as deploy]
      )
     
     (let [phases (into [] (mapcat command-map commands))

           options (:pushtoy project)
           app-name (get options :app-name (:name project))
           ips (:ips options)
           _ (assert ips "ips should be a vector of ip strings")
           user (get options :user
                     {:username "root"})
           user (if (every? nil? (select-keys user [:private-key :public-key :public-key-path :private-key-path]))
                  (assoc user
                    :public-key-path ((resolve 'pallet.core.user/default-public-key-path))
                    :private-key-path ((resolve 'pallet.core.user/default-private-key-path)))
                  user)

           group-id (str "group-" app-name)
           service-spec {:provider "node-list"
                         :node-list (into []
                                          (for [[i ip] (map-indexed vector ips)
                                                :let [node-id (str "node-" app-name "-" i)]]
                                            ((resolve 'node-list/make-node) node-id group-id ip :ubuntu)))
                         :environment {:user ((resolve 'pallet.core.user/map->User) user)}}
           service ((resolve 'configure/compute-service-from-map) service-spec)
           app-root (str "/opt/" app-name)
           app-jar (str app-name ".jar")
           project-path (str "target/" app-name "-%s-standalone.jar")
           run-command (str "java -jar " app-root "/" app-jar)
           deploy-spec ((resolve 'deploy/server-spec)
                        {:app-root app-root
                         :artifacts {:from-lein
                                     [{:project-path project-path
                                                   
                                       :path app-jar
                                       }]}
                         :run-command run-command
                         }
                        :instance-id (keyword app-name)
                        )
           group ((resolve 'core/group-spec) group-id
                                  :extends [((resolve 'java/server-spec) {})
                                            ((resolve 'runit/server-spec) {})
                                            deploy-spec])]
       ((resolve 'core/lift) group
                  :phase phases
                  :compute service
                  :environment {:project project}))))





