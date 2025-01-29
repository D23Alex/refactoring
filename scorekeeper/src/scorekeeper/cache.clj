(ns scorekeeper.cache
  (:require [clojure.pprint]
            [scorekeeper.events :refer [get-events]]
            [clojure.walk])
  (:import (com.github.benmanes.caffeine.cache Caffeine)
           (java.util.concurrent TimeUnit)
           (java.util.function Function)))

(def cache
  (-> (Caffeine/newBuilder)
      (.maximumSize 10000)
      (.expireAfterWrite 10 TimeUnit/SECONDS)
      .build))

(defn cached-fn [f]
  (fn [& args]
    (.get cache args
          (reify Function
            (apply [_ _] (apply f args))))))

(def cached-get-events (cached-fn get-events))