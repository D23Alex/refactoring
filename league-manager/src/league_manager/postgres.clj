(ns league-manager.postgres
  (:require [clojure.pprint]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [league-manager.config :refer [conf]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(defn get-resource [resource id]
  (jdbc/execute-one! (conf :db-spec)
                     (-> (h/select :*)
                         (h/from resource)
                         (h/where [:= :id id])
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-all-resources [resource]
  (jdbc/execute! (conf :db-spec)
                 (-> (h/select :*)
                     (h/from resource)
                     sql/format)
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn create-resource [resource data]
  (jdbc/execute-one! (conf :db-spec)
                     (-> (h/insert-into resource)
                         (h/values [data])
                         (h/returning :*)
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps
                      :return-keys true}))

(defn update-resource [resource id data]
  (jdbc/execute-one! (conf :db-spec)
                     (-> (h/update resource)
                         (h/set data)
                         (h/where [:= :id id])
                         (h/returning :*)
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps
                      :return-keys true}))

(defn delete-resource [resource id]
  (jdbc/execute! (conf :db-spec)
                 (-> (h/delete-from resource)
                     (h/where [:= :id id])
                     sql/format)))

(defn get-all-lineup-occurrences [seasons teams players games]
  (let [lineup-occurrences
        (jdbc/execute! (conf :db-spec)
                       (-> (h/select [:games/id :game_id]
                                     :lineup_occurrences/team
                                     :lineup_occurrences/jersey_number
                                     :games/team_1_id
                                     :games/team_2_id
                                     :games/season_id
                                     :lineup_occurrences/player_id)
                           (h/from :games)
                           (h/join :lineup_occurrences [:= :games/id :lineup_occurrences/game_id])
                           (h/join :seasons [:= :seasons/id :games/season_id])
                           (h/where (if (> (count seasons) 0)
                                      [:in :seasons/id (vec seasons)])
                                    (if (> (count games) 0)
                                      [:in :games/id (vec games)])
                                    (if (> (count players) 0)
                                      [:in :lineup_occurrences/player_id players])
                                    (if (> (count teams) 0)
                                      [:or [:and [:= :lineup_occurrences/team [:cast "team1" :team_enum]]
                                            [:in :games/team_1_id (vec teams)]]
                                       [:and [:= :lineup_occurrences/team [:cast "team2" :team_enum]]
                                        [:in :games/team_2_id (vec teams)]]]))
                           sql/format)
                       {:builder-fn rs/as-unqualified-lower-maps})]
    (map #(-> %
              (assoc :team_id (if (= (% :team) "team1")
                                (% :team_1_id)
                                (% :team_2_id)))
              (dissoc :team_1_id :team_2_id))
         lineup-occurrences)))

(defn terminate [contract-id termination_date]
  (jdbc/execute-one! (conf :db-spec)
                     (-> (h/update :contracts)
                         (h/set {:is_terminated true
                                 :end_date termination_date})
                         (h/where [:= :id contract-id])
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps
                      :return-keys true}))

(defn transfer [contract-id team-id transfer-date]
  (jdbc/with-transaction [tx (conf :db-spec)]
                         (let [original-contract (jdbc/execute-one! tx
                                                                    (-> (h/select :*)
                                                                        (h/from :contracts)
                                                                        (h/where [:= :id contract-id])
                                                                        sql/format)
                                                                    {:builder-fn rs/as-unqualified-lower-maps})
                               _ (jdbc/execute-one! tx
                                                    (-> (h/update :contracts)
                                                        (h/set {:end_date transfer-date
                                                                :is_transferred true})
                                                        (h/where [:= :id contract-id])
                                                        sql/format)
                                                    {:builder-fn rs/as-unqualified-lower-maps})
                               new-contract (assoc original-contract
                                              :start_date transfer-date
                                              :team_id team-id
                                              :is_transferred false)]
                           (jdbc/execute-one! tx
                                              (-> (h/insert-into :contracts)
                                                  (h/values [(dissoc new-contract :id)])
                                                  (h/returning :*)
                                                  sql/format)
                                              {:builder-fn rs/as-unqualified-lower-maps
                                               :return-keys true}))))

(defn team-members [team-id date]
  (jdbc/execute-one! (conf :db-spec)
                     (-> (h/select :players/*)
                         (h/from :contracts)
                         (h/join :players [:= :players/id  :contracts/player_id])
                         (h/where [:= :team_id team-id]
                                  [:between date :start-date :end-date])
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps}))
