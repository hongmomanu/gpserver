(ns gpserver.config
  (:require [taoensso.timbre :as timbre]))

(def defaults
  {:init
   (fn []
     (timbre/info "\n-=[gpserver started successfully]=-"))
   :middleware identity})
