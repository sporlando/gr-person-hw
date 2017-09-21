(ns gr-person-hw.data
  (:require [gr-person-hw.config :as config :only [datafile-directory]]
            [clojure.string :as str]
            [clj-time.format :as tf]))

(defn delim-str->maps
  "Extract a delimited string (e.g. slurped from a file) into a vector of maps
   which have the file header names as keys. Dates strings are also transformed
   into date objects."
  [data delim-re]
  (let [data (map #(str/split % delim-re) (str/split-lines data))
        header (first data)]
    ;; Converting to date objects will help with comparisons later on. These
    ;; objects will get (tf/unparse) to display properly.
    (mapv #(assoc % "DateOfBirth" (tf/parse (tf/formatter config/date-format)
                                            (get % "DateOfBirth")))
          (map #(zipmap header %) (rest data)))))

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
