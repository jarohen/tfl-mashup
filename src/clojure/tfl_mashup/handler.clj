(ns tfl-mashup.handler
  (:require [ring.util.response :refer [response]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [api]]
            [hiccup.page :refer [html5 include-css include-js]]
            [frodo :refer [repl-connect-js]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [clj-http.client :as http]
            [chord.http-kit :refer [with-channel]]
            [clojure.tools.reader.edn :as edn]
            [clojure.core.async :as a :refer [<! >! go go-loop]]))

(defn page-frame []
  (html5
   [:head
    [:title "Location Tracker - CLJS Single Page Web Application"]
    (include-js "//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js")
    (include-js "//cdn.leafletjs.com/leaflet-0.5/leaflet.js")
    (include-js "//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js")
    (include-css "//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css")
    (include-css "//cdn.leafletjs.com/leaflet-0.5/leaflet.css")
    (include-js "/js/tfl-mashup.js")]
   [:body
    [:div.container
     [:div#content]
     [:script (repl-connect-js)]]]))

(defn send-locations! [users]
  (let [message (pr-str (vals users))]
    (doseq [conn (keys users)]
      (a/put! conn message))))

(defn handle-event [users {:keys [action conn details]}]
  (case action
    :joined (assoc users conn {})
    :update (assoc users conn details)
    :left (dissoc users conn)))

(let [users-ch (a/chan)]
  (go-loop [users {}]
    
    (let [tick (a/timeout 500)
          [v c] (a/alts! [tick users-ch])]
      (condp = c
        tick (recur (doto users send-locations!))
        users-ch (recur (handle-event users v)))))
  
  (defn user-connected! [conn]
    (a/put! users-ch {:action :joined :conn conn})
    (go-loop []
      (if-let [{:keys [message]} (<! conn)]
        (let [details (edn/read-string message)]
          (>! users-ch {:action :update
                        :conn conn
                        :details details})
          (recur))
        
        (>! {:action :left
             :conn conn})))))

(defn user-joined! [req]
  (with-channel req user-conn
    (user-connected! user-conn)))

(defroutes app-routes
  (GET "/" [] (response (page-frame)))
  (GET "/locations" {:as req}
    (user-joined! req))
  (resources "/js" {:root "js"}))

(def app 
  (-> #'app-routes
      (wrap-restful-format :formats [:edn :json-kw])
      api))
