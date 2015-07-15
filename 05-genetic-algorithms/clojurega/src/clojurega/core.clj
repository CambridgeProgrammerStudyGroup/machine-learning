(ns clojurega.core
  (:require [clojure.pprint :refer [pprint]]
            [clojurega.utils :as utils])
  (:gen-class))



(def alphabet (map char (range 32 127)))

(defn rand-range [a b]
  (+ (rand-int (- b a)) a))

(defn gen-one []
  (apply str (repeatedly (rand-range 10 30) #(rand-nth alphabet))))

(defn gen-pop [] (repeatedly 100 #(gen-one)))

(def target-string "Welcome To Genetic Algorithms!")

(defn fitness [candidate]
  (utils/damerau–levenshtein candidate target-string))

(defn select [population]
  "Very naive selection - there are much better examples.
  Take a look at https://en.wikipedia.org/wiki/Crossover_(genetic_algorithm)"
  (let [fitness-ranked (sort-by fitness population)
        discard (int (* 0.5 (count fitness-ranked)))
        best (take discard fitness-ranked)]
    best))

(defn breed [a b]
  "Cut and splice crossover"
  (let [from-a (take (rand-int (count a)) a)
        from-b (drop (rand-int (count b)) b)]
    (str from-a from-b)))


(defn crossover [population]
  (let [parents population
        reducer (fn [children parent]
                  (conj children
                        (breed parent (rand-nth parents))
                        (breed parent (rand-nth parents))))]

    (reduce reducer population population)))


(defn mutate-swap [i where]
  (apply str
         (concat
            (take (dec where) i)
            [(nth i (inc where))]
            [(nth i where)]
            (drop (inc where) i))))

(defn mutate-delete [i where]
  (apply str (concat (take (dec where) i) (drop where i))))

(defn mutate-add [i where]
  (apply str (concat (take where i) [(rand-nth alphabet)] (drop where i))))

(defn mutate-change [i where]
  (apply str (concat (take (dec where) i) [(rand-nth alphabet)] (drop where i))))

(defn mutate [individual]
  "Very naive mutation"
  (let [spot (dec (rand-int (count individual)))
        f (rand-nth [mutate-swap mutate-add mutate-change mutate-delete])]
    (f individual spot)))



; (map #(%1 "abcdefghijkl" 3) [mutate-swap mutate-add mutate-change mutate-delete])


(defn mutate-pop [population]
  (let [rate 0.1]
    (map (fn [individual]
           (if (< (rand) rate)
             (mutate individual)
             individual))
         population)))


(defn run-generations [ngens initial-population mutate crossover select]
  (loop [population initial-population
         generation 0]
    (if (< generation ngens)
      (do
        (let [popul (sort-by fitness population)]
        (println "Generation:" generation "best fitness (less is best):" (fitness (first popul)) "[" (first popul) "]" )
        (recur
         (mutate (crossover (select population)))
         (inc generation))))
      population)))


(defn -main [& args]
  (pprint (run-generations 100 (gen-pop) mutate-pop crossover select)))
