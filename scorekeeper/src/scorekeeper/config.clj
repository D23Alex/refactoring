(ns scorekeeper.config)

(def conf {:db {:name "scorekeeper"
                :collection "statsheets"}
           :base-url "/api/statsheets"
           :port (-> "SCOREKEEPER_PORT" System/getenv Integer.)
           :league-manager-api-url (str "http://"
                                        (System/getenv "LEAGUE_MANAGER_HOST")
                                        ":"
                                        (-> "LEAGUE_MANAGER_PORT" System/getenv Integer.)
                                        "/api")})
