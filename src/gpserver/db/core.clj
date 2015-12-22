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

(defn addstudypoint[item]

  (mc/insert-and-return db "studypoints" item)

  )

(defn add-new-user [item]

  (mc/insert-and-return db "users" item)

  )

(defn add-new-userstudypoint [item]

  (mc/insert-and-return db "userstudypoint" item)

  )


(defn insert-newclass [item]

  (mc/insert-and-return db "onlineclass" item)

  )


(defn get-unreadmsg-by-uid [cond]
  (mc/find-maps
    db "messages" cond
    )
  )



(defn savearctile-by-oid [item oid]

  (mc/update-by-id db "arctiles" oid {$set item})

  )

(defn savestudypoint-by-oid [item oid]

  (mc/update-by-id db "studypoints" oid {$set item})

  )


(defn update-group-message-byid [oid data]

  (mc/update-by-id db "messages" oid {$push data})

  )

(defn update-onlineclass-state-byid [oid data]

  (mc/update-by-id db "onlineclass" oid {$set data})

  )

(defn delete-onlineclass-byid [oid]

  (mc/remove-by-id db "onlineclass"  oid)

  )

(defn update-message-byid [oid data]
   (mc/update-by-id db "messages" oid {$set data})
  )

(defn get-onlineclass-bystart  [startpage]

  (with-collection db "onlineclass"

    (sort {:time -1})
    (paginate :page startpage :per-page 10))


  )

(defn get-studypoints-bystart  [startpage]

  (with-collection db "studypoints"

    (sort {:time -1})
    (paginate :page startpage :per-page 10))


  )




(defn get-message [conds size]
  (with-collection db "messages"
    (find conds)
    (sort {:time -1})
    (limit size))

  )

(defn insert-message [item]

  (mc/insert-and-return db "messages" item)

  )


(defn get-users-by-cond [cond]

  (mc/find-maps
    db "users" cond
    )

  )

(defn update-studypoint-byid [oid data]


  (mc/update-by-id db "studypoints" oid {$set data})

  )

(defn update-userstudypoint-byid [oid data]
  (mc/update-by-id db "userstudypoint" oid {$set data})

  )


(defn get-articles [conds]

  (with-collection db "arctiles"
    (find conds)
    (sort {:time -1})
    (limit 50))

  )


(defn get-studypoints [conds]

  (with-collection db "studypoints"
    (find conds)
    (sort {:time -1})
    (limit 50))

  )

(defn get-articles-by-cond [conds size]

  (with-collection db "arctiles"
     (fields [:_id :title :time :titleimage :source])
    (find conds)
    (sort {:time 1})
    (limit size)
    )

  )

(defn get-articles-byid [oid]

  (mc/find-map-by-id
    db "arctiles" oid
    )

  )



(defn get-studypoints-byid [oid]

  (mc/find-map-by-id
    db "studypoints" oid
    )

  )

(defn get-userstudypoint-byids   [studypointid userid]
  (mc/find-one-as-map
    db "userstudypoint" {:studypointid studypointid :userid userid}
    )

  )
