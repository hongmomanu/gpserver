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
