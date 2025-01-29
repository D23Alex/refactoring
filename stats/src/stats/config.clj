(ns stats.config)

(def conf {:base-url "/api/stats"
           :port (-> "STATS_PORT" System/getenv Integer.)
           :scorekeeper-api-url (str "http://"
                                     (System/getenv "SCOREKEEPER_HOST")
                                     ":"
                                     (-> "SCOREKEEPER_PORT" System/getenv Integer.)
                                     "/api/scorekeeper")})
