(ns tfl-mashup.cljs.home
  (:require [dommy.core :as d]
            [blade :refer [L]])
  (:require-macros [dommy.macros :refer [node sel1]]))


(defn on-location-found [e]
  (.log js/console (.-latlng e)))

(def tile-url
  "http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png")

(defn watch-hash! [!hash]
  (add-watch !hash :home-page
             (fn [_ _ _ hash]
               (when (= "#/" hash)
                 (d/replace-contents! (sel1 :#content)
                                      (node [:div
                                              [:h2 {:style {:margin-top :1em}}
                                                   "Hello world from ClojureScript!"]
                                              [:div#map {:style {:width "800px" :height "600px"}}]
                                             ]))
                 (let [map (-> L (.map "map") (.setView (clj->js [51.505 -0.09]) 13))]
                   (-> L (.tileLayer tile-url (clj->js {:maxZoom 18 :attribution "OpenStreetMap"}))
                       (.addTo map))
                    (.on map "locationfound" on-location-found)
                    (.locate map (clj->js {:setView true :maxZoom 16})))
                 ))))
