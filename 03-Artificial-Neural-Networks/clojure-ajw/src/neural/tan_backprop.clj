(ns neural.tan-backprop)

;;; In this example, the net learns to distinguish between
;;; horizontal and vertical patterns.

;;; With *ETA* set to 0.1 and fixed, the 16-example training set
;;; gets convergence to a system error of 0.02 in about 500 epochs.
;;; This program runs on a Macintosh IIci with MCL at the rate of about 200
;;; epochs every 5 minutes.  Therefore 1000 epochs requires about 25min.

;;; Learning proceeds by running the backpropagation algorithm on
;;; each training example, accumulating the proposed weight changes
;;; in an array,  "increments".  After each example has been processed,
;;; the "epoch" ends and the weight are updated using the increments.
;;; The main procedure shows a display of the weight and activation
;;; levels every 100 epochs.

;;; The program computes the "system error" in each iteration, which is
;;; the average squared error on the examples in the training set.

(def n-inputs (atom 9))
(def n-hidden (atom 5))
(def n-outputs (atom 2))

;;; Set *NMAX* as the maximum of
;;;   (*NINPUTS* + 1), (*NHIDDEN* + 1), and *NOUTPUTS*.
;;; This is because the input layer and the hidden layer each have
;;; a "dummy unit" whose activation is always 1, which feeds the units
;;; of the next layer in lieu of their having a "threshold" or "bias".
;;; The weight for the threshold links are learned just like all
;;; the others.

(def n-max (atom 10))
(def n-examples (atom 16))
(def beta (atom 1.0))
(def eta-start (atom 0.1))
(def eta-run-thresh (atom 5))
(def iterations (atom 0))
(def target-error (atom 0))

(def modulo (atom 10))

;;; Each weight is associated with the level of its tail endpoint,
;;; and is thus referenced with with an expression of them form
;;; (AREF *W* level from-node to-node).

(def weight (atom (make-array Float/TYPE 2 @n-max @n-max)))

;;; Used to accumulate the corrections to weight during an epoch.

(def increment (atom (make-array Float/TYPE 2 @n-max @n-max)))
  
;;; Unit activations ( = g(h) ).  
  
(def activation (atom (make-array Float/TYPE 3 @n-max)))
 
;;; Used to store the summed inputs to each unit  
  
(def summed-input (atom (make-array Float/TYPE 3 @n-max)))
                    
;;; Used to store the summed inputs to each unit.

;;; Used during backpropagation.

(def delta (atom (make-array Float/TYPE 3 @n-max)))

;;; Used to store the derivative of G, that is, G prime,
;;; evaluated on the H value of each node.

(def sigmoid-prime (atom (make-array Float/TYPE 3 @n-max)))

;;; Here is the training set.
;;; Each input vector is given on a separate line.

(def example-inputs (atom []))

(def example-outputs (atom [])) 

; ----
  
(def level-size (atom (make-array Integer/TYPE 3)))

(def sim-level-size (atom (make-array Integer/TYPE 3)))

(def eta (atom 0)) ; ajw !

;; ==========
;; initialise
;; ==========

(defn init []
  "Initializes the *LEVELSIZE* arrays and the weight."
    
  (reset! n-max (inc (max @n-inputs @n-hidden @n-outputs)))  
  
  ; Float arrays
  
  (reset! weight (make-array Float/TYPE 2 @n-max @n-max))
  (reset! increment (make-array Float/TYPE 2 @n-max @n-max))
  (reset! activation (make-array Float/TYPE 3 @n-max))
  (reset! summed-input (make-array Float/TYPE 3 @n-max))
  (reset! delta (make-array Float/TYPE 3 @n-max))
  (reset! sigmoid-prime (make-array Float/TYPE 3 @n-max))

  ; Level size arrays
  
  (aset @level-size 0 (inc @n-inputs))
  (aset @level-size 1 (inc @n-hidden))
  (aset @level-size 2 (inc @n-outputs))

  ;;; Note that the input layer and the hidden layer each have an
  ;;; extra unit, whose activation is always set to 1, and whose
  ;;; outgoing weight serve as thresholds for other units.

  (aset-float @activation 0 @n-inputs 1.0)
  (aset-float @activation 1 @n-hidden 1.0)
    
  ;;; The simulated numbers of units are kept, also:

  (aset @sim-level-size 0 @n-inputs)
  (aset @sim-level-size 1 @n-hidden)
  (aset @sim-level-size 2 @n-outputs)


  ;;; Now initialize the weight to small random values:
  
  (dotimes [level 2]
    (dotimes [i (aget @level-size level)]
       (dotimes [j (aget @sim-level-size (inc level))]
          (aset-float @weight level i j
               (/ (rand 100) 500.0) )
     ) ) )

  ;;; Initialize the step size for weight adjustment:
  
  (reset! eta @eta-start)
  
)



(defn sigmoid [h]
  "Returns the value of the sigmoid function at H."
  (/ (inc (Math/pow Math/E (* -2.0 @beta h)))) )  ; ajw why * 2.0?

(defn feedforward [input-example]
  "Determines unit activations for INPUT-EXAMPLE."
  (let [sum (atom 0.0)
        gval (atom 0.0)
        p input-example]
        
    ;;; Copy input activations:
    
    (dotimes [i @n-inputs]                               ; ajw
        (aset-float @activation 0 i (p i)))
    
    ;;; Compute activations at next 2 levels:
        
    (dotimes [level 2]
      (dotimes [j (aget @sim-level-size (inc level))]
        (reset! sum 0.0)

        (dotimes [i (aget @level-size level)]
          (reset! sum (+ @sum (* (aget @activation level i)
                                (aget @weight level i j) ))))

        (aset-float @summed-input (inc level) j @sum)

        (reset! gval (sigmoid @sum))
        (aset-float @activation (inc level) j @gval)

        (aset-float @sigmoid-prime (inc level) j
              (* 2.0 @beta @gval (- 1.0 @gval)))
        ) ) ) )

;;; ==============
;;; training epoch
;;; ==============

;;; The following procedure takes one input/output pair,
;;; determines the error at each output using the current
;;; weight, and uses backpropagation to compute the changes
;;; that should be made to the weight.  These changes are
;;; added to the pre-existing collection of changes, maintained
;;; in the array *INCREMENT*.

(defn backprop-one-example [input-example desired-output]
  "Uses one I/O example to adjust the weight."
  (let [sum (atom 0.0) 
        example-error (atom 0.0)
        temp (atom 0.0)]

    (feedforward input-example)
    
    ;;; Compute *DELTA* values for output layer:
        
    (dotimes [i @n-outputs]
      (aset-float @delta 2 i
        (* (aget @sigmoid-prime 2 i)
           (- (desired-output i)
              (aget @activation 2 i) ))))
 
    ;;; Compute *INCREMENT* values for arcs coming
    ;;; into output layer:
    
    (let [level 1]

      (dotimes [i (aget @level-size level)]
        (reset! sum 0.0)
        (dotimes [j (aget @sim-level-size (inc level))]
          (reset! sum (+ @sum (* (aget @weight level i j)
                                (aget @delta (inc level) j) )))
          (aset-float @increment level i j
            (+ (aget @increment level i j)
               (* @eta
                  (aget @delta (inc level) j)
                  (aget @activation level i) ) ) )
          
         ;;; Compute *DELTA* values for hidden layer:
       
         (if (not (= i @n-hidden))
           (aset-float @delta level i
                 (* (aget @sigmoid-prime level i) @sum) ) ) )))


    ;;; Compute *INCREMENT* values for hidden layer's
    ;;; incoming arcs:
      
    (let [level 0]

      (dotimes [i (aget @level-size level)]
        (reset! sum 0.0)
        (dotimes [j (aget @sim-level-size (inc level))]
          (reset! sum (+ @sum (* (aget @weight level i j)
                                (aget @delta (inc level) j) )))
          (aset-float @increment level i j
            (+ (aget @increment level i j)     
               (* @eta
                  (aget @delta (inc level) j)
                  (aget @activation level i) ) ) ) ) ))

    ;;; Compute the sum-squared error for this example:

    (let [example-error (atom 0.0)]

      (dotimes [i @n-outputs]
        (let [temp (- (desired-output i)
                    (aget @activation 2 i) )]
          (reset! example-error (+ @example-error (* temp temp)))))
      @example-error) 
))   
    
(defn clear-increments []
  "Sets the increments to zero for the beginning
   of a new epoch."
  (dotimes [level 2]
    (dotimes [i @n-max]
      (dotimes [j @n-max]
        (aset-float @increment level i j 0.0) ) ) ) )

(defn apply-increments []
  "Changes the weight according to the increments computed
   during the epoch."
  (dotimes [level 2]
    (dotimes [i (aget @level-size level)]
      (dotimes [j (aget @sim-level-size (inc level))]
        (aset-float @weight level i j
              (+ (aget @weight level i j)
                 (aget @increment level i j)))))))

(defn training-epoch []
  "Makes a pass through all the training examples,
   accumulating the increments to the weight until the end,
   and finally adjusts the weight.
   Returns the average system error for the examples."

  (clear-increments)

  (let [sum (atom 0.0)]
    (dotimes [i @n-examples]
      (reset! sum (+ @sum
          (backprop-one-example
              (@example-inputs i)
              (@example-outputs i)) ) ) )

    (apply-increments)

    (/ @sum @n-examples) ) )

;; ========
;; backprop
;; ========
;; Runs successive training epochs
;;  and monitors results


(defn show-outputs []  
  (println "The output vector is: ")
  (dotimes [i @n-examples]
    (feedforward (@example-inputs i))
    (print 
      (if (> (count (@example-inputs i)) 4) i (@example-inputs i))
      " => "
      (map #(format "%.6f" (aget @activation 2 %)) (range @n-outputs))
      " ideal: "
      (@example-outputs i)
      "\n")))


(defn print-status [epoch system-error]
      (println "At end of epoch #" (format "%5d" epoch) ", " 
                    "system error = " (format "%.6f" @system-error) 
                    " eta " (format "%.6f" @eta) ))

(defn backprop []
  "Performs training of the neural network for many epochs."
  (println "Beginning neural net training with backpropagation.")

  (let [num-consec-wins (atom 0) ; number of consecutive epochs with improvements.
        last-error (atom 1.0)    ; an arbitrary high value.
        system-error (atom 0.0)] 

    (loop [epoch 1]
      (reset! system-error (training-epoch))
      
      (if (or (< @system-error @target-error)
              (> epoch @iterations))
        
        (do
            (print-status epoch system-error) 
            (show-outputs))
        
        (do
      
      ;;;   Adaptively modify eta to improve training rate:

      (if (< @system-error @last-error)
          (do
            (swap! num-consec-wins + 1)
            (if (> @num-consec-wins @eta-run-thresh)
                (do
                  (swap! eta + 0.01)
                  (reset! num-consec-wins 0) ) ) )
          (do
            (swap! eta * 0.8)
            (reset! num-consec-wins 0) ) )
      
      ;;;  Prepare for next epoch
      
      (if (zero? (mod epoch @modulo))
          (do (print-status epoch system-error) ))   
      (reset! last-error @system-error)
      (recur (inc epoch)) ) ) )))

;; ============
;; tan-backprop
;; ============
;; Setupparameters for run


(defn tan-backprop [p] 
                   
  (reset! n-hidden (p :n-hidden))
  (reset! beta (p :beta))   
  (reset! eta-start (p :eta-start))
  (reset! eta-run-thresh (p :eta-run-thresh))
  (reset! iterations (p :iterations))
  (reset! target-error (p :target-error))
  
  (reset! modulo (or (p :modulo) 10))
  
  ; Settings worked out from training set
  
  (let [ts (p :training-set)]
  
    (reset! n-inputs (count (first (first ts))))
    (reset! n-outputs (count (second (first ts))))
    (reset! example-inputs (into [] (map first ts)))
    (reset! example-outputs (into [] (map second ts)))  
    (reset! n-examples (count ts)))
  
  (init)
  (backprop)
)

;; ======
;; unused
;; ======


(defn show-weight []
  "Displays the current weight."
  (dotimes [level 2]
    (println "Incoming weight for level " (inc level) ":")
    (dotimes [j (aget @sim-level-size (inc level))]
      (println " For unit (" (inc level) "," j ")")
      (dotimes [i (aget @level-size level)]
        (println (aget @weight level i j)) ))))

(defn show-activations []
  "Displays the current unit activation levels."
  (dotimes [level 2]
    (dotimes [j (aget sim-level-size (inc level))]
      (println "Activation for node (" (inc level) "," j ") = "
                    (aget activation (inc level) j) ) ) )
  
  ;;; Report the input and output values:
  
  (println "The input vector is: ")
  (dotimes [i n-inputs]
    (println (aget activation 0 i)) )
  
  (println "The output vector is: ")
  (dotimes [i n-outputs]
    (println (aget activation 2 i)) )
 )



;; ==
;; ns
;; ==

(defn core [] (ns neural.core))




