(ns scorekeeper.totals
  (:require [scorekeeper.http :refer [http-get]]
            [scorekeeper.config :refer [conf]]
            [scorekeeper.mongo :refer [get-statsheet]]))

(def apis {:lineup-occurrences (str (conf :league-manager-api-url) "/lineup-occurrences")})

(defn player-performance [team jersey-number statsheet]
  (reduce (fn [acc [k v]]
            (if (and (map? v) (contains? v team))
              (let [team-stats (get v team)]
                (if (contains? team-stats jersey-number)
                  (assoc acc k (get team-stats jersey-number))
                  acc))
              acc))
          {}
          statsheet))

(defn add-totals [& totals]
  (reduce (fn [acc total]
            (merge-with + acc total))
          {}
          totals))

(defn get-totals [seasons teams players games]
  (let [lineup-occurrences (http-get (apis :lineup-occurrences)
                                     {:seasons (-> seasons set vec)
                                      :teams (-> teams set vec)
                                      :players (-> players set vec)
                                      :games (-> games set vec)})
        performance-for-each (map #(player-performance (-> % :team keyword)
                                                       (-> % :jersey_number str keyword)
                                                       (get-statsheet {:game-id (% :game_id)}))
                                  lineup-occurrences)
        totals (apply add-totals performance-for-each)]
    (assoc totals :game-ids (set (map :game_id lineup-occurrences))
                  :player-ids (set (map :player_id lineup-occurrences))
                  :team-ids (set (map :team_id lineup-occurrences))
                  :season-ids (set (map :season_id lineup-occurrences)))))