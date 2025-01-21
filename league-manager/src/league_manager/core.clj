(ns league-manager.core
  (:gen-class)
  (:require [clojure.pprint]
            [league-manager.config :refer [conf]]
            [league-manager.routes :refer [app]]
            [league-manager.util :refer [parse-date]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as json]
            [ring.middleware.keyword-params :as keyword-params]
            [ring.middleware.params :as params]))

(defn wrap-parse-dates [handler]
  (fn [request]
    (let [body (:body request)
          parsed-body (clojure.walk/postwalk
                        (fn [x]
                          (if (and (string? x) (re-matches #"\d{4}-\d{2}-\d{2}" x))
                            (parse-date x)
                            x))
                        body)]
      (handler (assoc request :body parsed-body)))))

(defn wrap-exception-handling [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        {:status 500
         :body {:error (str "Internal Server Error: " (.getMessage e))}}))))

(defn -main [& args]
  (jetty/run-jetty (-> app
                       params/wrap-params
                       keyword-params/wrap-keyword-params
                       wrap-parse-dates
                       json/wrap-json-body
                       wrap-exception-handling
                       json/wrap-json-response)
                   {:port (conf :port)
                    :join true}))