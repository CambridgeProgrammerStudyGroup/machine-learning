(ns neural.wali-backprop
  (:use incanter.core))

;; (ajw)
;; Documentatin from Wali
;; problem solved by assigning weights in Taniumoto manner

;; user> (run-binary NN  [0 1])
;; [1]
;; user> (run-binary NN  [1 0])
;; [1]


;; user> (run-binary NN  [0 0])
;; [0]
;; user> (run-binary NN  [1 1]) ;; incorrect output generated
;; [1]



(defprotocol NeuralNetwork
  (run        [network inputs])
  (run-binary [network inputs])
  (train-ann-new [network samples])
  (train-ann-old [network samples]))

;;; AJW
;;; With the original rand-list the training jams
;;; with the revised weight allocationtaken from tanimoto it works 

(defn rand-list
  "Create a list of random doubles between -epsilon and +epsilon."
  [len epsilon]
  ; (println "weights: " epsilon)
  (map (fn [x] (- (rand (* 2 epsilon)) epsilon))
       (range 0 len)))
           ;    (/ (rand 100) 500.0) )

(defn rand-list-tan
  "Create a list of random doubles between -epsilon and +epsilon."
  [len epsilon]
  ; (println "weights: " epsilon)
  (map (fn [x] (/ (rand 100) 500.0))
       (range 0 len)))

;; from docs:
;; wraps around clatrix
;; (def A2 (matrix [1 2 3 4 5 6 7 8 9] 3)) ; produces the same 3x3 matrix
;;

(defn random-initial-weights
  "Generate random initial weight matrices for given layers.
  layers must be a vector of the sizes of the layers."
  [layers epsilon]
  (for [i (range 0 (dec (length layers)))]
    (let [cols (inc (get layers i))     ; cols is no. of inputs + 1 (for bias)
          rows (get layers (inc i))]    ; rows is no. of neurons in next layer
      (matrix (rand-list-tan (* rows cols) epsilon) cols))))

;;;

(defn sigmoid
  "Apply the sigmoid function 1/(1+exp(-z)) to all
  elements in the matrix z."
  [z]
  (div 1 (plus 1 (exp (minus z)))))

;;;

(defn bind-bias
  "Add the bias input to a vector of inputs."
  [v]
  (bind-rows [1] v))

;;;

(defn matrix-mult
  "Multiply two matrices and ensure the result is also a matrix."
  [a b]
  (let [result (mmult a b)]
    (if (matrix? result)
      result
      (matrix [result]))))

(defn forward-propagate-layer
  "Calculate activations for layer l+1 given weight matrix
  between layer l and l+1 and layer l activations."
  [weights activations]
  (sigmoid (matrix-mult weights activations)))

(defn forward-propagate
  "Propagate activation values through a network's
  weight matrix and return output layer activation values."
  [weights input-activations]
  (reduce #(forward-propagate-layer %2 (bind-bias %1))
          input-activations weights))

;;;


(defn forward-propagate-all-activations
  "Propagate activation values through the network
  and return all activation values for all nodes."
  [weights input-activations]
  (loop [all-weights     weights
         activations     (bind-bias input-activations)
         all-activations [activations]]
    (let [[weights & all-weights'] all-weights
           last-iter?       (empty? all-weights')
           out-activations  (forward-propagate-layer
                             weights activations)
           activations'     (if last-iter? out-activations
                                (bind-bias out-activations))
           all-activations' (conj all-activations activations')]
      (if last-iter? all-activations'
          (recur all-weights' activations' all-activations')))))

(defn back-propagate-layer
  "Back propagate deltas (from layer l-1) and
  return layer l deltas."
  [deltas weights layer-activations]
  (let [bp (mult (matrix-mult (trans weights) deltas)
        (mult layer-activations (minus 1 layer-activations)))]

    bp))

;;;

(defn calc-deltas
  "Calculate hidden deltas for back propagation.
  Returns all deltas including output-deltas."
  [weights activations output-deltas]

  (let [hidden-weights     (reverse (rest weights))
        hidden-activations (rest (reverse (rest activations)))]
    (loop [deltas          output-deltas
           all-weights     hidden-weights
           all-activations hidden-activations
           all-deltas      (list output-deltas)]
      (if (empty? all-weights) all-deltas
        (let [[weights
               & all-weights']      all-weights
               [activations
                & all-activations'] all-activations
              deltas'        (back-propagate-layer
                               deltas weights activations)
              all-deltas'    (cons (rest deltas')
                                   all-deltas)]
          (recur deltas' all-weights' all-activations' all-deltas'))))))

(defn calc-gradients
  "Calculate gradients from deltas and activations."
  [deltas activations]
  (map #(mmult %1 (trans %2)) deltas activations))

;;;

(defn calc-error
  "Calculate deltas and squared error for given weights."
  [weights [input expected-output]]

  (let [activations    (forward-propagate-all-activations
                         weights (matrix input))
        output         (last activations)
        output-deltas  (minus output expected-output)
        all-deltas     (calc-deltas
                         weights activations output-deltas)
        gradients      (calc-gradients all-deltas activations)]
   ; (println (first output) (first output-deltas))
    (list gradients
          (sum (pow output-deltas 2)))))

;;;

(defn new-gradient-matrix
  "Create accumulator matrix of gradients with the
  same structure as the given weight matrix
  with all elements set to 0."
  [weight-matrix]
  (let [[rows cols] (dim weight-matrix)]
    (matrix 0 rows cols)))

;;; [ajw] why was total error initialised to 1?

(defn calc-gradients-and-error' [weights samples]
  (loop [gradients   (map new-gradient-matrix weights)
         total-error 0
         samples     samples]
    (let [[sample
           & samples']     samples
           [new-gradients squared-error] 
                     (calc-error weights sample)
            gradients'        (map plus new-gradients gradients)
            total-error'   (+ total-error squared-error)]
      (if (empty? samples')
        (list gradients' total-error')
        (recur gradients' total-error' samples')))))

;;;

(defn calc-gradients-and-error
  "Calculate gradients and MSE for sample
  set and weight matrix."
  [weights samples]
  (let [num-samples   (length samples)
        [gradients
         total-error] (calc-gradients-and-error'
                        weights samples)] 
   (let [lgm (list
          (map #(div % num-samples) gradients)    ; gradients
          (/ total-error num-samples))]
    ;  (println "===" (first lgm) "===")
     lgm
     
     
     )))          ; MSE

;;;

(defn gradient-descent-complete?
  "Returns true if gradient descent is complete."
  [network iter mse]
  (let [options (:options network)]
    (or (>= iter (:max-iters options))
        (< mse (:desired-error options)))))

;;;

(defn apply-weight-changes
  "Applies changes to corresponding weights."
  [weights changes]
  (map plus weights changes))

(defn gradient-descent
  "Preform gradient descent to adjust network weights."
  [step-fn init-state network samples]
  (loop [network network
         state init-state
         iter 0]
    (let [iter     (inc iter)
          weights  (:weights network)
          [gradients mse]    
            (calc-gradients-and-error weights samples)]
      (if (gradient-descent-complete? network iter mse)
        network
        (let [[changes state] (step-fn network gradients state)
              new-weights     (apply-weight-changes weights changes)
              network         (assoc network :weights new-weights)]
          (recur network state iter))))))

;;;

(defn calc-weight-changes
  "Calculate weight changes:
  changes = learning rate * gradients +
            learning momentum * deltas."

  [gradients deltas learning-rate learning-momentum]
  (map #(plus (mult learning-rate %1)
              (mult learning-momentum %2))
       gradients deltas))

(defn bprop-step-fn [network gradients deltas]
  (let [options             (:options network)
        learning-rate       (:learning-rate options)
        learning-momentum   (:learning-momentum options)
        changes             (calc-weight-changes
                              gradients deltas
                              learning-rate learning-momentum)]
    [(map minus changes) changes]))

(defn gradient-descent-bprop [network samples]
  (let [gradients (map new-gradient-matrix (:weights network))]
    (gradient-descent bprop-step-fn gradients
                      network samples)))

;;;

(defn round-output
  "Round outputs to nearest integer."
  [output]
  (mapv #(Math/round ^Double %) output))




(defrecord MultiLayerPerceptron [options]
  NeuralNetwork

  ;; Calculates the output values for the given inputs.
  (run [network inputs]
    (let [weights (:weights network)
          input-activations (matrix inputs)]
      (forward-propagate weights input-activations)))

  ;; Rounds the output values to binary values for
  ;; the given inputs.
  (run-binary [network inputs]
    (round-output (run network inputs)))

 ; (run-raw [network inputs]
 ;   (raw-output (run network inputs)))
  
  ;; Trains a multilayer perceptron ANN from sample data.
  (train-ann-new [network samples]
    (let [options         (:options network)
          hidden-neurons  (:hidden-neurons options)
          epsilon         (:weight-epsilon options)
          [first-in
           first-out]     (first samples)
          num-inputs      (length first-in)
          num-outputs     (length first-out)
          sample-matrix   (map #(list (matrix (first %))
                                      (matrix (second %)))
                               samples)
          layer-sizes     (conj (vec (cons num-inputs
                                           hidden-neurons))
                                num-outputs)
          new-weights     (random-initial-weights layer-sizes epsilon)
          network         (assoc network :weights new-weights)]
      (gradient-descent-bprop network sample-matrix)))
    
  (train-ann-old [network samples]    
    (let [
          sample-matrix   (map #(list (matrix (first %))
                                      (matrix (second %)))
                               samples)]
      (gradient-descent-bprop network sample-matrix)))
    
  
  
  )


(defn train [samples default-options]
  (let [network (MultiLayerPerceptron. default-options)]
    (train-ann-new network samples)))



; (defn ajw2 [x] x)

(defn raw-output
  "Round outputs to nearest integer."
  [output]
  (mapv (fn [x]  x) output))

(def netw (atom nil))

   
(defn state-of-play []   
   (let [x00 (first (raw-output (run @netw [0 0])))
         x01 (first (raw-output (run @netw [0 1])))
         x02 (first (raw-output (run @netw [1 0])))
         x03 (first (raw-output (run @netw [1 1])))]
 ;  (println "2" x00 x01 x02 x03)  
   (println "total error: "  (+ (Math/pow (- 0.0 x00) 2) 
                                (Math/pow (- 1.0 x01) 2) 
                                (Math/pow (- 1.0 x02) 2) 
                                (Math/pow (- 0.0 x03) 2))) ) 
  ; (println "3")
   (println "[0 0] => " (raw-output (run @netw [0 0])) (run-binary @netw [0 0]) ": [0]")
   (println "[0 1] => " (raw-output (run @netw [0 1])) (run-binary @netw [0 1]) ": [1]" )
   (println "[1 0] => " (raw-output (run @netw [1 0])) (run-binary @netw [1 0]) ": [1]")
   (println "[1 1] => " (raw-output (run @netw [1 1])) (run-binary @netw [1 1]) ": [0]")
)

(defn wali-backprop [sample-data p]
  
 (let [iterations (p :iterations)
       iteration-count (atom 0)
       
       default-options   {:max-iters (p :max-iters)
                         :desired-error (p :desired-error)
                         :hidden-neurons (p :hidden-neurons)
                         :learning-rate (p :learning-rate)
                         :learning-momentum (p :learning-momentum)
                         :weight-epsilon (p :weight-epsolon)} ] 
      
    (if (p :new) (do 
                    (println "Iteration: " @iteration-count)
                    (reset! netw (train sample-data default-options))))
        
    (loop []  
      
           
              (let [weights  (:weights @netw)
                   [gradients mse] (calc-gradients-and-error weights sample-data)]
             
               (swap! iteration-count inc)  
               (println "Iterations: (* 100) " @iteration-count "mse: " mse)
               
               
               (if (or (>= @iteration-count (p :iterations)) 
                       (> (p :desired-error) mse)) 
                 
                 (do 
                    (println "Complete.") 
                    (state-of-play))
                 
                 (do (reset! netw (train-ann-old @netw sample-data))
                     (recur)))))
))


(defn core [] (ns neural.core))

