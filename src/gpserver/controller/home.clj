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
             [monger.operators :refer [$gt $gte $lt $lte $and $ne $push $or $nin $in]]
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

(defn studypoints-page []

  (let [
        studypoints (db/get-studypoints {})

        ]

    (layout/render "studypoints.html" {:studypoints studypoints})
    )

  )



(defn arctiledetail [articleid]

  (let [
        article (db/get-articles-byid (ObjectId. articleid))

        ]



    (layout/render "articleedit.html" {:articledetail article})
    )


  )

(defn studypointdetail [studypointid]
  (let [
        article (db/get-studypoints-byid (ObjectId. studypointid))

        ]



    (layout/render "studypointedit.html" {:studypointdetail article})
    )

  )

(defn get-group-message-history [fromid groupid time]


  (let [
        datetime (f/parse (f/formatters :date-time-no-ms) time)
        ]

    (db/get-message {$and [{:groupid  groupid } {:time { $lt (.toDate datetime) }} ]} 10)
    )


  )

(defn send-message-online-group [userid msg ]

  (doseq [channel (keys @websocket/channel-hub)]
    (when (= (get  (get @websocket/channel-hub channel) "userid") userid)

      (do
        (timbre/info "send-message-online : " msg )

        (send! channel (generate-string
                       {

                         :data  msg
                         :type "message"
                         }
                       )
        false)

        (db/update-group-message-byid (:_id msg)  {:userids userid})

        )



      )
    )


  )

(defn send-message-online [userid msg ]

  (doseq [channel (keys @websocket/channel-hub)]
    (when (= (get  (get @websocket/channel-hub channel) "userid") userid)

      (do
        (timbre/info "send-message-online : " msg )

        (send! channel (generate-string
                       {

                         :data  msg
                         :type "message"
                         }
                       )
        false)

        (db/update-message-byid (:_id msg) {:isread true})
        )



      )
    )


  )


(defn getunreadmessages [userid usertype]

  (let [
        ;unreadperson (db/get-unreadmsg-by-uid {:toid userid :isread false})



        unreadgroups (db/get-unreadmsg-by-uid {:toid usertype :userids {$nin [userid]}})

        msgs unreadgroups ;(concat unreadperson unreadgroups)

        ]
     (dorun (map #(send-message-online  userid %) msgs))

     (dorun (map #(db/update-group-message-byid (:_id %)  {:userids userid}) unreadgroups))

     (ok {:success true} )
   )

  )


(defn updateonlineclassestate [id state]

  (try



      (do
        (db/update-onlineclass-state-byid (ObjectId. id) {:state (read-string state)})
        (ok {:success true})
        )





      (catch Exception ex
        (ok {:success false :message (.getMessage ex)})
        )

    )


  )


(defn applyforstudypoint [studyid userid]
  (try



      (do
        (db/update-studypoint-byid (ObjectId. studyid)  {$push {:userids userid}}  )

        (db/add-new-userstudypoint {:studypointid studyid :userid userid :timelearn 0})

        (ok {:success true})
        )



      (catch Exception ex
        (ok {:success false :message (.getMessage ex)})
        )

    )


  )

(defn deleteonlineclassestate [id]
  (try



      (do
        (db/delete-onlineclass-byid (ObjectId. id) )
        (ok {:success true})
        )





      (catch Exception ex
        (ok {:success false :message (.getMessage ex)})
        )

    )

  )

(defn addnewclass [userid realname title classtime place]

  (try


    (let [
          item (db/insert-newclass
                {
                 :userid userid :realname realname
                 :title title :classtime classtime :state 0
                 :place place :time (new Date)
                 }
             )


          ]


      (ok {:success true })

      )

      (catch Exception ex
        (ok {:success false :message (.getMessage ex)})
        )

    )

  )

(defn getonlineclasses [startpage]

  (ok (db/get-onlineclass-bystart  (read-string startpage)))

  )

(defn getstudypoints [startpage userid]

  (let [
        studypoints (db/get-studypoints-bystart  (read-string startpage))

        results (map #(conj % (let [
                                    userdata (db/get-userstudypoint-byids  (str (:_id %)) userid)
                                    ]

                                (if (nil? userdata) {} {:userdata userdata})

                                )) studypoints)

        ]

    (ok results )
    )



  )

(defn getstudypointbyid [studypointid]


  (ok (db/get-studypoints-byid  (ObjectId. studypointid)))


  )

(defn getusertotalpointsbyuid [userid]

  (let [
        items (db/get-userstudypoint-by-cond   {:userid userid } )

        detailitmes (map #(conj % {:detaildata (db/get-studypoints-byid  (ObjectId. (:studypointid %)))}) items)



        filteritems (filter (fn [x](>= (:timelearn x) (read-string (-> x  :detaildata :timelong))))
                                        detailitmes)

        totalpoints (apply + (map #(read-string (-> % :detaildata :point ))  filteritems))
        ]

      (ok {:totalpoints totalpoints})

    )

  )

(defn updateuserstudypointlearntime [id timelearn]
  (try



      (do

        (db/update-userstudypoint-byid (ObjectId. id)  {:timelearn (read-string timelearn)})

        (ok {:success true})

        )



      (catch Exception ex

        (ok {:success false :message (.getMessage ex)})

        )

    )

  )


(defn getuserstudypoint [studypointid userid]


  (ok (db/get-userstudypoint-byids  studypointid userid))


  )


(defn addgroupmessage [content ftype fromid toid groupid mtype toname fromname]

  (try


    (let [
          item (db/insert-message
                {
                 :content content :ftype ftype
                 :fromid fromid :toid toid :isread false
                 :toname toname :fromname fromname :userids []
                 :groupid groupid :mtype mtype :time (new Date)
                 }
             )

          groupitems (db/get-users-by-cond {$and [{:usertype groupid} {:_id {$ne (ObjectId. fromid)}}]})

          ]

      (dorun (map #(send-message-online-group (str (:_id %)) item) groupitems))
      (db/update-group-message-byid (:_id item)  {:userids fromid})


      (ok {:success true :data item})

      )

      (catch Exception ex
        (ok {:success false :message (.getMessage ex)})
        )

    )

  )



(defn getarticlebyid [articleid]

  (let [
        article (db/get-articles-byid (ObjectId. articleid))

        ]



     (ok article)
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

(defn addstudypoint [title videofile point timelong]

  (try
      (do
       (let [
          uploadpath  (str commonfunc/datapath "upload/")
          timenow (c/to-long  (l/local-now))
          filename (str timenow (:filename videofile) )
          ]

      (nio/upload-file uploadpath  (conj videofile {:filename filename}))

      (db/addstudypoint {:title title :videofile filename :userids []  :point point :timelong timelong :time (new Date) })

      (found "/studypoints")

      )
      )
    (catch Exception ex

      (found "/studypoints")

      ))

  )





(defn login [username password]

  (try
      (let [
             item (db/get-users-by-cond {:username username :password password})
             ]

      (do
        (if (empty? item)
          (ok {:success false :message "用户或密码错误"})
          (ok {:success true :message "登录成功" :user (first item) })
          )

        )
      )
      (catch Exception ex
        (ok {:success false :message (.getMessage ex)})
        )

    )

  )

(defn updateusercardbyid [dutyid personid userid]
  (try



      (do

        (db/update-user-byid (ObjectId. userid)  {:dutyid dutyid :personid personid})

        (ok {:success true})

        )



      (catch Exception ex

        (ok {:success false :message (.getMessage ex)})

        )

    )


  )

(defn newuser [username realname password usertype]

  (try
      (let [
             item (db/get-users-by-cond {:username username})
             ]

      (do
        (if (empty? item)
          (ok {:success true  :user (db/add-new-user {:username username :realname realname :money 0
                                                      :password password :usertype usertype :personid "" :dutyid ""
                                                      })})
          (ok {:success false :message "用户已存在"  })
          )

        )
      )
      (catch Exception ex
        (ok {:success false :message (.getMessage ex)})
        )

    )

  )

(defn getarticlesbytypeandtime [type time]

  (let [
        datetime (f/parse (f/formatters :date-time) time)
        items (db/get-articles-by-cond  {:time { $lte (.toDate datetime) } :type type} 1 -1)

        oneitem (first items)


        ]
     (if (nil? oneitem) (ok {:success false}) (ok {:success true :time (:time oneitem) :data (db/get-articles-by-cond
                                 {:type type :time { $lte (:time oneitem) $gte (.toDate (f/parse (f/formatters :basic-date ) (f/unparse (f/formatters :basic-date ) (c/from-date (:time oneitem)))))}}  1000 1)}))

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

(defn savestudypoint [title videofile point timelong  id]
    (try
      (do
       (let [
          uploadpath  (str commonfunc/datapath "upload/")
          timenow (c/to-long  (l/local-now))
          filename (str timenow (:filename videofile) )
          ]

       (if (= (:size videofile)0)

         (db/savestudypoint-by-oid {:title title   :point point :timelong timelong :time (new Date) } (ObjectId. id))

         (do (nio/upload-file uploadpath  (conj videofile {:filename filename}))
             (db/savestudypoint-by-oid {:title title :videofile filename :point point :timelong timelong :time (new Date)} (ObjectId. id))
           )


         )




      (found "/studypoints")

      )
      )
    (catch Exception ex

      (found "/studypoints")
      ))



  )
