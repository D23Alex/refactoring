(ns stats.advanced-stats
  (:require [stats.config :refer [conf]]
            [stats.http :refer [http-get]]
            [stats.basic-stats :refer [get-totals]]))

(def apis {:events (str (conf :scorekeeper-api-url) "/events")})

(defn- get-play-intervals [player-id game-id]
  (let [events (http-get (apis :events)
                         {:event-types [:after_timeout_court_appearances :period_end_court_exits
                                        :period_start_court_appearances :substitutions_in
                                        :substitutions_out :timeout_calls]
                          :seasons []
                          :teams []
                          :players player-id
                          :games game-id})]
    {:in-timestamps (map :milliseconds_since_start
                         (concat (events :after_timeout_court_appearances)
                                 (events :period_start_court_appearances)
                                 (events :substitutions_in)))
     :out-timestamps (map :milliseconds_since_start
                          (concat (events :timeout_calls)
                                  (events :period_start_court_exits)
                                  (events :substitutions_out)))}))

(defn get-time-played [player-id game-id]
  (let [play-intervals (get-play-intervals player-id game-id)]
    (- (apply + (play-intervals :in-timestamps))
       (apply + (play-intervals :out-timestamps)))))

(defn- calculate-averages [totals count]
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

(defn get-performance-index-rating [seasons teams players games]
  (let [totals (get-totals seasons teams players games)]
    (- (->> totals
            (filter #(contains? #{:points :rebounds :blocks :steals :assists} (first %)))
            vals
            (apply +))
       (->> totals
            (filter #(contains? #{:field-goals-missed :turnovers :free-throws-missed :fouls} (first %)))
            vals
            (apply +)))))

(defn get-offence-efficiency-rating [seasons teams players games]
  (let [totals (get-totals seasons teams players games)]
    (/ (totals :points)
       (totals :field-goals-attempted))))

(defn- on-court-at-timestamp? [in-timestamps out-timestamps timestamp]
  (let [last-event-timestamp (->> in-timestamps
                                  (concat out-timestamps)
                                  sort
                                  take-while #(< % timestamp)
                                  last)]
    (contains? in-timestamps last-event-timestamp)))

(defn get-plus-minus [player-id game-id]
  (let [play-intervals (get-play-intervals player-id game-id)
        scoring-events (http-get (apis :events)
                                 {:event-types [:field_goal_attempts :free_throw_attempts]
                                  :seasons []
                                  :teams []
                                  :players player-id
                                  :games game-id})
        two-pointers (filter #(and (= (% :is_successful) true)
                                   (not (= "three-pointer" (% :type)))
                                   (on-court-at-timestamp? (play-intervals :in-timestamps)
                                                           (play-intervals :out-timestamps)
                                                           (% :milliseconds_since_start)))
                             (scoring-events :field_goal_attempts))
        three-pointers (filter #(and (= (% :is_successful) true)
                                     (= "three-pointer" (% :type))
                                     (on-court-at-timestamp? (play-intervals :in-timestamps)
                                                             (play-intervals :out-timestamps)
                                                             (% :milliseconds_since_start)))
                               (scoring-events :field_goal_attempts))
        free-throws (filter #(and (= (% :is_successful) true)
                                  (on-court-at-timestamp? (play-intervals :in-timestamps)
                                                          (play-intervals :out-timestamps)
                                                          (% :milliseconds_since_start)))
                            (scoring-events :free_throw_attempts))]
    (- (+ (* (count (filter #(= "team1" (% :team)) two-pointers)) 2)
          (* (count (filter #(= "team1" (% :team)) three-pointers)) 3)
          (count (filter #(= "team1" (% :team)) free-throws)))
       (+ (* (count (filter #(= "team2" (% :team)) two-pointers)) 2)
          (* (count (filter #(= "team2" (% :team)) three-pointers)) 3)
          (count (filter #(= "team2" (% :team)) free-throws))))))

(defn get-standard-tendex-rating [player-id game-id]
  (let [totals (get-totals [] [] [player-id] [game-id])
        minutes-played (/ (get-time-played player-id game-id) (* 1000 60))]
    (- (+ (totals :points)
          (totals :rebounds)
          (totals :steals)
          (totals :blocks)
          (totals :assists))
       (+ (totals :field-goals-missed)
          (totals :free-throws-missed)
          (totals :turnovers)
          (/ (/ (totals :fouls) minutes-played) 60)))))

(defn get-modified-tendex-rating [player-id game-id]
  (let [totals (get-totals [] [] [player-id] [game-id])
        minutes-played (/ (get-time-played player-id game-id) (* 1000 60))]
    (- (+ (totals :points)
          (totals :rebounds)
          (* 1.25 (totals :steals))
          (totals :blocks)
          (* 1.25 (totals :assists)))
       (+ (totals :field-goals-missed)
          (/ (totals :free-throws-missed) 2)
          (* 1.25 (totals :turnovers))
          (-> totals
              :fouls
              (/ 2)
              (/ minutes-played)
              (/ 60))))))
