(ns scorekeeper.routes
  (:require [clojure.pprint]
            [clojure.walk]
            [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [scorekeeper.totals :refer [get-totals]]
            [scorekeeper.mongo :refer [get-statsheet delete-statsheet create-statsheet update-statsheet]])
  (:import (org.bson.types ObjectId)))

(defn as-integer-coll [s]
  (if (coll? s) (map #(Integer. %) s) (if s #{(Integer. s)})))

(compojure/defroutes app
                     (compojure/GET "/api/statsheets/totals" [seasons teams players games]
                       {:status 200
                        :body (get-totals (as-integer-coll seasons)
                                          (as-integer-coll teams)
                                          (as-integer-coll players)
                                          (as-integer-coll games))})
                     (compojure/GET "/statsheets/:id" [id]
                       {:status 200
                        :body (get-statsheet {:_id (ObjectId. id)})})
                     (compojure/POST "/statsheets" [:as {body :body}]
                       {:status 201
                        :body (create-statsheet body)})
                     (compojure/PUT "/statsheets/:id" [id :as {body :body}]
                       {:status 200
                        :body (update-statsheet id body)})
                     (compojure/DELETE "/statsheets/:id" [id]
                       {:status 204
                        :body (delete-statsheet id)})
                     (compojure-route/not-found "Page not found"))
