(ns neural.fatvat-perceptron)

; If we run these in the REPL we can see that the results are perfect 
;  for the linearly separable data.

;  > (apply train ls-test-data)
;  (0.04982859491606148 -0.0011851610388172009 -4.431771581539448E-4)

;  > (run-network  [0 1 0] (apply train ls-test-data))
;  0

;  > (run-network  [1 0 0] (apply train ls-test-data))
;  1


;However, for the non-linearly separable they are completely wrong:


;  > (apply train xor-test-data)
;  (-0.02626745010362212 -0.028550312499346104)

;  > (run-network [1 1] (apply train xor-test-data))
;  0

;  > (run-network [0 1] (apply train xor-test-data))
;  0

;  > (run-network [1 0] (apply train xor-test-data))
;  0

;  > (run-network [0 0] (apply train xor-test-data))
;  0

; ---------------

; The SLP is nothing more than a collection of weights and an output value. 
; The Clojure code below allows you to create a network (initially with zero weights) 
; and get a result from the network given some weights and an input. Not very interesting.


(defn create-network 
  [out]
  (repeat out 0))

(defn run-network
  [input weights]
  (if (pos? (reduce + (map * input weights))) 1 0))

     
; The clever bit is adapting the weights so that the neural network learns. 
; This process is known as training and is based on a set of data with known expectations. 
; The learning algorithm for SLPs is shown below. 
; Given an error (either 1 or -1 in this case), adjust the weights based on the size of the inputs. 
; The learning-rate decides how much to vary the weights; 
; too high and the algorithm won't converge, too low and it'll take forever to converge.



(def learning-rate 0.05)

(defn- update-weights
  [weights inputs error]
  (map 
   (fn [weight input] (+ weight (* learning-rate error input)))
   weights inputs))



; Finally, we can put this all together with a simple training function. 
; Given a series of samples and the expected values, 
; repeatedly update the weights until the training set is empty.

(defn train
  ([samples expecteds] (train samples expecteds (create-network (count (first samples)))))
  ([samples expecteds weights]
     (if (empty? samples)
       weights
       (let [sample (first samples)
             expected (first expecteds)
             actual (run-network sample weights)
             error (- expected actual)]
         (recur (rest samples) (rest expecteds) (update-weights weights sample error))))))

; So we have our network now. How can we use it? 
; Firstly, let's define a couple of data sets both linearly separable and not. 
; Jiggle adds some random noise to each sample. 
; Note the cool # syntax for a short function definition (I hadn't seen it before).

(defn jiggle [data]
  (map (fn [x] (+ x (- (rand 0.05) 0.025))) data))

(defn make-test-data [training-set]
   (let [x1 (mapv (fn [x] (first x)) training-set)
         x2 (mapv (fn [x] (second x)) training-set)]
      [(apply concat (map (fn [x] (take 2 (repeatedly #(jiggle x)))) x1))
       (flatten (map (fn [x] (repeat 2 (first x))) x2))]
 ))


(defn ls-test-data []

      [(concat
        (take 2 (repeatedly #(jiggle [0 1 0])))
        (take 2 (repeatedly #(jiggle [1 0 0]))))
       (concat
        (repeat 2 0)
        (repeat 2 1))])

(def xor-test-data
     [(concat
       (take 100 (repeatedly #(jiggle [0 1])))
       (take 100 (repeatedly #(jiggle [1 0])))
       (take 100 (repeatedly #(jiggle [0 0])))
       (take 100 (repeatedly #(jiggle [1 1]))))
      (concat
       (repeat 100 1)
       (repeat 100 1)
       (repeat 100 0)
       (repeat 100 0))])


(defn fatvat-perceptron [p]
   (let [training-set (p :training-set)
         test-data (make-test-data training-set)
         network (apply train test-data)]
     (println "fp")
     
     (println (mapv (fn [x] [(first x) (second x)]) training-set))
     (doseq [ts (mapv (fn [x] [(first x) (second x)]) training-set)] 
         (println (first ts) " => " (second ts) " : " (run-network (first ts) network)))))
      
(defn core [] (ns neural.core))
  


