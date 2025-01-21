(ns stats.stats
  (:require [stats.config :refer [conf]]
            [stats.http :refer [http-get]]))

(def apis {:totals (str (conf :statsheets-api-url) "/totals")})

(defn get-totals [seasons teams players games]
  (http-get (apis :totals) {:seasons seasons
                            :teams teams
                            :players players
                            :games games}))

(defn calculate-averages [totals count]
  (into {} (map (fn [[k v]] [k (/ v count)]) totals)))

(defn get-averages [seasons teams players games]
  (let [totals (get-totals seasons teams players games)
        player-count (count (:player-ids totals))
        game-count (count (:game-ids totals))
        team-count (count (:team-ids totals))
        season-count (count (:season-ids totals))
        stats (dissoc totals :player-ids :game-ids :team-ids :season-ids)]
    {:per-player (calculate-averages stats player-count)
     :per-game (calculate-averages stats game-count)
     :per-team (calculate-averages stats team-count)
     :per-season (calculate-averages stats season-count)}))


(defn get-efficiency [seasons teams players games]
  (let [totals (get-totals seasons teams players games)]
    {:two-point (/ (totals :two-pointers-made) (totals :two-pointers-attempted))
     :three-point (/ (totals :three-pointers-made) (totals :three-pointers-attempted))
     :free-throw (/ (totals :free-throws-made) (totals :free-throws-attempted))
     :field-goal (/ (+ (totals :two-pointers-made) (totals :three-pointers-made))
                    (+ (totals :two-pointers-attempted) (totals :three-pointers-attempted)))
     :effective-field-goal (/ (+ (totals :two-pointers-made) (-> totals :three-pointers-made (* 3)))
                              (+ (totals :two-pointers-attempted) (totals :three-pointers-attempted)))}))
