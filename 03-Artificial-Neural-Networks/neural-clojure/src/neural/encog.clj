(ns neural.encog
  (:use [enclog nnets training]))

; AJW lots of reflection warnings on enclog
; demo: (ec-new mlp)
;       (ec-train mlp)
;       (ec-test MLP)

(defn round-output
  "Round outputs to nearest integer."
  [output]
  (mapv #(Math/round ^Double %) output))

(def mlp (atom nil))
(def MLP (atom nil))

(defn train-network [network data trainer-algo]
  (let [trainer (trainer trainer-algo
                         :network network
                         :training-set data)]
    (train trainer 0.01 10000 [])))

(def xor-dataset
  (let [xor-input [[0.0 0.0] [1.0 0.0] [0.0 1.0] [1.0 1.0]]
        xor-ideal [[0.0]     [1.0]     [1.0]     [0.0]]]
        (data :basic-dataset xor-input xor-ideal)))


(defn run-network [network input]
  (let [input-data (data :basic input)
        output     (.compute network input-data)
        output-vec (.getData output)]
    (round-output output-vec)))


(defn ec-new [net]
   (reset! net (network (neural-pattern :feed-forward)
                  :activation :sigmoid
                  :input      2
                  :output     1
                  :hidden     [3])))

(defn ec-train [net]
   (reset! MLP (train-network @net xor-dataset :back-prop)))
           
(defn ec-test [net]
  (println "[0 0] => " (run-network @net [0 0]))
  (println "[0 1] => " (run-network @net [0 1]))
  (println "[1 0] => " (run-network @net [1 0]))
  (println "[1 1] => " (run-network @net [1 1])))

;; user> (def MLP (train-network mlp dataset :back-prop))
;; Iteration # 1 Error: 26.461526% Target-Error: 1.000000%
;; Iteration # 2 Error: 25.198031% Target-Error: 1.000000%
;; Iteration # 3 Error: 25.122343% Target-Error: 1.000000%
;; Iteration # 4 Error: 25.179218% Target-Error: 1.000000%
;; ...
;; ...
;; Iteration # 999 Error: 3.182540% Target-Error: 1.000000%
;; Iteration # 1,000 Error: 3.166906% Target-Error: 1.000000%
;; #'user/MLP

;; compute function will return a number of type [BasicMLData:0.8860386884769196]>
;; https://github.com/encog/encog-java-core/blob/master/src/main/java/org/encog/neural/networks/BasicNetwork.java

;; user> (run-network MLP [1 1])
;; [0]
;; user> (run-network MLP [1 0])
;; [1]
;; user> (run-network MLP [0 1])
;; [1]
;; user> (run-network MLP [0 0])
;; [0]
