(ns tfl-mashup.cljs.home
  (:require [dommy.core :as d]
            [blade :refer [L]])
  (:require-macros [dommy.macros :refer [node sel1]]))


(def tile-url
  "http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png")

(defn watch-hash! [!hash]
  (add-watch !hash :home-page
             (fn [_ _ _ hash]
               (when (= "#/" hash)
                 (d/replace-contents! (sel1 :#content)
                                      (node [:div
                                              [:h2 {:style {:margin-top :1em}}
                                                   "Hello world frfom ClojureScript!"]
                                              [:div#map {:style {:width "800px" :height "600px"}}]
                                             ]))
                 (let [map (-> L (.map "map") (.setView (clj->js [51.505 -0.09] 13)))
                       tile-layer (.tileLayer L tile-url {:attribution "whocares"})]
                   (.log js/console map)
                   (.log js/console tile-layer)
                   (.addTo tile-layer map))
                 ))))
