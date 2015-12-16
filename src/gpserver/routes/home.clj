(ns gpserver.routes.home
  (:require [gpserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok found]]

            [noir.io :as nio]

            [image-resizer.core :refer :all]
            [image-resizer.format :as format]

            [clj-time.coerce :as c]
            [clj-time.local :as l]
            [me.raynes.fs :as fs]



            [gpserver.controller.home :as home]
            [ring.util.response :refer [file-response]]
            [gpserver.public.common :as commonfunc]
            [clojure.java.io :as io]))

(defn home-page []



  #_(println (format/as-file (resize-to-width (io/file (str commonfunc/datapath "upload/1450057913770title.png")) 21 )
                        (str commonfunc/datapath "upload/big/1450057913770title.png")))


  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))



(defroutes home-routes
  (GET "/" [] (home-page))

  (GET "/hellogpapp" [] (ok {:success true}))


  (GET "/about" [] (about-page))


  (GET "/login" [username password] (home/login username password))


  (GET "/newuser" [username realname password usertype] (home/newuser username realname password usertype))

  (GET "/articles" [] (home/articles-page))

  (GET "/getarticlesbytypeandtime" [type time] (home/getarticlesbytypeandtime type time))


  (POST "/addarctile" [title titleimage type source content]

        (home/addarctile title titleimage type source content)

        )



  (POST "/savearctile" [title titleimage type source content id]

        (home/savearctile title titleimage type source content id)

        )


  (POST "/addgroupmessage" [content ftype fromid toid groupid mtype toname fromname] (home/addgroupmessage content ftype fromid toid groupid mtype toname fromname))



  (GET "/getunreadmessages" [userid usertype] (home/getunreadmessages userid usertype))



  (GET "/getgroupmessagehistory" [fromid groupid time] (home/get-group-message-history fromid groupid time))

  (POST "/getgroupmessagehistory" [fromid groupid time] (home/get-group-message-history fromid groupid time))



  (POST "/uploadfile"  [file filename]

        ;(println file)
    (try
      (do
       (let [
          uploadpath  (str commonfunc/datapath "upload/")
          timenow (c/to-long  (l/local-now))
          filename (str timenow (:filename file) (when-not (nil? filename) filename))
          ]
      ;(println file)

      (nio/upload-file uploadpath  (conj file {:filename filename}))

      (when (= (subs  (:content-type file) 0 5) "image")


        (fs/rename (format/as-file (resize-to-width (io/file (str commonfunc/datapath "upload/" filename)) 100 )
                        (str commonfunc/datapath "upload/" filename))
                   (str commonfunc/datapath "upload/" "small" filename)

                   )

        )


      (ok {:success true :filename filename :name (:filename file) :filetype (:content-type file)})
      )
      )
    (catch Exception ex

      (ok {:success false :message (.getMessage ex)})

      ))


    )


  (GET "/files/:filename" [filename]

    (file-response (str commonfunc/datapath "upload/" filename))

    )

  (GET "/article/:articleid" [articleid]

    (home/arctiledetail articleid)

    )
  (GET "/getarticlebyid" [articleid]

    (home/getarticlebyid articleid)

    )


  )



