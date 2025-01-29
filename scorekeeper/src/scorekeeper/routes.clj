(ns scorekeeper.routes
  (:require [clojure.pprint]
            [clojure.walk]
            [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [scorekeeper.totals :refer [get-events]]
            [scorekeeper.postgres :refer [get-event]]))

(defn as-integer-coll [s]
  (if (coll? s) (map #(Integer. %) s) (if s #{(Integer. s)} #{})))

(defn as-keyword-coll [s]
  (if (coll? s) (map keyword s) (if s #{(keyword s)} #{})))

(compojure/defroutes app
                     (compojure/GET "/api/scorekeeper/events" [event-types seasons teams players games]
                       {:status 200
                        :body (get-events (as-keyword-coll event-types)
                                          (as-integer-coll seasons)
                                          (as-integer-coll teams)
                                          (as-integer-coll players)
                                          (as-integer-coll games))})
                     (compojure/GET "/api/scorekeeper/events/:event-type" [event-type seasons teams players games]
                       {:status 200
                        :body (get-events (as-keyword-coll event-type)
                                          (as-integer-coll seasons)
                                          (as-integer-coll teams)
                                          (as-integer-coll players)
                                          (as-integer-coll games))})
                     (compojure/GET "/api/scorekeeper/events/:event-type/season/:season-id" [event-type season-id teams players games]
                       {:status 200
                        :body (get-events (as-keyword-coll event-type)
                                          (as-integer-coll season-id)
                                          (as-integer-coll teams)
                                          (as-integer-coll players)
                                          (as-integer-coll games))})
                     (compojure/GET "/api/scorekeeper/events/:event-type/team/:team-id" [event-type seasons team-id players games]
                       {:status 200
                        :body (get-events (as-keyword-coll event-type)
                                          (as-integer-coll seasons)
                                          (as-integer-coll team-id)
                                          (as-integer-coll players)
                                          (as-integer-coll games))})
                     (compojure/GET "/api/scorekeeper/events/:event-type/player/:player-id" [event-type seasons teams player-id games]
                       {:status 200
                        :body (get-events (as-keyword-coll event-type)
                                          (as-integer-coll seasons)
                                          (as-integer-coll teams)
                                          (as-integer-coll player-id)
                                          (as-integer-coll games))})
                     (compojure/GET "/api/scorekeeper/events/:event-type/game/:game-id" [event-type seasons teams players game-id]
                       {:status 200
                        :body (get-events (as-keyword-coll event-type)
                                          (as-integer-coll seasons)
                                          (as-integer-coll teams)
                                          (as-integer-coll players)
                                          (as-integer-coll game-id))})
                     (compojure/GET "/api/scorekeeper/events/:event-type/player/:player-id/game/:game-id" [event-type seasons teams player-id game-id]
                       {:status 200
                        :body (get-events (as-keyword-coll event-type)
                                          (as-integer-coll seasons)
                                          (as-integer-coll teams)
                                          (as-integer-coll player-id)
                                          (as-integer-coll game-id))})
                     (compojure/GET "/api/scorekeeper/events/:event-type/team/:team-id/game/:game-id" [event-type seasons team-id players game-id]
                       {:status 200
                        :body (get-events (as-keyword-coll event-type)
                                          (as-integer-coll seasons)
                                          (as-integer-coll team-id)
                                          (as-integer-coll players)
                                          (as-integer-coll game-id))})
                     (compojure/GET "/api/scorekeeper/events/:event-type/season/:season-id/team/:team-id" [event-type season-id team-id players games]
                       {:status 200
                        :body (get-events (as-keyword-coll event-type)
                                          (as-integer-coll season-id)
                                          (as-integer-coll team-id)
                                          (as-integer-coll players)
                                          (as-integer-coll games))})
                     (compojure/GET "/api/scorekeeper/events/:event-type/season/:season-id/player/:player-id" [event-type season-id teams player-id games]
                       {:status 200
                        :body (get-events (as-keyword-coll event-type)
                                          (as-integer-coll season-id)
                                          (as-integer-coll teams)
                                          (as-integer-coll player-id)
                                          (as-integer-coll games))})
                     (compojure/GET "/api/scorekeeper/events/:event-type/:id" [event-type id]
                       {:status 200
                        :body (get-event event-type id)})
                     (compojure-route/not-found "Page not found"))
