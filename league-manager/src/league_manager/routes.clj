(ns league-manager.routes
  (:require [clojure.pprint]
            [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [league-manager.postgres :refer [create-resource
                                             delete-resource
                                             get-all-lineup-occurrences
                                             get-all-resources
                                             get-resource
                                             team-members
                                             terminate
                                             transfer
                                             update-resource]]
            [league-manager.util :refer [parse-date]]))


(defn as-integer-coll [s]
  (if (coll? s) (map #(Integer. %) s) (if s #{(Integer. s)})))

(compojure/defroutes app
                     (compojure/GET "/api/lineup-occurrences" [seasons teams players games]
                       {:status 200
                        :body (get-all-lineup-occurrences (as-integer-coll seasons)
                                                          (as-integer-coll teams)
                                                          (as-integer-coll players)
                                                          (as-integer-coll games))})
                     (compojure/GET "/contracts/:contract-id/transfer" [contract-id team-id transfer-date]
                       {:status 200
                        :body (transfer (Integer. contract-id) (Integer. team-id) (parse-date transfer-date))})
                     (compojure/GET "/contracts/:contract-id/terminate" [contract-id termination-date]
                       {:status 200
                        :body (terminate (Integer. contract-id) (parse-date termination-date))})
                     (compojure/GET "/players/by-team/:team-id" [team-id date]
                       {:status 200
                        :body (team-members (Integer. team-id) (parse-date date))})
                     (compojure/GET "/:resource" [resource]
                       {:status 200 :body (get-all-resources (keyword resource))})
                     (compojure/GET "/:resource/:id" [resource id]
                       {:status 200 :body (get-resource (keyword resource) (Integer. id))})
                     (compojure/POST "/:resource" [resource :as {body :body}]
                       {:status 201 :body (create-resource (keyword resource) body)})
                     (compojure/PUT "/:resource/:id" [resource id :as {body :body}]
                       {:status 200 :body (update-resource (keyword resource) (Integer. id) body)})
                     (compojure/DELETE "/:resource/:id" [resource id]
                       {:status 204 :body (delete-resource (keyword resource) (Integer. id))})
                     (compojure-route/not-found "Page not found"))