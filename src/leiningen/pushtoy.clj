(ns leiningen.pushtoy
)



(def help-text
  "Simple deployment.

usage: lein pushtoy <command>...

Available commands:

  install: installs java, runit, and creates a service for your clojure application
  deploy: deploys your uberjar in a location that can be run from runit
  start: starts your deployed uberjar using runit
  stop: stops your application via runit
  restart: restarts your application via runit

Configuration:

  Add the following key to your project.clj file:
  
  :pushtoy {:ips [\"<first ip>\" \"<second ip>\"]
            :port <port>
            :server-name \"<server name>\"
            :user {:username \"ubuntu\"}]}
  

Examples:

  First deployment
  $ lein do uberjar, pushtoy install deploy restart

  Subsequent deployments
  $ lein do uberjar, pushtoy deploy restart
")

(def command-map
  {"install" [:settings :install :configure]
   "deploy" [:deploy :restart]
   "start" [:start :run]
   "stop" [:stop]
   "restart" [:restart]})

(defn ^{:doc help-text} pushtoy

  ([project] (println help-text))
  ([project & commands]
     (assert (every? #(contains? command-map %) commands)
             (str "Invalid command list: " commands "\n\n" help-text))

     ;; require here instead of in ns so that other commands (eg. help) 
     ;; run more quickly
     (require                             

      '[pallet.core :as core]

      '[pallet.compute.node-list :as node-list]
      '[pallet.configure :as configure]


      '[pallet.crate.runit :as runit]
      '[pallet.crate.java :as java]
      '[pallet.crate.app-deploy :as deploy]
      '[pallet.crate.nginx :as nginx]
      )
     
     (let [phases (into [] (mapcat command-map commands))

           options (:pushtoy project)
           app-name (get options :app-name (:name project))
           ips (:ips options)
           port (:port options)
           server-name (:server-name options)
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
           ;; https://github.com/rstradling/nginx-crate
           nginx-settings {:sites [{:action :enable
                                    ;; Notice the .site file extension.  See the README notes section for more information.
                                    :name (str app-name ".site") 
                                    :upstreams [{:lines [{:server (str "127.0.0.1:" port)}
                                                         {:keepalive 32}]
                                                 :name (str "http_backend_" app-name)}]
                                    :servers [
                                              {:access-log ["/var/log/nginx/app.access.log"]
                                               :server-name server-name
                                               :locations [{:path "/"
                                                            :proxy-pass (str "http://http_backend_" app-name)
                                                            :proxy-http-version "1.1"

                                                            :proxy-set-header [{:Connection "upgrade"},
                                                                               {:Upgrade "$http_upgrade"}
                                                                               {:X-Forwarded-For 
                                                                                "$proxy_add_x_forwarded_for"}, 
                                                                               {:Host "$http_host"}]}]}]}]
                           :version "1.4.4"
                           :configuration {:gzip "on;\nserver_names_hash_bucket_size 64"}
                           }
           group ((resolve 'core/group-spec) group-id
                                  :extends (vec
                                            (concat
                                             [((resolve 'java/server-spec) {})
                                              ((resolve 'runit/server-spec) {})]
                                             (when (and server-name port)
                                               [((resolve 'nginx/nginx) nginx-settings)])
                                             [deploy-spec])))]
       ((resolve 'core/lift) group
                  :phase phases
                  :compute service
                  :environment {:project project}))))





