(defproject search "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.encog/encog-core "3.2.0"]
                 [enclog "0.6.5"]]
  :plugins [[lein-autoexpect "1.4.0"]]
  :aot [neural.core]
  :main neural.core
)

