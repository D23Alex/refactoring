(ns stats.basic-stats
  (:require [stats.config :refer [conf]]
            [stats.http :refer [http-get]]))

(def apis {:events (str (conf :scorekeeper-api-url) "/events")})

(defn get-totals [seasons teams players games]
  (let [events (http-get (apis :events)
                         {:event-types []
                          :seasons seasons
                          :teams teams
                          :players players
                          :games games})
        two-pointers-attempted (count (filter #(not (= "three-pointer" (% :type)))
                                              (events :field_goal_attempts)))
        two-pointers-made (count (filter #(and (= (% :is_successful) true)
                                               (not (= "three-pointer" (% :type))))
                                         (events :field_goal_attempts)))
        three-pointers-attempted (count (filter #(= "three-pointer" (% :type))
                                                (events :field_goal_attempts)))
        three-pointers-made (count (filter #(and (= (% :is_successful) true)
                                                 (= "three-pointer" (% :type)))
                                           (events :field_goal_attempts)))
        free-throws-attempted (count (events :free-throw-attempts))
        free-throws-made (count (filter #(= (% :is_successful) true)
                                        (events :free-throw-attempts)))]
    {:game-ids (events :game-ids)
     :player-ids (events :player-ids)
     :team-ids (events :team-ids)
     :season-ids (events :season-ids)
     :field-goals-attempted (+ two-pointers-attempted three-pointers-attempted)
     :field-goals-made (+ two-pointers-made three-pointers-made)
     :two-pointers-attempted two-pointers-attempted
     :three-pointers-attempted three-pointers-attempted
     :two-pointers-made two-pointers-made
     :three-pointers-made three-pointers-made
     :free-throws-attempted free-throws-attempted
     :free-throws-made free-throws-made
     :points (+ (* two-pointers-made 2)
                (* three-pointers-made 3)
                free-throws-made)
     :assists (-> events :assists count)
     :blocks (-> events :blocks count)
     :steals (-> events :steals count)
     :inbounds (-> events :inbounds count)
     :out-of-bounds (-> events :out_of_bounds count)
     :fouls (-> events :fouls count)
     :turnovers (+ (-> events :steals count)
                   (-> events :blocks count)
                   (-> events :out_of_bounds count)
                   (-> events :rule_violations count))}))
