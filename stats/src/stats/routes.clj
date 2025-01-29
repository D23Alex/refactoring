(ns stats.routes
  (:require [clojure.algo.generic.functor :as f]
            [clojure.pprint]
            [clojure.walk]
            [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [stats.cache :refer [cached-fn]]
            [stats.basic-stats :refer [get-totals]]
            [stats.advanced-stats :refer [get-averages get-efficiency get-time-played get-plus-minus]]
            [stats.config :refer [conf]]))

(def resource-urls (f/fmap #(str (conf :base-url) "/" %)
                           {:totals "totals"
                            :time-played "time-played"
                            :plus-minus "plus-minus"
                            :averages "averages"
                            :efficiency "efficiency"}))

(defn as-integer-coll [s]
  (if (coll? s) (map #(Integer. %) s) (if s #{(Integer. s)})))

(compojure/defroutes app
                     (compojure/GET (str (resource-urls :time-played) "/player/:player-id/game/:game-id")
                                    [player-id game-id]
                       {:status 200
                        :body ((cached-fn get-time-played) (Integer. player-id) (Integer. game-id))})
                     (compojure/GET (str (resource-urls :plus-minus) "/player/:player-id/game/:game-id")
                                    [player-id game-id]
                       {:status 200
                        :body ((cached-fn get-plus-minus) (Integer. player-id) (Integer. game-id))})
                     (compojure/GET (resource-urls :totals)
                                    [seasons teams players games]
                       {:status 200
                        :body (->> [seasons teams players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/season/:season-id")
                                    [season-id teams players games]
                       {:status 200
                        :body (->> [season-id teams players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/team/:team-id")
                                    [seasons team-id players games]
                       {:status 200
                        :body (->> [seasons team-id players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/player/:player-id")
                                    [seasons teams player-id games]
                       {:status 200
                        :body (->> [seasons teams player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/game/:game-id")
                                    [seasons teams players game-id]
                       {:status 200
                        :body (->> [seasons teams players game-id]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/season/:season-id/team/:team-id")
                                    [season-id team-id players games]
                       {:status 200
                        :body (->> [season-id team-id players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/season/:season-id/player/:player-id")
                                    [season-id teams player-id games]
                       {:status 200
                        :body (->> [season-id teams player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/team/:team-id/player/:player-id")
                                    [seasons team-id player-id games]
                       {:status 200
                        :body (->> [seasons team-id player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/team/:team-id/game/:game-id")
                                    [seasons team-id players game-id]
                       {:status 200
                        :body (->> [seasons team-id players game-id]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/player/:player-id/game/:game-id")
                                    [seasons teams player-id game-id]
                       {:status 200
                        :body (->> [seasons teams player-id game-id]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (str (resource-urls :totals) "/season/:season-id/team/:team-id/player/:player-id")
                                    [season-id team-id player-id games]
                       {:status 200
                        :body (->> [season-id team-id player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-totals)))})
                     (compojure/GET (resource-urls :averages)
                                    [seasons teams players games]
                       {:status 200
                        :body (->> [seasons teams players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/season/:season-id")
                                    [season-id teams players games]
                       {:status 200
                        :body (->> [season-id teams players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/team/:team-id")
                                    [seasons team-id players games]
                       {:status 200
                        :body (->> [seasons team-id players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/player/:player-id")
                                    [seasons teams player-id games]
                       {:status 200
                        :body (->> [seasons teams player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/game/:game-id")
                                    [seasons teams players game-id]
                       {:status 200
                        :body (->> [seasons teams players game-id]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/season/:season-id/team/:team-id")
                                    [season-id team-id players games]
                       {:status 200
                        :body (->> [season-id team-id players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/season/:season-id/player/:player-id")
                                    [season-id teams player-id games]
                       {:status 200
                        :body (->> [season-id teams player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/team/:team-id/player/:player-id")
                                    [seasons team-id player-id games]
                       {:status 200
                        :body (->> [seasons team-id player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/team/:team-id/game/:game-id")
                                    [seasons team-id players game-id]
                       {:status 200
                        :body (->> [seasons team-id players game-id]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/player/:player-id/game/:game-id")
                                    [seasons teams player-id game-id]
                       {:status 200
                        :body (->> [seasons teams player-id game-id]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (str (resource-urls :averages) "/season/:season-id/team/:team-id/player/:player-id")
                                    [season-id team-id player-id games]
                       {:status 200
                        :body (->> [season-id team-id player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-averages)))})
                     (compojure/GET (resource-urls :efficiency)
                                    [seasons teams players games]
                       {:status 200
                        :body (->> [seasons teams players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/season/:season-id")
                                    [season-id teams players games]
                       {:status 200
                        :body (->> [season-id teams players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/team/:team-id")
                                    [seasons team-id players games]
                       {:status 200
                        :body (->> [seasons team-id players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/player/:player-id")
                                    [seasons teams player-id games]
                       {:status 200
                        :body (->> [seasons teams player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/game/:game-id")
                                    [seasons teams players game-id]
                       {:status 200
                        :body (->> [seasons teams players game-id]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/season/:season-id/team/:team-id")
                                    [season-id team-id players games]
                       {:status 200
                        :body (->> [season-id team-id players games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/season/:season-id/player/:player-id")
                                    [season-id teams player-id games]
                       {:status 200
                        :body (->> [season-id teams player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/team/:team-id/player/:player-id")
                                    [seasons team-id player-id games]
                       {:status 200
                        :body (->> [seasons team-id player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/team/:team-id/game/:game-id")
                                    [seasons team-id players game-id]
                       {:status 200
                        :body (->> [seasons team-id players game-id]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/player/:player-id/game/:game-id")
                                    [seasons teams player-id game-id]
                       {:status 200
                        :body (->> [seasons teams player-id game-id]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure/GET (str (resource-urls :efficiency) "/season/:season-id/team/:team-id/player/:player-id")
                                    [season-id team-id player-id games]
                       {:status 200
                        :body (->> [season-id team-id player-id games]
                                   (map as-integer-coll)
                                   (apply (cached-fn get-efficiency)))})
                     (compojure-route/not-found "Page not found"))
