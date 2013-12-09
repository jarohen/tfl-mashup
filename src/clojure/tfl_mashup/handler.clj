(ns tfl-mashup.handler
  (:require [ring.util.response :refer [response]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [api]]
            [hiccup.page :refer [html5 include-css include-js]]
            [frodo :refer [repl-connect-js]]))

(defn page-frame []
  (html5
   [:head
    [:title "tfl-mashup - CLJS Single Page Web Application"]
    (include-js "//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js")
    (include-js "//cdn.leafletjs.com/leaflet-0.7.1/leaflet.js")
    (include-js "//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js")
    (include-css "//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css")
    (include-css "//cdn.leafletjs.com/leaflet-0.7.1/leaflet.css")
    (include-js "/js/tfl-mashup.js")]
   [:body
    [:div.container
     [:div#content]
     [:script (repl-connect-js)]]]))

(defroutes app-routes
  (GET "/" [] (response (page-frame)))
  (resources "/js" {:root "js"}))

(def app 
  (-> app-routes
      api))
