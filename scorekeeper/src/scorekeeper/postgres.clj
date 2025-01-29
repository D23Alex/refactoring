(ns scorekeeper.postgres
  (:require [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [scorekeeper.config :refer [conf]]
            [scorekeeper.events :refer [event-names]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def table-names {:rebounds :rebounds
                  :field-goal-attempts :field_goal_attempts
                  :blocks :blocks
                  :steals :steals
                  :assists :assists
                  :after-timeout-court-appearances :after_timeout_court_appearances
                  :fouls :fouls
                  :free-throw-attempts :free_throw_attempts
                  :inbounds :inbounds
                  :out-of-bounds  :out_of_bounds
                  :period-end-court_exits :period_end_court_exits
                  :period-start-court_appearances :period_start_court_appearances
                  :rule-violations :rule_violations
                  :substitutions-in :substitutions_in
                  :substitutions-out :substitutions_out
                  :timeout-calls :timeout_calls
                  :event-cancellations :event_cancellations})

(defn create-resource [resource data]
  (jdbc/execute-one! (conf :db-spec)
                     (-> (h/insert-into (table-names resource))
                         (h/values [data])
                         (h/returning :*)
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps
                      :return-keys true}))

(defn get-event [event-type id]
  (jdbc/execute-one! (conf :db-spec)
                     (-> (h/select :*)
                         (h/from (table-names event-type))
                         (h/where [:= :id id])
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-all-events
  ([event-type]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from (table-names event-type))
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps}))
  ([event-type game-id]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from (table-names event-type))
                      (h/where [:= :game_id game-id])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps}))
  ([event-type game-id team]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from (table-names event-type))
                      (h/where [:= :game_id game-id]
                               [:= :team [:cast team :team_enum]])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps}))
  ([event-type game-id team player-jersey-number]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from (table-names event-type))
                      (h/where [:= :game_id game-id]
                               [:= :team [:cast team :team_enum]]
                               [:= :player-jersey-number player-jersey-number])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps})))

(defn get-uncancelled-events
  ([event-type]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from (table-names event-type))
                      (h/where [:not-in :id (-> (h/select :event_id)
                                                (h/from :event_cancellations)
                                                (h/where [:= (event-names event-type) :event_type]))])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps}))
  ([event-type game-id]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from (table-names event-type))
                      (h/where [:= :game_id game-id]
                               [:not-in :id (-> (h/select :event_id)
                                                (h/from :event_cancellations)
                                                (h/where [:= (event-names event-type) :event_type]))])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps}))
  ([event-type game-id team]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from (table-names event-type))
                      (h/where [:= :game_id game-id]
                               [:= :team [:cast team :team_enum]]
                               [:not-in :id (-> (h/select :event_id)
                                                (h/from :event_cancellations)
                                                (h/where [:= (event-names event-type) :event_type]))])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps}))
  ([event-type game-id team player-jersey-number]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from (table-names event-type))
                      (h/where [:= :game_id game-id]
                               [:= :team [:cast team :team_enum]]
                               [:= :player-jersey-number player-jersey-number]
                               [:not-in :id (-> (h/select :event_id)
                                                (h/from :event_cancellations)
                                                (h/where [:= (event-names event-type) :event_type]))])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps})))