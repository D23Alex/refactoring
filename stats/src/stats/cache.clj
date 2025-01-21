(ns stats.cache
  (:require [clojure.pprint]
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
