(ns league-manager.core
  (:gen-class)
  (:require [clojure.pprint]
            [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as json]
            [ring.middleware.keyword-params :as keyword-params]
            [ring.middleware.params :as params])
  (:import (java.text SimpleDateFormat)
           (java.time LocalDate ZoneId)))

(def db-spec {:dbtype "postgresql"
              :dbname "league_manager"
              :host "localhost"
              :port 5432
              :user "postgres"
              :password "postgres"})

(defn parse-date [value]
  "Parses a string in the yyyy-MM-dd format into a java.util.Date object."
  (try
    (-> (LocalDate/parse value)
        )
    (catch Exception _ value)))

(defn get-resource [resource id]
  (jdbc/execute-one! db-spec
                     (-> (h/select :*)
                         (h/from resource)
                         (h/where [:= :id id])
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-all-resources [resource]
  (jdbc/execute! db-spec
                 (-> (h/select :*)
                     (h/from resource)
                     sql/format)
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn create-resource [resource data]
  (jdbc/execute-one! db-spec
                     (-> (h/insert-into resource)
                         (h/values [data])
                         (h/returning :*)
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps
                      :return-keys true}))

(defn update-resource [resource id data]
  (jdbc/execute-one! db-spec
                     (-> (h/update resource)
                         (h/set data)
                         (h/where [:= :id id])
                         (h/returning :*)
                         sql/format)
                     {:builder-fn rs/as-unqualified-lower-maps
                      :return-keys true}))

(defn delete-resource [resource id]
  (jdbc/execute! db-spec
                 (-> (h/delete-from resource)
                     (h/where [:= :id id])
                     sql/format))
  {:status 204})

(compojure/defroutes app
                     (compojure/GET "/:resource" [resource]
                       {:status 200 :body (get-all-resources (keyword resource))})
                     (compojure/GET "/:resource/:id" [resource id]
                       {:status 200 :body (get-resource (keyword resource) (Integer. id))})
                     (compojure/POST "/:resource" [resource :as {body :body}]
                       {:status 201 :body (create-resource (keyword resource) body)})
                     (compojure/PUT "/:resource/:id" [resource id :as {body :body}]
                       {:status 200 :body (update-resource (keyword resource) (Integer. id) body)})
                     (compojure/DELETE "/:resource/:id" [resource id]
                       {:status 204 :body (delete-resource (keyword resource) (Integer. id))})
                     (compojure-route/not-found "Page not found"))

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

                       json/wrap-json-response)
                   {:port 3000
                    :join true}))