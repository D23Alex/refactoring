(ns stats.routes
  (:require [clojure.algo.generic.functor :as f]
            [clojure.pprint]
            [clojure.walk]
            [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [stats.cache :refer [cached-fn]]
            [stats.stats :refer [get-averages get-efficiency]]
            [stats.config :refer [conf]]))

(def resource-urls (f/fmap #(str (conf :base-url) "/" %)
                           {:averages "averages"
                            :efficiency "efficiency"}))

(defn as-integer-coll [s]
  (if (coll? s) (map #(Integer. %) s) (if s #{(Integer. s)})))

(compojure/defroutes app
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
