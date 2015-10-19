; Perceptron example
;
; {:weights [1 2 3]
;  :bias 0.0}


(defn build-perceptron [n]
  {:weights (take n (repeatedly #(- 0.5 (rand))))
   :bias (- 0.5 (rand))})

(defn threshold [algo]
  (case algo
        :edge #(if (> % 0) 1 -1)
        :tanh #(Math/tanh %)))

(defn feed-forward [perceptron inputs]
  (let [bias (perceptron :bias)
        weights (perceptron :weights)]
  ((threshold :edge)
   (reduce + bias (map * weights inputs)))))

(defn update-weights [perceptron inputs expected rate]
  (let [guess (feed-forward perceptron inputs)
        error (- expected guess)
        bias (perceptron :bias)
        weights (perceptron :weights)]
    {:weights (map #(+ %1 (* rate error %2)) weights inputs)
     :bias (+ bias (* error rate))}))

(defn train-perceptron [perceptron training-cases iterations rate]
  (let [ticks (take iterations (repeatedly #(rand-nth training-cases)))]
    (reduce
     (fn [perceptron [inputs expected]] (update-weights perceptron inputs expected rate))
     perceptron ticks)))


(defn build-train-perceptron [data iterations rate]
  (let [perceptron (build-perceptron (count (first (first data))))]
    (println "Before: " perceptron)
    (let [p (train-perceptron perceptron data iterations rate)]
      (println "After: " p)
      p
    )))

(defn test-perceptron [perceptron data]
  (let [data-count (count data)
        expected? (fn [[inputs expected]] (= (feed-forward perceptron inputs) expected))]
    (/
     (count (filter identity (map expected? data)))
     data-count)))

(def NOT
  [[[1] -1]
   [[-1] 1]])

(def AND
  [[[-1 -1] -1]
   [[ 1 -1] -1]
   [[-1  1] -1]
   [[ 1  1]  1]])

(def OR
  [[[-1 -1] -1]
   [[ 1 -1]  1]
   [[-1  1]  1]
   [[ 1  1]  1]])

(def XOR
  [[[-1 -1] -1]
   [[ 1 -1]  1]
   [[-1  1]  1]
   [[ 1  1] -1]])



(def N 100)
(def training-rate 0.01)



(def network
 {:input [(build-perceptron 1) (build-perceptron 1)]
  :hidden [(build-perceptron 2) (build-perceptron 2)]
  :output [(build-perceptron 2)]})

(defn feed-forward-network [network iv]
  (let [hidden-input (map #(feed-forward %1 %2) (map vector (network :input) iv))
        hidden-output (map #(feed-forward %1 hidden-input) (network :hidden))
        output (map #(feed-forward %1 hidden-output) (network :output))]
    output))

(defn output-errors [expected predicted]
  (map (fn [[ev ov]] (- ev ov)) (map vector expected predicted)))


(defn total-error [expected predicted]
  (* 0.5 (reduce + (map #(* %1 %1) (output-errors expected predicted)))))

(defn derivative-fn [y]
  (- 1.0 (* y y)))

(defn hidden-errors [output-errors neurons]
  ())

(output-errors [1 1] [0 1])
(def expected [1 1])
(def predicted [0 1])
(map vector expected predicted)
(map (fn [[ev ov]] (* ev ov)) (map vector expected predicted))


(test-perceptron (build-train-perceptron NOT N training-rate) NOT)
(test-perceptron (build-train-perceptron AND N training-rate) AND)
(test-perceptron (build-train-perceptron OR N training-rate) OR)
(test-perceptron (build-train-perceptron XOR N training-rate) XOR)
