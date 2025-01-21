(ns league-manager.util
  (:import (java.time LocalDate)))

(defn parse-date [value]
  (try
    (-> (LocalDate/parse value))
    (catch Exception _ value)))
