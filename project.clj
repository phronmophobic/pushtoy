(defproject lein-pushtoy "0.1.3-SNAPSHOT"
  :description "A Leiningen plugin to make deploying clojure apps to an existing servers simple."
  :url "https://github.com/phronmophobic/pushtoy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [com.palletops/runit-crate "0.8.0-alpha.1"]
                 [com.palletops/java-crate "0.8.0-beta.5"]
                 [com.palletops/pallet "0.8.0-RC.1"]
                 [ch.qos.logback/logback-classic "1.0.9"]
                 [org.clojars.strad/nginx-crate "0.8.3"]
                 [org.clojars.phronmophobic/datomic-crate "0.8.10-SNAPSHOT"]
                 ;; [org.clojars.strad/datomic-crate "0.8.8"]
                 [com.palletops/app-deploy-crate "0.8.0-alpha.3"]]
  :eval-in-leiningen true)
