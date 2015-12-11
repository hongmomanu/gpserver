(ns gpserver.public.common
  (:use compojure.core)
  (:require
            [clojure.data.json :as json]
            )
  )





(def datapath (str (System/getProperty "user.dir") "/"))
