(ns scorekeeper.routes
  (:require [clojure.pprint]
            [clojure.algo.generic.functor :as f]
            [clojure.walk]
            [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [scorekeeper.events :refer [event-names get-events]]
            [scorekeeper.postgres :refer [create-resource get-event]]
            [scorekeeper.config :refer [conf]]))

(def event-by-url-name (map-invert event-names))

(defn event-type-valid? [s]
  (contains? (vals event-names) s))

(def resource-full-urls (f/fmap #(str (conf :base-url) "/" %)
                                {:events "events"
                                 :event-cancellations "event-cancellations"}))

(defn as-integer-coll [s]
  (if (coll? s) (map #(Integer. %) s) (if s #{(Integer. s)} #{})))

(compojure/defroutes app
                     (compojure/GET (resource-full-urls :events)
                                    [event-types seasons teams players games include-cancelled]
                       {:status 200
                        :body (get-events (map event-by-url-name event-types)
                                          (as-integer-coll seasons)
                                          (as-integer-coll teams)
                                          (as-integer-coll players)
                                          (as-integer-coll games)
                                          include-cancelled)})
                     (compojure/GET (str (resource-full-urls :events) "/:event-type")
                                    [event-type seasons teams players games include-cancelled]
                       (if (event-type-valid? event-type)
                         {:status 200
                          :body (get-events [(event-by-url-name event-type)]
                                            (as-integer-coll seasons)
                                            (as-integer-coll teams)
                                            (as-integer-coll players)
                                            (as-integer-coll games)
                                            include-cancelled)}
                         {:status 400}))
                     (compojure/GET (str (resource-full-urls :events) "/:event-type/season/:season-id")
                                    [event-type season-id teams players games include-cancelled]
                       (if (event-type-valid? event-type)
                         {:status 200
                          :body (get-events [(event-by-url-name event-type)]
                                            (as-integer-coll season-id)
                                            (as-integer-coll teams)
                                            (as-integer-coll players)
                                            (as-integer-coll games)
                                            include-cancelled)}
                         {:status 400}))
                     (compojure/GET (str (resource-full-urls :events) "/:event-type/team/:team-id")
                                    [event-type seasons team-id players games include-cancelled]
                       (if (event-type-valid? event-type)
                         {:status 200
                          :body (get-events [(event-by-url-name event-type)]
                                            (as-integer-coll seasons)
                                            (as-integer-coll team-id)
                                            (as-integer-coll players)
                                            (as-integer-coll games)
                                            include-cancelled)}
                         {:status 400}))
                     (compojure/GET (str (resource-full-urls :events) "/:event-type/player/:player-id")
                                    [event-type seasons teams player-id games include-cancelled]
                       (if (event-type-valid? event-type)
                         {:status 200
                          :body (get-events [(event-by-url-name event-type)]
                                            (as-integer-coll seasons)
                                            (as-integer-coll teams)
                                            (as-integer-coll player-id)
                                            (as-integer-coll games)
                                            include-cancelled)}
                         {:status 400}))
                     (compojure/GET (str (resource-full-urls :events) "/:event-type/game/:game-id")
                                    [event-type seasons teams players game-id include-cancelled]
                       (if (event-type-valid? event-type)
                         {:status 200
                          :body (get-events [(event-by-url-name event-type)]
                                            (as-integer-coll seasons)
                                            (as-integer-coll teams)
                                            (as-integer-coll players)
                                            (as-integer-coll game-id)
                                            include-cancelled)}
                         {:status 400}))
                     (compojure/GET (str (resource-full-urls :events) "/:event-type/player/:player-id/game/:game-id")
                                    [event-type seasons teams player-id game-id include-cancelled]
                       (if (event-type-valid? event-type)
                         {:status 200
                          :body (get-events [(event-by-url-name event-type)]
                                            (as-integer-coll seasons)
                                            (as-integer-coll teams)
                                            (as-integer-coll player-id)
                                            (as-integer-coll game-id)
                                            include-cancelled)}
                         {:status 400}))
                     (compojure/GET (str (resource-full-urls :events) "/:event-type/team/:team-id/game/:game-id")
                                    [event-type seasons team-id players game-id include-cancelled]
                       (if (event-type-valid? event-type)
                         {:status 200
                          :body (get-events [(event-by-url-name event-type)]
                                            (as-integer-coll seasons)
                                            (as-integer-coll team-id)
                                            (as-integer-coll players)
                                            (as-integer-coll game-id)
                                            include-cancelled)}
                         {:status 400}))
                     (compojure/GET (str (resource-full-urls :events) "/:event-type/season/:season-id/team/:team-id")
                                    [event-type season-id team-id players games include-cancelled]
                       (if (event-type-valid? event-type)
                         {:status 200
                          :body (get-events [(event-by-url-name event-type)]
                                            (as-integer-coll season-id)
                                            (as-integer-coll team-id)
                                            (as-integer-coll players)
                                            (as-integer-coll games)
                                            include-cancelled)}
                         {:status 400}))
                     (compojure/GET (str (resource-full-urls :events) "/:event-type/season/:season-id/player/:player-id")
                                    [event-type season-id teams player-id games include-cancelled]
                       (if (event-type-valid? event-type)
                         {:status 200
                          :body (get-events [(event-by-url-name event-type)]
                                            (as-integer-coll season-id)
                                            (as-integer-coll teams)
                                            (as-integer-coll player-id)
                                            (as-integer-coll games)
                                            include-cancelled)}
                         {:status 400}))
                     (compojure/GET (str (resource-full-urls :events) "/:event-type/:id")
                                    [event-type id]
                       {:status 200
                        :body (get-event event-type id)})
                     (compojure/POST (resource-full-urls :event-cancellations)
                                     [:as {body :body}]
                       {:status 201
                        :body (create-resource :event-cancellations body)})
                     (compojure-route/not-found "Page not found"))
