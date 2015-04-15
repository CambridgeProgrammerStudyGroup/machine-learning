(ns neural.perceptron)


(declare classify inner-product learn-all)

(def num-inputs (atom 4))
(def threshold (atom 10.0))
(def weights (atom []))

(def total-updates (atom 0))
(def iterations (atom 0))
(def positives (atom []))
(def negatives (atom []))

;; ---
;; ajw
;; ---

(defn null? [x]
      (or (and (coll? x) (empty? x)) (nil? x)))
  
(defn setw [a n nw]
   (reset! a (assoc @a n nw)))

(defn add-bias [x]
  (into [] (concat x [1])))

(defn init-p []
   (reset! num-inputs (inc (count (first @positives))))
   (reset! weights (into [] (repeat @num-inputs 0.0)))
)   

(defn print-weights [w]
  "Neatly prints WEIGHTS."
  (map println @w))
   
;;; TRAIN takes the EXAMPLE and adjusts the weights of the
;;; perceptron by DELTA at a time, until the example is classified
;;; positively (if POS-NEG is '+) or negatively (if POS-NEG is '-).
;;; If DELTA is small, TRAIN may use many iterations to converge.

(defn train [example pos-neg delta]
  "Repeatedly adjusts *WEIGHTS* until EXAMPLE is
   classified correctly."
  (reset! iterations 0)
  (loop [] ; [open (list start-node) closed '() path '{}]    
    (if (= (classify example) pos-neg) 
      'exit
      (do
       (dotimes [i @num-inputs]
          (setw weights i (apply pos-neg 
                  [(nth @weights i) (* delta (nth example i))] )))
          (swap! total-updates + 1)
          (swap! iterations + 1)
          (recur))))
        (println "iterations: " @iterations))
  
;;; CLASSIFY uses the current weights to decide whether
;;; the given input example should be accepted (+) or
;;; rejected (-).

(defn classify [example]
  "Returns '+ if the EXAMPLE is classified by the
   current weights as positive; '- otherwise."
  (if (< (inner-product (add-bias example) @weights) @threshold)
      - +))

;;; INNER-PRODUCT computes the sum of the componentwise
;;; products for sequences X and Y.

(defn inner-product [x y]
  "Returns the vector inner product for X and Y."
  (if (or (null? x)(null? y)) 0.0
      (+ (* (first x)(first y))
         (inner-product (rest x)(rest y)) ) ) )

;;; ALLTRAIN attempts to use each of the positive and negative
;;; examples to train the perceptron.  Note that this only makes
;;; one pass through the examples, and multiple passes are often
;;; necessary before all examples can be correctly classified
;;; using the same set of weights.

(defn all-train [delta]
  "Makes a training pass through all the examples."
  (doseq [x @positives] (train (add-bias x) + delta))
  (doseq [x @negatives] (train (add-bias x) - delta)) 
)

(defn show-all []
  "Returns T if all examples are correctly classified using
   the current weights."
    (do              
      (doseq [x @positives]
        (println x " => " 
           (if (= (classify (add-bias x)) -) 
              "negative" "positive")))
      (doseq [x @negatives]
        (println x " => " 
           (if (= (classify (add-bias x)) -) 
              "negative" "positive")))
      (println "weights: " @weights)
      
      )) 



;;; TESTALL returns T if all examples are classified correctly
;;; using the current weighting values.  It throws NIL as soon
;;; as it finds any example that is misclassified.

(defn test-all []
  "Returns T if all examples are correctly classified using
   the current weights."
  (try
    (let []              
      (doseq [x @positives]
        (if (= (classify (add-bias x)) -) (throw (Throwable. false))))
      (doseq [x @negatives]
        (if (= (classify (add-bias x)) +) (throw (Throwable. false)))))
      true
    (catch Throwable e false)))     

(defn test-count []
  "Returns T if all examples are correctly classified using
   the current weights."
    (let [cnt (atom 0)]              
      (doseq [x @positives]
        (if (= (classify (add-bias x)) +) (swap! cnt + 1)))
      (doseq [x @negatives]
        (if (= (classify (add-bias x)) -) (swap! cnt + 1)))
      @cnt))
 


;;; LEARNALL repeatedly calls ALLTRAIN until TESTALL returns T.
;;; Note that LEARNALL will go into an infinite loop if the
;;; sets of positive and negative examples cannot be handled
;;; correctly by any one-layer perceptron.
;;; LEARNALL also prints out counts of the training steps,
;;; for each run of TESTALL and for the entire LEARNALL run.
;;; Note that the strategy used here is to reduce DELTA by
;;; 20 percent in each top-level loop iteration.

(defn learn-all [attempts]
  "Keeps calling ALLTRAIN and TESTALL until all examples
   are correctly classified."
  (reset! total-updates 0)
  (let [epochs (atom 0)
        delta (atom 0.5)]
    (loop [] (if (or (test-all) 
                     (and (> attempts 0) (> @epochs attempts))) (test-count)
      (do  
        (println "Test Count: " (test-count))
        (swap! epochs + 1)
        (println "Beginning epoch " @epochs)
        (all-train @delta)
        (println "Total updates: " @total-updates)
        (println "Current weights: " @weights)
        (swap! delta * 0.8) 
        (recur))
     ) )))

(defn nsc [] (ns neural.core))