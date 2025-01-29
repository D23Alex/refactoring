(ns scorekeeper.totals
  (:require [scorekeeper.http :refer [http-get]]
            [scorekeeper.config :refer [conf]]
            [scorekeeper.postgres :refer [get-all-events]]))

(def apis {:lineup-occurrences (str (conf :league-manager-api-url) "/lineup-occurrences")})

(def all-event-types #{:rebounds :field_goal_attempts :blocks :steals :assists :after_timeout_court_appearances
                       :fouls :free_throw_attempts :inbounds :out_of_bounds :period_end_court_exits
                       :period_start_court_appearances :rule_violations :substitutions_in :substitutions_out
                       :timeout_calls})

(defn get-events [event-types seasons teams players games]
  (let [lineup-occurrences (http-get (apis :lineup-occurrences)
                                     {:seasons (-> seasons set vec)
                                      :teams (-> teams set vec)
                                      :players (-> players set vec)
                                      :games (-> games set vec)})
        events (for [et (if (empty? event-types)
                          all-event-types
                          (-> event-types set vec))
                     lo lineup-occurrences]
                 (map #(assoc % :event_type et)
                      (get-all-events et (lo :game_id) (lo :team) (lo :jersey_number))))]
    (assoc (->> events
                flatten
                (group-by :event_type))
      :game-ids (set (map :game_id lineup-occurrences))
      :player-ids (set (map :player_id lineup-occurrences))
      :team-ids (set (map :team_id lineup-occurrences))
      :season-ids (set (map :season_id lineup-occurrences)))))
