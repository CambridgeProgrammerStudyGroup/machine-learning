(defproject search "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
 
                 [org.encog/encog-core "3.2.0"]
                 [enclog "0.6.5"]
                 
                 [org.clojure/data.csv "0.1.2"]
                 
                 [cc.artifice/clj-ml "0.5.1"]
                 [incanter "1.5.6"]]    ; going to 1.9.0 doesn;t work
    
  :plugins [[lein-autoexpect "1.4.0"]]
  :repl-options {
             ;; If nREPL takes too long to load it may timeout,
             ;; increase this to wait longer before timing out.
             ;; Defaults to 30000 (30 seconds)
             :timeout 120000
             }
  :aot [neural.core]
  :main neural.core
)

