(ns gpserver.public.websocket
  (:use org.httpkit.server)
   (:use ruiyun.tools.timer)
  (:require
    [clojure.data.json :as json]
    [taoensso.timbre :as timbre]

    [cheshire.core :refer :all]


    )
  )

(def channel-hub (atom {}))



(defn scheduleFunc []
  (timbre/info "timer  schedule  fire")
      (doseq [channel (keys @channel-hub)]
     (do
        (timbre/info "firesenheat-jump : " channel )

        (send! channel (generate-string
                       {
                          :type "heartjump"
                         }
                       )
        false)


        )




    )
      )



(defn start-schedule []

  (timbre/info "timer  schedule  started")


  (run-task! #(scheduleFunc) :period 30000 )

  )


(defn handler [request]
  (with-channel request channel

                (on-receive channel (fn [data]

                                      (let [itemdata (json/read-str data)]
                                        (when-not (nil? (get itemdata "userid"))

                                          (do (swap! channel-hub assoc channel itemdata)

                                            (timbre/info "new connected")
                                            )
                                          )

                                        )

                                      (timbre/info data)

                                      ))
                (on-close channel (fn [status]

                                    (timbre/info channel " disconnected. status: "  )
                                    (swap! channel-hub dissoc channel)

                                    ))))




(defn start-server [port]

  (run-server handler {:port port :max-body 16388608 :max-line 16388608})

  (timbre/info (str "gpserver-websocket started successfully on port" port))

  )
