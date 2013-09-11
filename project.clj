(defproject lein-pushtoy "0.1.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [com.palletops/runit-crate "0.8.0-alpha.1"]
                 [com.palletops/java-crate "0.8.0-beta.5"]

                 [com.palletops/pallet "0.8.0-RC.1"]

                 ;; [ch.qos.logback/logback-classic "1.0.9"]
                 [com.palletops/app-deploy-crate "0.8.0-alpha.3"]]

;;   :dependencies [
;;                  [com.palletops/runit-crate "0.8.0-alpha.1"]
;;                  [com.palletops/java-crate "0.8.0-beta.4"]
;;                  ;; [org.slf4j/slf4j-api "1.6.1"]
;;                  [com.palletops/pallet "0.8.0-RC.1"]
;;                  ;; [ch.qos.logback/logback-core "1.0.0"]
;;                  ;; [ch.qos.logback/logback-classic "1.0.0"]
;;                  [ch.qos.logback/logback-classic "1.0.9"]
;;                  [com.palletops/app-deploy-crate "0.8.0-alpha.1"]

;;                  ;; [clj-logging-config "1.9.10"]
;;                  ;; [log4j/log4j "1.2.16" :exclusions [javax.mail/mail
;;                  ;;                                    javax.jms/jms
;;                  ;;                                    com.sun.jdmk/jmxtools
;;                  ;;                                    com.sun.jmx/jmxri]]
;; ]
  ;; :resource-paths ["resources"]

  ;; :exclusions [commons-logging]
  :eval-in-leiningen true)
