(ns league-manager.routes
  (:require [clojure.pprint]
            [clojure.set :refer [map-invert]]
            [clojure.algo.generic.functor :as f]
            [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [league-manager.postgres :refer [table-names
                                             create-resource
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

(def resource-url-names {:players "players"
                         :contracts "contracts"
                         :lineup-occurrences "lineup-occurrences"
                         :teams "teams"
                         :seasons "seasons"
                         :games "games"})

(def resource-by-url-name (map-invert resource-url-names))

(defn resource-valid? [resource]
  (contains? (resource-url-names vals) resource))

(def resource-full-urls (f/fmap #(str (conf :base-url) "/" %)
                                resource-url-names))

(defn as-integer-coll [s]
  (if (coll? s) (map #(Integer. %) s) (if s #{(Integer. s)})))

(compojure/defroutes app
                     (compojure/GET (resource-full-urls :lineup-occurrences)
                                    [seasons teams players games]
                       {:status 200
                        :body (get-all-lineup-occurrences (as-integer-coll seasons)
                                                          (as-integer-coll teams)
                                                          (as-integer-coll players)
                                                          (as-integer-coll games))})
                     (compojure/GET (str (resource-full-urls :contracts) "/:contract-id/transfer")
                                    [contract-id team-id transfer-date]
                       {:status 200
                        :body (transfer (Integer. contract-id) (Integer. team-id) (parse-date transfer-date))})
                     (compojure/GET (str (resource-full-urls :contracts) "/:contract-id/terminate")
                                    [contract-id termination-date]
                       {:status 200
                        :body (terminate (Integer. contract-id) (parse-date termination-date))})
                     (compojure/GET (str (resource-full-urls :players) "/by-team/:team-id")
                                    [team-id date]
                       {:status 200
                        :body (team-members (Integer. team-id) (parse-date date))})
                     (compojure/GET (str (conf :base-url) "/:resource")
                                    [resource]
                       (if (resource-valid? resource)
                         {:status 200
                          :body (get-all-resources (resource-by-url-name resource))}
                         {:status 400}))
                     (compojure/GET (str (conf :base-url) "/:resource/:id")
                                    [resource id]
                       (if (resource-valid? resource)
                         {:status 200
                          :body (get-resource (resource-by-url-name resource) (Integer. id))}
                         {:status 404}))
                     (compojure/POST (str (conf :base-url) "/:resource")
                                     [resource :as {body :body}]
                       (if (resource-valid? resource)
                         {:status 201
                          :body (create-resource (resource-by-url-name resource) body)}
                         {:status 404}))
                     (compojure/PUT (str (conf :base-url) "/:resource/:id")
                                    [resource id :as {body :body}]
                       (if (resource-valid? resource)
                         {:status 200
                          :body (update-resource (resource-by-url-name resource) (Integer. id) body)}
                         {:status 404}))
                     (compojure/DELETE (str (conf :base-url) "/:resource/:id")
                                       [resource id]
                       (if (resource-valid? resource)
                         {:status 204
                          :body (delete-resource (resource-by-url-name resource) (Integer. id))}
                         {:status 404}))
                     (compojure-route/not-found "Page not found"))
