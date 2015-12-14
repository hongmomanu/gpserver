(ns gpserver.routes.home
  (:require [gpserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok found]]

            [gpserver.controller.home :as home]
            [ring.util.response :refer [file-response]]
            [gpserver.public.common :as commonfunc]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))



(defroutes home-routes
  (GET "/" [] (home-page))

  (GET "/hellogpapp" [] (ok {:success true}))

  (GET "/about" [] (about-page))

  (GET "/articles" [] (home/articles-page))

  (GET "/getarticlesbytypeandtime" [type time] (home/getarticlesbytypeandtime type time))


  (POST "/addarctile" [title titleimage type source content]

        (home/addarctile title titleimage type source content)

        )

  (POST "/savearctile" [title titleimage type source content id]

        (home/savearctile title titleimage type source content id)

        )


  (GET "/files/:filename" [filename]

    (file-response (str commonfunc/datapath "upload/" filename))

    )

  (GET "/article/:articleid" [articleid]

    (home/arctiledetail articleid)

    )

  )



