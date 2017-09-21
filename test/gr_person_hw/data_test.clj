(ns gr-person-hw.data-test
  (:require [gr-person-hw.config :as config]
            [gr-person-hw.data :as data]
            [clj-time.core :as time :only [date-time]]
            [clojure.test :refer [deftest is testing]]))

;;; Utility Tests

(deftest test-date-str->date-obj
  (testing "Transforming a date string into a date object."
    (is (= (data/date-str->date-obj "12/31/1991" "M/d/YYYY")
           (time/date-time 1991 12 31)))
    (is (= (data/date-str->date-obj "3/4/2010" "M/dd/YYYY")
           (time/date-time 2010 3 4)))))

(deftest test-date-obj->date-str
  (testing "Transforming a date object into a date string with a format."
    (is (= (data/date-obj->date-str (time/date-time 1991 12 31) "M/d/YYYY")
           "12/31/1991"))
    (is (= (data/date-obj->date-str (time/date-time 2010 3 4) "M/d/YYYY")
           "3/4/2010"))))

(deftest test-transform-dates
  (testing "Replace all date strings with date objects or vice versa."
    (is (= (data/transform-dates
            [{:date "5/4/1997"}{:date "12/5/2010"}] :date "M/d/YYYY")
           [{:date (time/date-time 1997 5 4)}
            {:date (time/date-time 2010 12 5)}]))
    (is (= (data/transform-dates
            [{:date (time/date-time 1997 5 4)}
             {:date (time/date-time 2010 12 5)}] :date "M/d/YYYY")
           [{:date "5/4/1997"}
            {:date "12/5/2010"}]))))

;;; Extraction Tests

(def comma-delim-string
  "LastName,FirstName,Gender,FavoriteColor,DateOfBirth\r\nWest,Jerry,Male,Blue,11/17/1991\r\nJean,Billie,Female,Red,9/4/1934")

(def pipe-delim-string
  "LastName|FirstName|Gender|FavoriteColor|DateOfBirth\r\nJohnson|Chad|Male|Blue|12/31/1991\r\nRooney|Mara|Female|Red|9/4/1989")

(def space-delim-string
  "LastName  FirstName Gender    FavoriteColor  DateOfBirth\r\nOrlando   Sam       Male      Blue               12/31/1991\r\nJones     Amanda    Female    Red                  9/4/1989")

(deftest test-delim-str->maps
  (testing "Testing parsing accuracy for the three types of delimiters
            as well as proper building of the final data structure."
    (is (= (data/delim-str->maps comma-delim-string #",")
           [{:LastName "West"
             :FirstName "Jerry"
             :Gender "Male"
             :FavoriteColor "Blue"
             :DateOfBirth (time/date-time 1991 11 17)}
            {:LastName "Jean"
             :FirstName "Billie"
             :Gender "Female"
             :FavoriteColor "Red"
             :DateOfBirth (time/date-time 1934 9 4)}]))
    (is (= (data/delim-str->maps pipe-delim-string #"\|")
           [{:LastName "Johnson"
             :FirstName "Chad"
             :Gender "Male"
             :FavoriteColor "Blue"
             :DateOfBirth (time/date-time 1991 12 31)}
            {:LastName "Rooney"
             :FirstName "Mara"
             :Gender "Female"
             :FavoriteColor "Red"
             :DateOfBirth (time/date-time 1989 9 4)}]))
    (is (= (data/delim-str->maps space-delim-string #"\s+")
           [{:LastName "Orlando"
             :FirstName "Sam"
             :Gender "Male"
             :FavoriteColor "Blue"
             :DateOfBirth (time/date-time 1991 12 31)}
            {:LastName "Jones"
             :FirstName "Amanda"
             :Gender "Female"
             :FavoriteColor "Red"
             :DateOfBirth (time/date-time 1989 9 4)}]))))

(deftest test-extract-person-file
  (with-redefs [config/file-codec {"test_person_comma.csv" #","}
                config/person-file-directory "./resources/ingest-files-test/"]
    (testing "Testing correctly finding a file and slurping it."
      (is (= (data/extract-person-file "test_person_comma.csv")
             [{:LastName "Gordon"
               :FirstName "Jeff"
               :Gender "Male"
               :FavoriteColor "Red"
               :DateOfBirth (time/date-time 1969 6 14)}
              {:LastName "Lively"
               :FirstName "Blake"
               :Gender "Female"
               :FavoriteColor "Green"
               :DateOfBirth (time/date-time 1977 8 27)}
              {:LastName "Stark"
               :FirstName "Arya"
               :Gender "Female"
               :FavoriteColor "Yellow"
               :DateOfBirth (time/date-time 1974 7 22)}])))))

(deftest test-extract-all-files
  (with-redefs [config/file-codec {"test_person_comma.csv" #","
                                   "test_person_pipe.csv" #"\|"
                                   "test_person_space.csv" #"\s+"}
                config/person-file-directory "./resources/ingest-files-test/"]
    (testing "Testing combining records from multiple files."
      (is (= (data/extract-all-files ["test_person_comma.csv"
                                      "test_person_pipe.csv"
                                      "test_person_space.csv"])
             [;; comma.csv
              {:LastName "Gordon"
               :FirstName "Jeff"
               :Gender "Male"
               :FavoriteColor "Red"
               :DateOfBirth (time/date-time 1969 6 14)}
              {:LastName "Lively"
               :FirstName "Blake"
               :Gender "Female"
               :FavoriteColor "Green"
               :DateOfBirth (time/date-time 1977 8 27)}
              {:LastName "Stark"
               :FirstName "Arya"
               :Gender "Female"
               :FavoriteColor "Yellow"
               :DateOfBirth (time/date-time 1974 7 22)}
              ;; pipe.csv
              {:LastName "Flacco"
               :FirstName "Joe"
               :Gender "Male"
               :FavoriteColor "Red"
               :DateOfBirth (time/date-time 1969 6 14)}
              {:LastName "Petro"
               :FirstName "Laura"
               :Gender "Female"
               :FavoriteColor "Green"
               :DateOfBirth (time/date-time 1977 8 27)}
              {:LastName "Adams"
               :FirstName "Amy"
               :Gender "Female"
               :FavoriteColor "Green"
               :DateOfBirth (time/date-time 1974 7 22)}
              ;; space.csv
              {:LastName "Allen"
               :FirstName "Tim"
               :Gender "Male"
               :FavoriteColor "Red"
               :DateOfBirth (time/date-time 1969 6 14)}
              {:LastName "Williams"
               :FirstName "Serena"
               :Gender "Female"
               :FavoriteColor "Green"
               :DateOfBirth (time/date-time 1977 8 27)}
              {:LastName "Williams"
               :FirstName "Venus"
               :Gender "Female"
               :FavoriteColor "Green"
               :DateOfBirth (time/date-time 1974 7 22)}])))))

;;; Output Tests

(deftest test-sort-gender-lastname
  (testing "Sort by :Gender then :LastName."
    (is (= (data/sort-gender-lastname
            [{:Gender "Male" :LastName "Orlando"}
             {:Gender "Female" :LastName "Jones"}
             {:Gender "Male" :LastName "Arty"}])
           (seq [{:Gender "Female" :LastName "Jones"}
                 {:Gender "Male" :LastName "Arty"}
                 {:Gender "Male" :LastName "Orlando"}])))))

(deftest test-sort-dateofbirth
  (testing "Sort by :DateOfBirth."
    (is (= (data/sort-dateofbirth
            [{:DateOfBirth (time/date-time 1974 7 22) :LastName "Orlando"}
             {:DateOfBirth (time/date-time 2012 4 7) :LastName "Jones"}
             {:DateOfBirth (time/date-time 1974 6 10) :LastName "Arty"}])
           (seq [{:DateOfBirth (time/date-time 1974 6 10) :LastName "Arty"}
                 {:DateOfBirth (time/date-time 1974 7 22) :LastName "Orlando"}
                 {:DateOfBirth (time/date-time 2012 4 7) :LastName "Jones"}])))))

(deftest test-sort-lastname-descending
  (testing "Sort by :LastName descending."
    (is (= (data/sort-lastname-descending
            [{:LastName "Orlando" :FirstName "Sam"}
             {:LastName "Archibald" :FirstName "Francis"}
             {:LastName "Zion" :FirstName "Jerry"}])
           (seq [{:LastName "Zion" :FirstName "Jerry"}
                 {:LastName "Orlando" :FirstName "Sam"}
                 {:LastName "Archibald" :FirstName "Francis"}])))))
