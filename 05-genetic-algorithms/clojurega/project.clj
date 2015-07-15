(defproject clojurega "0.0.1"
  :description "Genetic algorithm playground"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [net.mikera/core.matrix "0.36.1"]]
  :main ^:skip-aot clojurega.core
  :target-path "build/%s"
  :profiles {:uberjar {:aot :all}})
