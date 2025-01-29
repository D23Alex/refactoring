(ns scorekeeper.postgres
  (:require [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [scorekeeper.config :refer [conf]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(defn get-event [event-type id]
  (jdbc/execute-one! (conf :db-spec)
                     (-> (h/select :*)
                         (h/from event-type)
                         (h/where [:= :id id])
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-all-events
  ([event-type]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from event-type)
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps}))
  ([event-type game-id]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from event-type)
                      (h/where [:= :game_id game-id])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps}))
  ([event-type game-id team]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from event-type)
                      (h/where [:= :game_id game-id]
                               [:= :team [:cast team :team_enum]])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps}))
  ([event-type game-id team player-jersey-number]
   (jdbc/execute! (conf :db-spec)
                  (-> (h/select :*)
                      (h/from event-type)
                      (h/where [:= :game_id game-id]
                               [:= :team [:cast team :team_enum]]
                               [:= :player-jersey-number player-jersey-number])
                      sql/format)
                  {:builder-fn rs/as-unqualified-lower-maps})))
