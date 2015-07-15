(ns clojurega.core
  (:require [clojure.pprint :refer [pprint]]
            [clojurega.utils :as utils])
  (:gen-class))



(def alphabet (map char (range 32 127)))

(defn rand-range [a b]
  (+ (rand-int (- b a)) a))

(defn gen-one []
  (apply str (repeatedly (rand-range 10 30) #(rand-nth alphabet))))

(gen-one)

(defn gen-pop [n] (repeatedly n #(gen-one)))

(def target-string "Welcome To Genetic Algorithms!")

;(defn fitness [candidate]
;  (utils/damerau–levenshtein candidate target-string))

(defn fitness [candidate]
  (-
    (apply + (map (fn [a b] (if (= a b) 1 0)) candidate target-string))
    (Math/abs (- (count candidate) (count target-string)))))


(defn select [population]
  (let [fitness-ranked (sort-by fitness population)
        discard (int (* 0.5 (count fitness-ranked)))
        best (drop discard fitness-ranked)]
    best))

(defn breed [a b]
    (apply str (map (fn [a b] (rand-nth [a b])) a b)))


(defn crossover [population]
  (let [pairs (map vector population (shuffle population))
        reducer (fn [children pair]
                  (conj children
                        (breed (first pair) (second pair))))]

    (reduce reducer [] pairs )))


(defn clamp [i a b]
  (cond (< b i) b
        (> a i) a
        :else i))

(defn mutate-delete [i where]
  (apply str (concat (take (dec where) i) (drop where i))))

(defn mutate-add [i where]
  (apply str (concat (take where i) [(rand-nth alphabet)] (drop where i))))

(defn mutate-change [i where]
  (apply str (concat (list (take (dec where) i)) [(rand-nth alphabet)] (list (drop where i)))))

(defn mutate-shift [i where]
  (apply str (concat
              (take (dec where) i)
              [(char ((rand-nth [inc dec]) (int (nth i where))))]
              (drop where i))))

(defn dont-mutate [i where] i)

(defn mutate [individual]
  (let [spot  (rand-int (dec (count individual)))
        f     (rand-nth [mutate-delete mutate-add mutate-change mutate-shift])
        new   (f individual spot)]
    new))



(map #(%1 (gen-one) 3) [ mutate-add mutate-change mutate-delete])


(defn mutate-population [population]
  (let [rate 1.0
        newpop (map (fn [individual]
                       (if (< (rand) rate)
                         (mutate individual)
                         individual))
                     population)]
    newpop))


(defn run-generations [ngens initial-population mutate-pop crossover select]
  (loop [population initial-population
         generation 0]
    (if (< generation ngens)
      (do
        (let [sorted-popul (sort-by fitness population)
              best (last sorted-popul)
              best-fitness (fitness best)]
        (println "Generation:" generation "best fitness:" best-fitness "[" best "]" )

        (recur
         (let [selected (select population)
               children (mutate-pop (crossover selected))]
               (concat selected children))
         (inc generation))))
      population)))


(defn -main [& args]
  (let [starting (gen-pop 1000)
        population (run-generations 500 starting mutate-population crossover select)]))
