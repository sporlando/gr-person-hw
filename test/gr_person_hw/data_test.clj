(ns gr-person-hw.data-test
  (:require [gr-person-hw.data :as data]
            [clojure.test :refer :all]))

(deftest test-data-test-fn
  (testing "Testing data-test-fn."
    (is (= (data/data-test-fn) "I work."))))
