(defproject gr-person-hw "0.1.0-SNAPSHOT"
  :description "Mock personnel data for a mini ETL-like system."
  :url "http://localhost:3001/"
  :min-lein-version "2.0.0"
  :dependencies [[cheshire "5.8.0"]
                 [clj-time "0.11.0"]
                 [compojure "1.6.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.2.1"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler gr-person-hw.handler/app}
  :main gr-person-hw.core
  :repl-options {:init-ns user}
  :global-vars {*print-length* 100}
  :profiles {:dev {:resource-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.10"]
                                  [javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.1"]]}})
