(ns gpserver.controller.home
  (:use [compojure.core]
        [org.httpkit.server]


        [noir.io :as nio]
        )
  (:require [gpserver.db.core :as db]
            [gpserver.public.websocket :as websocket]
            [gpserver.layout :as layout]
            [ring.util.http-response :refer [ok found]]

            [gpserver.public.common :as commonfunc]

            [ring.util.response :refer [file-response]]

            [clj-time.coerce :as c]
            [clj-time.local :as l]

            [clojure.data.json :as json]
            [monger.json]
            [taoensso.timbre :as timbre]
             [monger.operators :refer [$gt $lt $and $ne $push $or $nin $in]]
            [cheshire.core :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as f]
            )
  (:import [org.bson.types ObjectId]
           [java.util  Date Calendar]
           )
  )


(defn articles-page []

  (let [
        articles (db/get-articles {})

        ]

    (layout/render "articles.html" {:articles articles})
    )

  )

(defn arctiledetail [articleid]

  (let [
        article (db/get-articles-byid (ObjectId. articleid))

        ]



    (layout/render "articleedit.html" {:articledetail article})
    )


  )

(defn addarctile [title titleimage type source content]


  (try
      (do
       (let [
          uploadpath  (str commonfunc/datapath "upload/")
          timenow (c/to-long  (l/local-now))
          filename (str timenow (:filename titleimage) )
          ]
      ;(println filename)
      (nio/upload-file uploadpath  (conj titleimage {:filename filename}))
      (db/addarctile {:title title :titleimage filename :type type :source source :time (new Date) :content content})
      (found "/articles")

      )
      )
    (catch Exception ex

      (found "/articles")
      ))




  )


(defn getarticlesbytypeandtime [type time]

  (let [
        datetime (f/parse (f/formatters :date-time) time)
        oneitem (first (db/get-articles-by-cond  {:time { $lte (.toDate datetime) }} 1))
        ]
     (if (nil? oneitem) (ok []) (db/get-articles-by-cond
                                 {:time { $lte (.toDate datetime)}
                                  :time { $gte (:time oneitem)}}  1000))

    )


  )

(defn savearctile [title titleimage type source content id]



  (try
      (do
       (let [
          uploadpath  (str commonfunc/datapath "upload/")
          timenow (c/to-long  (l/local-now))
          filename (str timenow (:filename titleimage) )
          ]
      ;(println filename)
       (if (= (:size titleimage)0)

         (db/savearctile-by-oid {:title title   :type type :source source :time (new Date) :content content} (ObjectId. id))

         (do (nio/upload-file uploadpath  (conj titleimage {:filename filename}))
             (db/savearctile-by-oid {:title title :titleimage filename :type type :source source :time (new Date) :content content} (ObjectId. id))
           )


         )




      (found "/articles")

      )
      )
    (catch Exception ex

      (found "/articles")
      ))




  )
