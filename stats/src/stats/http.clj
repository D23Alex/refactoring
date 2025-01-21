(ns stats.http
  (:require [clj-http.client :as client]
            [clojure.pprint]
            [clojure.walk]))

(defn http-get [url params]
  (let [filtered-params (into {} (filter (fn [[_ v]] (some? v)) params))
        processed-params (into {} (map (fn [[k v]]
                                         [k (if (coll? v) (vec v) v)])
                                       filtered-params))]
    (-> (client/get url {:as :json
                         :accept :json
                         :query-params processed-params})
        :body)))
