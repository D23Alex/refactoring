(ns scorekeeper.mongo
  (:gen-class)
  (:require [clojure.pprint]
            [clojure.walk]
            [monger.collection :as mc]
            [monger.core :as mg]
            [scorekeeper.config :refer [conf]])
  (:import (org.bson.types ObjectId)))

(def conn (mg/connect))
(def db (mg/get-db conn (-> conf :db :name)))
(def statsheets-collection (-> conf :db :collection))

(defn get-statsheet [filters]
  (mc/find-one-as-map db statsheets-collection filters))

(defn create-statsheet [statsheet]
  (mc/insert-and-return db statsheets-collection statsheet))

(defn update-statsheet [id statsheet]
  (mc/update-by-id db statsheets-collection (ObjectId. id) statsheet)
  (get-statsheet {:_id (ObjectId. id)}))

(defn delete-statsheet [id]
  (mc/remove-by-id db statsheets-collection (ObjectId. id)))
