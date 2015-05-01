(ns neural.encog
  (:use [enclog nnets training]))

; AJW lots of reflection warnings on enclog

(def dataset (atom []))
(def n-inputs (atom 0))
(def n-outputs (atom 0))

(def inputs (atom []))
(def ideal (atom []))

(def test-in (atom []))
(def test-out (atom []))

(defn round-output
  "Round outputs to nearest integer."
  [output]
  (mapv #(Math/round ^Double %) output))

(def mlp (atom nil))
(def MLP (atom nil))

(defn train-network [network data target iterations trainer-algo]
  (let [trainer (trainer trainer-algo
                         :network network
                         :training-set data)]
    (train trainer target iterations [])))

(defn run-network [network input]
  (let [input-data (data :basic input)
        output     (.compute network input-data)
        output-vec (.getData output)]
    (round-output output-vec)))


(defn ec-dataset [in id tin tid]
  (reset! inputs in)
  (reset! ideal id)
  (reset! test-in tin)
  (reset! test-out tid)
  (reset! n-inputs (count (first @inputs)))
  (reset! n-outputs (count (first @ideal)))
  (reset! dataset (data :basic-dataset in id)))
  
;; ---
;; xor
;; ---

(defn ec-new [hidden]
   (reset! mlp (network (neural-pattern :feed-forward)
                  :activation :sigmoid
                  :input      @n-inputs
                  :output     @n-outputs
                  :hidden     [hidden])))

(defn ec-train [p]
   (reset! MLP (train-network @mlp @dataset 
                              (p :target-error)
                              (p :iterations) 
                              :back-prop)))
         
(defn xor-test []
  (dotimes [i (count @ideal)]
     (let [ri (run-network @MLP (@inputs i))]
     (println
       i " => " ri " ideal: "  (@ideal i)))))


(defn which-digit 
  ([ideal] (which-digit ideal 0))
  ([ideal n] (if (zero? (nth ideal n))
       (which-digit ideal (inc n)) n)))        

(defn ec-test []
  
  (let [good (atom 0)
        bad (atom 0)
        good-v (atom [])
        bad-v (atom [])]
  
  (dotimes [i (count @test-out)]
     (let [ri (run-network @MLP (@test-in i))]
    
        (if (not (= ri (map #(int %) (@test-out i))))  
    (do (println 
           i " => " ri " ideal: "  (@ideal i))
        (swap! bad + 1)
        (reset! bad-v 
          (conj @bad-v 
            (into [] (map str (into [] (cons (which-digit (@ideal i)) (@test-in i))))))))
    (do (swap! good + 1)
         (reset! good-v 
          (conj @good-v 
            (into [] (map str (into [] (cons (which-digit (@ideal i)) (@test-in i))))))))
     
     )))
  
      
  
     (println "good: " @good "bad: " @bad)
     [@good-v @bad-v]
     ))


(defn core [] (ns neural.core))
