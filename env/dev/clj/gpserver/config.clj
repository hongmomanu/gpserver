(ns gpserver.config
  (:require [selmer.parser :as parser]
            [taoensso.timbre :as timbre]
            [gpserver.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (timbre/info "\n-=[gpserver started successfully using the development profile]=-"))
   :middleware wrap-dev})
