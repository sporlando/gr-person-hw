(ns gr-person-hw.data
  (:require [gr-person-hw.config :as config :only [datafile-directory]]
            [clojure.pprint :refer [pprint print-table]]
            [clojure.string :as str]
            [clj-time.format :as tf]))

;;; Utility

(defn date-str->date-obj
  "Transforms a date string into a date object with a given format."
  [date-str format]
  (tf/parse (tf/formatter format) date-str))

(defn date-obj->date-str
  "Transforms a date object into a date string with a given format."
  [date-obj format]
  (tf/unparse (tf/formatter format) date-obj))

(defn transform-dates
  "Either replaces all date objects from a collection of maps with date strings
   OR replaces all date strings from a collection with date objects.
   The first item of the :date-field is checked and that determines the
   transformation."
  [coll date-field format]
  (let [date-fn (if (string? (date-field (first coll)))
                  (partial date-str->date-obj) (partial date-obj->date-str))]
    (mapv #(assoc % date-field
                    (date-fn (date-field %) format)) coll)))

(defn print-pretty
  "Uses print-table to display the given data."
  [data]
  (print-table data))

;;; Extraction

(defn delim-str->maps
  "Extract a delimited string (e.g. slurped from a file) into a vector of maps
   which have the file header names as keys. Dates strings are also transformed
   into date objects."
  [data delim-re]
  (let [data (map #(str/split % delim-re) (str/split-lines data))
        header (map keyword (first data))]
    (transform-dates
     (map #(zipmap header %) (rest data)) :DateOfBirth config/date-format)))

(defn extract-person-file
  "Loads a person file from the filesystem and returns the parsed data."
  [filename]
  (as-> (str config/person-file-directory filename) $
    (slurp $)
    (delim-str->maps $ (get config/file-codec filename))))

(defn extract-all-files
  "Uses the file-codec to extract each person file into a vector of maps, then
   combines those vectors into a single vector."
  [filenames]
  (into [] (apply concat
                  (for [file filenames]
                    (extract-person-file file)))))

;;; Output

(defn sort-gender-lastname
  "Sorts the data by :Gender (female before male), then by :LastName ascending."
  [data]
  (sort-by (juxt :Gender :LastName) data))

(defn sort-dateofbirth
  "Sorts the data by :DateOfBirth ascending."
  [data]
  (sort-by :DateOfBirth data))

(defn sort-lastname-descending
  "Sorts the data by :LastName descending."
  [data]
  (sort-by :LastName #(compare %2 %1) data))

(defn output-all
  "Displays all defined outputs."
  [data]
  (let [output1 {:title "By :Gender then :LastName ascending"
                 :output (sort-gender-lastname data)}
        output2 {:title "By :DateOfBirth"
                 :output (sort-dateofbirth data)}
        output3 {:title "By :LastName descending"
                 :output (sort-lastname-descending data)}
        all-outputs [output1 output2 output3]]
    (for [output all-outputs]
      (do
        (println \newline)
        (pprint (:title output))
        (print-pretty
         (transform-dates (:output output) :DateOfBirth config/date-format))))))
