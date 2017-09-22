(ns gr-person-hw.response
  (:require [gr-person-hw.config :as config]
            [gr-person-hw.data :as data]
            [cheshire.core :as cheshire]
            [ring.util.request :as rr]))

(defn json-200
  "Wraps response data in a JSON compatible format."
  [to-render]
  (let [pretty-print (cheshire/create-pretty-printer
                      (assoc cheshire/default-pretty-print-options
                             :indent-arrays? true))]
        {:status 200
         :headers {"Content-Type" "text/json; charset=utf-8"}
         :body (cheshire/generate-string to-render {:pretty pretty-print})}))

(defn parse-body
  "Converts the body of a request into a clojure object or data structure."
  [body]
  (cheshire/parse-string (slurp body) true))

(defn get-records
  "Gets all data records from both files and storage, then returns all
   records in a JSON response."
  [sort-field]
  (as-> (data/extract-all-files (keys config/file-codec)) $
    (into $ @data/person-data)
    (sort-by sort-field $)
    (data/transform-dates $ :DateOfBirth config/date-format)
    (json-200 $)))

(defn create-record
  "Create a new data record and add it to the 'persistent' dataset. Returns
   a JSON response of the newly created record."
  [request]
  (let [body (parse-body (:body request))
        new-record (data/delim-str->maps body #",|\||\s+" config/header)
        created-records (swap! data/person-data into new-record)]
    (as-> new-record $
      (data/transform-dates $ :DateOfBirth config/date-format)
      (json-200 $))))
