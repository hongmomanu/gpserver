(ns gpserver.db.core
  (:require
    [clojure.java.jdbc :as jdbc]
    [yesql.core :refer [defqueries]]
    [taoensso.timbre :as timbre]
    [monger.core :as mg]
    [monger.collection :as mc]
    [monger.operators :refer :all]
    [monger.query :refer [with-collection find options paginate sort fields limit]]

    [environ.core :refer [env]]))



(defonce db (let [uri (get (System/getenv) "MONGOHQ_URL" "mongodb://jack:1313@111.1.76.108/gpapp")
                  {:keys [conn db]} (mg/connect-via-uri uri)]
              db))



(defn addarctile [item]

  (mc/insert-and-return db "arctiles" item)

  )


(defn get-articles [conds]

  (with-collection db "arctiles"
    (find conds)
    (sort {:time -1})
    (limit 50))

  )