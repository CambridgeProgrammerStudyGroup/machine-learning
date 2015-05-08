(ns neural.fatvat-backprop)

  ;;; Example run through the REPL
;  uk.co.fatvat.mlperceptron> (example)
;  (0.10717792758953508) --> 0
;  (0.993502708495955) --> 1
;  (0.9930515903590437) --> 1
;  (0.00883530181467182) --> 0
;  nil

; So how do we model this network in a functional programming language like Clojure? 

; We start by defining some constants (including the activation-function and its derivation). 
; bp-nn represents the network itself.


(def activation-function (fn [x] (Math/tanh x)))
(def activation-function-derivation (fn [y] (- 1.0 (* y y))))

(def num-hidden (atom 6))
(def learning-rate (atom 0.5))
(def momentum (atom 0.1))

(defstruct bp-nn :weight-input :weight-output :change-input :change-output)

; ----------------
; Create a network
; ----------------

; Next we create some simple initialization functions to create an initial neural network, 
;  together with some helper functions for iterating over matrices 
;  (which we'll model as lists of lists). 

; Usually you'd use random weights to initialize things, 
;  but allowing fixed values makes testing possible.

(defn make-matrix
  [width height]
  "Create a matrix (list of lists)"
  (repeat width (repeat height 0)))

(defn matrix-map
  [m func]
  "Apply a function to every element in a matrix"
  (map (fn [x] (map func x)) m))

(defn rand-range 
  [l h]
  "Return a real number within the given range"
  (+ (rand (- h l)) l))

(defn create-network
  ([input hidden output]
     (create-network input hidden output true))
  ([input hidden output use-random-weights]
  "Create a network with the given number of input, hidden and output nodes"
  (let [i (inc input)
        w-func (if use-random-weights (fn [_] (rand-range -0.2 0.2)) (fn [_] 0.2))
        o-func (if use-random-weights (fn [_] (rand-range -2.0 2.0)) (fn [_] 2.0))]
    (struct bp-nn
            (matrix-map (make-matrix i hidden) w-func)
            (matrix-map (make-matrix hidden output) o-func)
            (make-matrix i hidden)
            (make-matrix hidden output)))))

; --------------------------------
; Feed forward through the network
; --------------------------------

; The first thing we should do is run a pattern through the network and see 
;   what comes out the other end. We're not just interested in the output result, 
;   we want to know what happened at the hidden layer, so we return a vector of 
;   ao (activation output) and ah (activation hidden).

; comp is functional composition. ((comp x y) 5) is the equivalent of (x (y 5)) 
;   so in the example below we add the numbers together and then apply the 
;   activation function. The nested map calls allow us to iterate over the 
;   elements in a matrix.

(defn apply-activation-function
  [w i]
  "Calculate the hidden activations"
  (apply map (comp activation-function +) (map (fn [col p] (map (partial * p) col)) w i)))

(defn run-network
  [pattern network]
  
  "Run the network with the given pattern and return the output and the hidden values"
  (assert (= (count pattern) (dec (count (network :weight-input)))))
  (let [p (cons 1 pattern)] ;; ensure bias term added
    (let [wi (network :weight-input)
          wo (network :weight-output)
          ah (apply-activation-function wi p)
          ao (apply-activation-function wo ah)]
      [ao ah])))


; In order to perform backwards-propagation we need a couple of helper functions 
;   that work on matrices and vectors to calculate changes in the output that 
;   will be used to update the weights.

;  These helper functions are pretty sick - (no-one wants to read 
;    (map (partial reduce +) ...). A better design would probably be to 
;    introduce a proper matrix abstraction. 
;  There's the beginnings of one here but this is a bit too "Java" syntax heavy 
;    for more liking.


(defn calculate-hidden-deltas
  [wo ah od]
  "Calculate the error terms for the hidden"
  (let [errors (map (partial reduce +) (map (fn [x] (map * x od)) wo))] ;; Sick.
    (map (fn [h e] (* e (activation-function-derivation h))) ah errors)))
    
(defn update-weights
  [w deltas co ah]
  (let [x (map 
           (fn [wcol ccol h] 
             (map (fn [wrow crow od] 
                    (let [change (* od h)]
                      [(+ wrow (* @learning-rate change) (* @momentum crow)) change]))
                  wcol ccol deltas))
           w co ah)]
    [(matrix-map x first) (matrix-map x second)]))

; I did warn you...

; ---------
; Algorithm
; ---------

; The next thing we need to implement is the back-propagation algorithm itself. 
; This takes in more parameters than it needs to so that it can be tested 
;   standalone (it could be implemented as a local function using a closure to 
;  capture some of them). 

; It returns an updated version of the neural network.


(defn back-propagate
  [target p results network]
  "Back propagate the results to adjust the rates"
  (assert (= (count target) (count (first (network :weight-output)))))
  (let [pattern (cons 1 p) ;; ensure bias term added
        ao (first results)
        ah (second results)
        error (map - target ao)
        wi (network :weight-input)
        wo (network :weight-output)
        ci (network :change-input)
        co (network :change-output)
        output-deltas (map (fn [o e] (* e (activation-function-derivation o))) ao error)
        hidden-deltas (calculate-hidden-deltas wo ah output-deltas)
        updated-output-weights (update-weights wo output-deltas co ah)
        updated-input-weights (update-weights wi hidden-deltas ci pattern)]

    (struct bp-nn
            (first  updated-input-weights)
            (first  updated-output-weights)
            (second updated-input-weights)
            (second updated-output-weights))
    
  ))

; All that remains is to train the network. 

; We need a set of samples with know results, together with a number of iterations to try. 
; I've split these into run-patterns which runs through the patterns once, 
;   and train-network which creates the initial network and runs it through 
;   the patterns the specified number of times.



(defn run-patterns
  [network samples expecteds]
  (if (empty? samples)
    network
    (let [expected (first expecteds)
          sample (first samples)
          [ah ao] (run-network sample network)
          updated-network (back-propagate expected sample [ah ao] network)]
      (recur updated-network (rest samples) (rest expecteds)))))




(defn train-network
  ([samples expected iterations]
     (train-network (create-network (count (first samples)) 
                                    @num-hidden 
                                    (count (first expected))) 
                    samples expected iterations))
  
  ([network samples expected iterations]
     (if (zero? iterations)
       network
       (recur (run-patterns network samples expected) samples expected (dec iterations)))))


; So how well does it work in practise? Pretty damn good. 
; It correctly converges after a few iterations (this time 100) and 
;   consistently gets the XOR test data set correct.

(defn jiggle [data]
  (map (fn [x] (+ x (- (rand 0.05) 0.025))) data))

(def xor-test-data
     [(concat
       (take 100 (repeatedly #(jiggle [0 0])))
       (take 100 (repeatedly #(jiggle [0 1])))
       (take 100 (repeatedly #(jiggle [1 0])))
       (take 100 (repeatedly #(jiggle [1 1]))))
      (concat
       (repeat 100 [0])
       (repeat 100 [1])
       (repeat 100 [1])
       (repeat 100 [0]))])
  
(defn jiggle [data]
  (map (fn [x] (+ x (- (rand 0.05) 0.025))) data))

(defn make-test-data [training-set]
   (let [x1 (mapv (fn [x] (first x)) training-set)
         x2 (mapv (fn [x] (second x)) training-set)]
      [(apply concat (map (fn [x] (take 100 (repeatedly #(jiggle x)))) x1))
       (apply concat (map (fn [x] (repeat 100 x)) x2))]
 ))  
  
  
(defn fatvat-backprop [p]
  
   (reset! momentum (p :momentum))
   (reset! num-hidden (p :hidden))
   (reset! learning-rate (p :learning-rate))
  
   (let [training-set (p :training-set)
         iterations (p :iterations)         
         test-data (make-test-data training-set)
         network (apply train-network (conj test-data iterations))]
     
     ; print results     

     (doseq [ts (mapv (fn [x] [(first x) (second x)]) training-set)] 
         (println (first ts) " --> " (first (run-network (first ts) network))  " : " (second ts)))))


; ---------
; shortcuts
; ---------

(defn core [] (ns neural.core))