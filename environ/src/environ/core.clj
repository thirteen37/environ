(ns environ.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))


(defn- keywordize [s]
  (-> (str/lower-case s)
      (str/replace "_" "-")
      (str/replace "." "-")
      (keyword)))

(defn- sanitize [k]
  (let [s (keywordize (name k))]
    (if-not (= k s) (println "Warning: environ key " k " was has been corrected to " s))
    s))

(defn- read-system-env []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-system-props []
  (->> (System/getProperties)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-property-file []
  (let [prop-file (io/file ".properties")]
    (if (.exists prop-file)
      (->> (doto (java.util.Properties.)
             (.load (io/reader ".properties")))
           (map (fn [[k v]] [(keywordize k) v]))
           (into {})))))

(defn- read-env-file []
  (let [env-file (io/file ".lein-env")]
    (if (.exists env-file)
      (into {} (for [[k v] (read-string (slurp env-file))]
                 [(sanitize k) v])))))

(def ^{:doc "A map of environment variables."}
  env
  (merge
   (read-property-file)
   (read-env-file)
   (read-system-props)
   (read-system-env)))
