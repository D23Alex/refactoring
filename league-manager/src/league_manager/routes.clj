(ns league-manager.routes
  (:require [clojure.pprint]
            [clojure.algo.generic.functor :as f]
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
            [league-manager.util :refer [parse-date]]
            [league-manager.config :refer [conf]]))

(def resource-urls (f/fmap #(str (conf :base-url) "/" %)
                           {:players "players"
                            :contracts "contracts"
                            :lineup-occurrences "lineup-occurrences"}))

(defn as-integer-coll [s]
  (if (coll? s) (map #(Integer. %) s) (if s #{(Integer. s)})))

(compojure/defroutes app
                     (compojure/GET (resource-urls :lineup-occurrences)
                                    [seasons teams players games]
                       {:status 200
                        :body (get-all-lineup-occurrences (as-integer-coll seasons)
                                                          (as-integer-coll teams)
                                                          (as-integer-coll players)
                                                          (as-integer-coll games))})
                     (compojure/GET (str (resource-urls :contracts) ":contract-id/transfer")
                                    [contract-id team-id transfer-date]
                       {:status 200
                        :body (transfer (Integer. contract-id) (Integer. team-id) (parse-date transfer-date))})
                     (compojure/GET (str (resource-urls :contracts) "/:contract-id/terminate")
                                    [contract-id termination-date]
                       {:status 200
                        :body (terminate (Integer. contract-id) (parse-date termination-date))})
                     (compojure/GET (str (resource-urls :players) "/by-team/:team-id")
                                    [team-id date]
                       {:status 200
                        :body (team-members (Integer. team-id) (parse-date date))})
                     (compojure/GET (str (conf :base-url) "/:resource")
                                    [resource]
                       {:status 200 :body (get-all-resources (keyword resource))})
                     (compojure/GET (str (conf :base-url) "/:resource/:id")
                                    [resource id]
                       {:status 200 :body (get-resource (keyword resource) (Integer. id))})
                     (compojure/POST (str (conf :base-url) "/:resource")
                                     [resource :as {body :body}]
                       {:status 201 :body (create-resource (keyword resource) body)})
                     (compojure/PUT (str (conf :base-url) "/:resource/:id")
                                    [resource id :as {body :body}]
                       {:status 200 :body (update-resource (keyword resource) (Integer. id) body)})
                     (compojure/DELETE (str (conf :base-url) "/:resource/:id")
                                       [resource id]
                       {:status 204 :body (delete-resource (keyword resource) (Integer. id))})
                     (compojure-route/not-found "Page not found"))