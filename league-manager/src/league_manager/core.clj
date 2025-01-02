(ns league-manager.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [clojure.pprint]
            [ring.middleware.keyword-params :as keyword-params]
            [ring.middleware.params :as params]
            [ring.middleware.json :as json]
            [compojure.core :as compojure]
            [compojure.route :as compojure-route])
  (:import [java.text SimpleDateFormat]))

(def date-format (SimpleDateFormat. "yyyy-MM-dd"))

(defn parse-date [date-str]
  (.parse date-format date-str))

(def storage (atom {:teams {0 {:name "Zenit" :city "Saint-Petersburg"}
                            1 {:name "CSKA" :city "Moscow"}}
                    :players {0 {:first-name "Firstname0"
                                 :last-name "Lastname0"
                                 :height 190
                                 :date-of-birth (parse-date "1997-06-18")}
                              1 {:first-name "Firstname1"
                                 :last-name "Lastname1"
                                 :height 195
                                 :date-of-birth (parse-date "1998-06-18")}}
                    :contracts {0 {:team-id 0
                                   :player-id 0
                                   :from (parse-date "2024-06-18")
                                   :to (parse-date "2026-06-18")}
                                1 {:team-id 1
                                   :player-id 1
                                   :from (parse-date "2023-06-18")
                                   :to (parse-date "2027-06-18")}}
                    :games {0 {:scheduled-start (parse-date "2025-06-18")
                               :teams [0 1]
                               :home-team 0
                               :location "KSK ARENA"}
                            1 {:scheduled-start (parse-date "2025-07-18")
                               :teams [0 1]
                               :home-team 1
                               :location "VTB ARENA"}}}))

(defn get-resource [resource id]
  (get-in @storage [resource id]))

(defn get-all-resources [resource]
  (vals (get @storage resource)))

(defn create-resource [resource data]
  (let [id (inc (apply max (keys (get @storage resource))))]
    (swap! storage assoc-in [resource id] data)
    (get-resource resource id)))

(defn update-resource [resource id data]
  (swap! storage assoc-in [resource id] data)
  (get-resource resource id))

(defn delete-resource [resource id]
  (swap! storage update resource dissoc id)
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

(defn -main [& args]
  (jetty/run-jetty (-> app
                       params/wrap-params
                       keyword-params/wrap-keyword-params
                       json/wrap-json-body
                       json/wrap-json-response)
                   {:port 3000
                    :join true}))