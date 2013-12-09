(defproject tfl-mashup ""

  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.5.1"]

                 [ring/ring-core "1.2.0"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.4"]

                 [prismatic/dommy "0.1.1"]

                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 [org.clojure/clojurescript "0.0-2080"]
                 [org.clojure/tools.reader "0.8.0"]]

  :plugins [[jarohen/lein-frodo "0.2.5"]
            [lein-cljsbuild "1.0.0"]
            [lein-pdo "0.1.1"]
            [com.keminglabs/cljx "0.3.1"]]

  :frodo/config-resource "tfl-mashup-config.edn"

  :source-paths ["src/clojure" "target/generated/clj"]

  :resource-paths ["resources" "target/resources"]

  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "target/generated/clj"
                   :rules :clj}

                  {:source-paths ["src/cljx"]
                   :output-path "target/generated/cljs"
                   :rules :cljs}]}

  :cljsbuild {:builds [{:source-paths ["src/cljs" "target/generated/cljs"]
                        :compiler {:output-to "target/resources/js/tfl-mashup.js"
                                   :output-dir "target/resources/js/"
                                   :optimizations :whitespace
                                   :pretty-print true

                                   ;; uncomment for source-maps
                                        ; :source-map "target/resources/js/tfl-mashup.js.map"
                                   }}

                       {:source-paths ["src/cljs" "target/generated/cljs"]
                        :id "prod"
                        :compiler {:output-to "target/resources/js/tfl-mashup.js"
                                   :optimizations :advanced
                                   :pretty-print false
                                   :externs ["externs/jquery.js"]}}]}

  :aliases {"dev" ["pdo" "cljx" "auto," "cljsbuild" "auto," "frodo"]
            "start" ["do" "cljx" "once," "cljsbuild" "once" "prod," "trampoline" "frodo"]})
