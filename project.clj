(defproject gr-person-hw "0.1.0-SNAPSHOT"
  :description "Mock personnel data for a mini ETL-like system."
  :url "http://localhost:3001/"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-time "0.11.0"]
                 [org.clojure/tools.cli "0.3.5"]]
  :plugins [[lein-ring "0.9.6"]]
  :main gr-person-hw.core
  :repl-options {:init-ns user}
  :global-vars {*print-length* 100}
  :profiles {:dev {:resource-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.10"]]}})
