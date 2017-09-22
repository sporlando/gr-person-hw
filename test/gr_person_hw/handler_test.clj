(ns gr-person-hw.handler-test
  (:require [gr-person-hw.handler :as handler]
            [gr-person-hw.response :as response]
            [gr-person-hw.config :as config]
            [gr-person-hw.data :refer [person-data]]
            [cheshire.core :as cheshire]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(def mock-person-data (atom []))

(deftest test-get-routes
  (with-redefs [config/file-codec {"test_person_comma.csv" #","}
                config/person-file-directory "./resources/ingest-files-test/"]
    (testing "GET all records by gender."
      (let [response (handler/app (mock/request :get "/records/gender"))]
        (is (= (:status response) 200))
        (is (= (get-in response [:headers "Content-Type"]) "text/json; charset=utf-8"))
        (is (= (cheshire/parse-string (:body response) true)
               (seq [{:LastName "Lively"
                      :FirstName "Blake"
                      :Gender "Female"
                      :FavoriteColor "Green"
                      :DateOfBirth "8/27/1977"}
                     {:LastName "Stark"
                      :FirstName "Arya"
                      :Gender "Female"
                      :FavoriteColor "Yellow"
                      :DateOfBirth "7/22/1974"}
                     {:LastName "Gordon"
                      :FirstName "Jeff"
                      :Gender "Male"
                      :FavoriteColor "Red"
                      :DateOfBirth "6/14/1969"}])))))

    (testing "GET all records by birthdate."
      (let [response (handler/app (mock/request :get "/records/birthdate"))]
        (is (= (:status response) 200))
        (is (= (get-in response [:headers "Content-Type"]) "text/json; charset=utf-8"))
        (is (= (cheshire/parse-string (:body response) true)
               (seq [{:LastName "Gordon"
                      :FirstName "Jeff"
                      :Gender "Male"
                      :FavoriteColor "Red"
                      :DateOfBirth "6/14/1969"}
                     {:LastName "Stark"
                      :FirstName "Arya"
                      :Gender "Female"
                      :FavoriteColor "Yellow"
                      :DateOfBirth "7/22/1974"}
                     {:LastName "Lively"
                      :FirstName "Blake"
                      :Gender "Female"
                      :FavoriteColor "Green"
                      :DateOfBirth "8/27/1977"}])))))

    (testing "GET all records by last name."
      (let [response (handler/app (mock/request :get "/records/name"))]
        (is (= (:status response) 200))
        (is (= (get-in response [:headers "Content-Type"]) "text/json; charset=utf-8"))
        (is (= (cheshire/parse-string (:body response) true)
               (seq [{:LastName "Gordon"
                      :FirstName "Jeff"
                      :Gender "Male"
                      :FavoriteColor "Red"
                      :DateOfBirth "6/14/1969"}
                     {:LastName "Lively"
                      :FirstName "Blake"
                      :Gender "Female"
                      :FavoriteColor "Green"
                      :DateOfBirth "8/27/1977"}
                     {:LastName "Stark"
                      :FirstName "Arya"
                      :Gender "Female"
                      :FavoriteColor "Yellow"
                      :DateOfBirth "7/22/1974"}])))))))

(deftest test-post-route
  (with-redefs [person-data mock-person-data]
    (testing "POST new record in a comma delimited format."
      (let [body (cheshire/generate-string "Orlando,Sam,Male,Blue,12/31/1991")
            response (handler/app (mock/request :post "/records" body))]
        (is (= (:status response) 200))
        (is (= (get-in response [:headers "Content-Type"]) "text/json; charset=utf-8"))
        (is (= (cheshire/parse-string (:body response) true)
               (seq [{:LastName "Orlando"
                      :FirstName "Sam"
                      :Gender "Male"
                      :FavoriteColor "Blue"
                      :DateOfBirth "12/31/1991"}])))))

    (testing "POST new record in a pipe delimited format."
      (let [body (cheshire/generate-string "Orlando|Sam|Male|Blue|12/31/1991")
            response (handler/app (mock/request :post "/records" body))]
        (is (= (:status response) 200))
        (is (= (get-in response [:headers "Content-Type"]) "text/json; charset=utf-8"))
        (is (= (cheshire/parse-string (:body response) true)
               (seq [{:LastName "Orlando"
                      :FirstName "Sam"
                      :Gender "Male"
                      :FavoriteColor "Blue"
                      :DateOfBirth "12/31/1991"}])))))

    (testing "POST new record in a space delimited format."
      (let [body (cheshire/generate-string "Orlando Sam Male Blue 12/31/1991")
            response (handler/app (mock/request :post "/records" body))]
        (is (= (:status response) 200))
        (is (= (get-in response [:headers "Content-Type"]) "text/json; charset=utf-8"))
        (is (= (cheshire/parse-string (:body response) true)
               (seq [{:LastName "Orlando"
                      :FirstName "Sam"
                      :Gender "Male"
                      :FavoriteColor "Blue"
                      :DateOfBirth "12/31/1991"}])))))))

(deftest test-post-then-get
  (with-redefs [person-data mock-person-data
                config/file-codec {"test_person_comma.csv" #","}
                config/person-file-directory "./resources/ingest-files-test/"]
    (testing "POST a record, have it be added to storage, then do a GET to show
              the new record is returned with all other records."
      (let [mock-person-data (reset! mock-person-data []) ; Make sure atom is empty.
            body (cheshire/generate-string "Orlando Sam Male Blue 12/31/1991")
            _ (handler/app (mock/request :post "/records" body))
            response (handler/app (mock/request :get "/records/gender"))]
        (is (= (:status response) 200))
        (is (= (get-in response [:headers "Content-Type"]) "text/json; charset=utf-8"))
        (is (= (sort-by :LastName (cheshire/parse-string (:body response) true))
               (seq [{:LastName "Gordon"
                      :FirstName "Jeff"
                      :Gender "Male"
                      :FavoriteColor "Red"
                      :DateOfBirth "6/14/1969"}
                     {:LastName "Lively"
                      :FirstName "Blake"
                      :Gender "Female"
                      :FavoriteColor "Green"
                      :DateOfBirth "8/27/1977"}
                     {:LastName "Orlando"
                      :FirstName "Sam"
                      :Gender "Male"
                      :FavoriteColor "Blue"
                      :DateOfBirth "12/31/1991"}
                     {:LastName "Stark"
                      :FirstName "Arya"
                      :Gender "Female"
                      :FavoriteColor "Yellow"
                      :DateOfBirth "7/22/1974"}])))))))
