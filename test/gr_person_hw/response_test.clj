(ns gr-person-hw.response-test
  (:require [gr-person-hw.response :as response]
            [clojure.test :refer :all]))

(deftest test-json-200
  (testing "Validating json-200 response structure."
    (is (= (set (keys (response/json-200 "Testing")))
           #{:status :headers :body}))
    (is (= (:body (response/json-200 {:LastName "Orlando"}))
           "{\n  \"LastName\" : \"Orlando\"\n}"))))
