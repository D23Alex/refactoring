(ns league-manager.config)

(def conf {:db-spec {:dbtype "postgresql"
                     :dbname (System/getenv "LEAGUE_MANAGER_POSTGRES_DATABASE")
                     :host (System/getenv "LEAGUE_MANAGER_POSTGRES_HOST")
                     :port (-> "LEAGUE_MANAGER_POSTGRES_PORT" System/getenv)
                     :user (System/getenv "LEAGUE_MANAGER_POSTGRES_USER")
                     :password (System/getenv "LEAGUE_MANAGER_POSTGRES_PASSWORD")}
           :base-url "/api/league-manager/"
           :port (-> "LEAGUE_MANAGER_PORT" System/getenv Integer.)})