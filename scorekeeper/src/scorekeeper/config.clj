(ns scorekeeper.config)

(def conf {:db-spec {:dbtype "postgresql"
                     :dbname (System/getenv "SCOREKEEPER_POSTGRES_DATABASE")
                     :host (System/getenv "SCOREKEEPER_POSTGRES_HOST")
                     :port (-> "SCOREKEEPER_POSTGRES_PORT" System/getenv)
                     :user (System/getenv "SCOREKEEPER_POSTGRES_USER")
                     :password (System/getenv "SCOREKEEPER_POSTGRES_PASSWORD")}
           :base-url "/api/scorekeeper"
           :port (-> "SCOREKEEPER_PORT" System/getenv Integer.)
           :league-manager-api-url (str "http://"
                                        (System/getenv "LEAGUE_MANAGER_HOST")
                                        ":"
                                        (-> "LEAGUE_MANAGER_PORT" System/getenv Integer.)
                                        "/api")})
