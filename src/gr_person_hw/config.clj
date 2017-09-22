(ns gr-person-hw.config)

(def person-file-directory "./resources/ingest-files/")

(def header [:LastName :FirstName :Gender :FavoriteColor :DateOfBirth])

(def file-codec
  {"person_comma_delim.csv" #","
   "person_pipe_delim.csv" #"\|"
   "person_space_delim.csv" #"\s+"})

(def date-format "M/d/YYYY")
