(defproject gridworld "0.1.0-SNAPSHOT"
  :description "BURLAP 'Hello GridWorld!' tutorial in Clojure"
  :url "http://burlap.cs.brown.edu/tutorials/hgw/p1.html"
  :license {:name "Apache License, Version 2.0"
            :url  "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [edu.brown.cs.burlap/burlap "3.0.1"]]
  :main ^:skip-aot gridworld.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
