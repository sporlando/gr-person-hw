(ns gr-person-hw.core
  (:require [gr-person-hw.config :as config]
            [gr-person-hw.data :as data]
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]))

(defn usage
  "Print usage summary."
  [options-summary]
  (->> ["Extract Files and display outputs."
        ""
        "Options:"
        options-summary
        "Please refer to your local developer for more information."]
       (str/join \newline)))

(defn error-msg
  "Print errors due to parsing incorrect commands."
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn exit
  "Exit the process using the appropriate status."
  [status msg]
  (println msg)
  (System/exit status))

(defn in?
  "Retun true if seq contains elm, nil if not."
  [seq elm]
  (some #(= elm %) seq))

(defn concat-file-names
  "Concat all file names into one array for a key in a map."
  [m k v]
  (if (contains? m k)
    (assoc m k (into [] (concat (get m k) [v])))
    (assoc m k [v])))

(def file-names (keys config/file-codec))

(def cli-options
  "Provide a list of cli options."
  [["-F" "--all-files" "Extract all files."]
   ["-f" "--file FILE NAME" "Extract file by name. Repeat for multiple files."
    :validate [#(in? file-names %)
               (apply str "Valid file names: " (str/join ", " file-names))]
    :assoc-fn concat-file-names]
   ["-h" "--help"]])

(defn -main
  "The main function used to parse command line options and run the program.
   From the top level directory of the project type: 'lein run -option (arg)'.
   For multiple files: 'lein run -f filename1 -f filename2 -f filename3'."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:all-files options) (exit 0 (data/output-all
                                    (data/extract-all-files file-names)))
      (:file options) (exit 0 (data/output-all
                               (data/extract-all-files (:file options))))
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))))
