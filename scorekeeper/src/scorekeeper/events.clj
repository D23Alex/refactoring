(ns scorekeeper.events
  (:require [scorekeeper.http :refer [http-get]]
            [scorekeeper.config :refer [conf]]
            [scorekeeper.postgres :refer [get-all-events get-uncancelled-events]]))

(def event-names {:rebounds "rebounds"
                  :field-goal-attempts "field-goal-attempts"
                  :blocks "blocks"
                  :steals "steals"
                  :assists "assists"
                  :after-timeout-court-appearances "after-timeout-court-appearances"
                  :fouls "fouls"
                  :free-throw-attempts "free-throw-attempts"
                  :inbounds "inbounds"
                  :out-of-bounds "out-of-bounds"
                  :period-end-court_exits "period-end_court-exits"
                  :period-start-court_appearances "period-start-court-appearances"
                  :rule-violations "rule-violations"
                  :substitutions-in "substitutions-in"
                  :substitutions-out "substitutions-out"
                  :timeout-calls "timeout-calls"})

(def apis {:lineup-occurrences (str (conf :league-manager-api-url) "/lineup-occurrences")})

(def all-event-types (keys event-names))

(defn get-events [event-types seasons teams players games include-cancelled?]
  (let [lineup-occurrences (http-get (apis :lineup-occurrences)
                                     {:seasons (-> seasons set vec)
                                      :teams (-> teams set vec)
                                      :players (-> players set vec)
                                      :games (-> games set vec)})
        events (for [et (if (empty? event-types)
                          all-event-types
                          (-> event-types set vec))
                     lo lineup-occurrences]
                 (map #(assoc % :event-type et)
                      ((if include-cancelled?
                         get-all-events
                         get-uncancelled-events)
                       et (lo :game_id) (lo :team) (lo :jersey_number))))]
    (assoc (->> events
                flatten
                (group-by :event_type))
      :game-ids (set (map :game_id lineup-occurrences))
      :player-ids (set (map :player_id lineup-occurrences))
      :team-ids (set (map :team_id lineup-occurrences))
      :season-ids (set (map :season_id lineup-occurrences)))))
