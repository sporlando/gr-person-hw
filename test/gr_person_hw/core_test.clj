(ns gr-person-hw.core-test
  (:require [gr-person-hw.config :as config]
            [gr-person-hw.core :as core]
            [gr-person-hw.data :as data]
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.test :refer [deftest is testing]]))

(def file-names ["test_person_comma.csv"
                 "test_person_pipe.csv"
                 "test_person_space.csv"])

(def cli-opts
  [["-F" "--all-files" "Extract all files."]
   ["-f" "--file" "Extract file by name. Repeat command for multiple files."
    :validate [#(core/in? file-names %)
               (apply str "Valid file names: " (str/join ", " file-names))]
    :assoc-fn core/concat-file-names]
   ["-h" "--help"]])

(deftest test-usage
  (testing "Display options summary correctly."
    (is (= (core/usage (:summary (parse-opts ["--help"] cli-opts)))
           (str "Extract Files and display outputs.\n\n"
                "Options:\n  -F, --all-files  Extract all files.\n  "
                "-f, --file       "
                "Extract file by name. Repeat command for multiple files.\n  "
                "-h, --help\n"
                "Please refer to your local developer for more information.")))))

(deftest test-in?
  (testing "True if a sequence contains an element, nil otherwise."
    (is (= (core/in? (seq [1 2 3]) 1)
           true))
    (is (= (core/in? (seq [1 2 3]) 4)
           nil))
    (is (= (core/in? (seq ["a" "b" "c"]) "b")
           true))
    (is (= (core/in? (seq ["a" "b" "c"]) "A")
           nil))
    (is (= (core/in? (seq []) "b")
           nil))
    (is (= (core/in? (seq [1 2]) nil)
           nil))
    (is (= (core/in? (seq []) nil)
           nil))))

(deftest test-concat-file-names
  (testing "Concat strings into a vector for a key-value in a map."
    (is (= (core/concat-file-names {"a" 1} "b" "new value")
           {"a" 1 "b" ["new value"]}))
    (is (= (core/concat-file-names {"a" 1 "b" ["old value"]} "b" "new value")
           {"a" 1 "b" ["old value" "new value"]}))
    (is (= (core/concat-file-names {:a 1} :b "new value")
           {:a 1 :b ["new value"]}))
    (is (= (core/concat-file-names {:a 1} :b "new value")
           {:a 1 :b ["new value"]}))
    (is (= (reduce #(core/concat-file-names %1 "key" %2) {} ["val1" "val2"])
           {"key" ["val1" "val2"]}))))

;; TODO: Provide tests for remaining options in -main cond.
