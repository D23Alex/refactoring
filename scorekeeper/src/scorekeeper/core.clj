(ns scorekeeper.core
  (:gen-class)
  (:require [clojure.pprint]
            [clojure.walk]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as json]
            [ring.middleware.keyword-params :as keyword-params]
            [ring.middleware.params :as params]
            [scorekeeper.routes :refer [app]]
            [scorekeeper.config :refer [conf]])
  (:import (java.time LocalDate)))

(defn wrap-exception-handling [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        {:status 500
         :body {:error (str "Internal Server Error: " (.getMessage e))}}))))

(defn parse-date [value]
  (try
    (LocalDate/parse value)
    (catch Exception _ value)))

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