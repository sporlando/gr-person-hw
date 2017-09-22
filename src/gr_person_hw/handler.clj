(ns gr-person-hw.handler
  (:require [gr-person-hw.response :as response]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.defaults :refer [api-defaults]]))

(defroutes app-routes
  (GET "/" [] "Home Page")
  (POST "/records" request (response/create-record request))
  (GET "/records/gender" [] (response/get-records :Gender))
  (GET "/records/birthdate" [] (response/get-records :DateOfBirth))
  (GET "/records/name" [] (response/get-records :LastName))
  (route/not-found "Not Found"))

(def app
  (wrap-json-body app-routes api-defaults))
