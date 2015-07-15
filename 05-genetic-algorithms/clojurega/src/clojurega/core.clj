(ns clojurega.core
  (:require [clojure.pprint :refer [pprint]]
            [clojurega.utils :as utils])
  (:gen-class))



; The alphabet is what we can use to make candidates
; in this case it includes all ASCII printables.
(def alphabet (map char (range 32 127)))
(def alphabet-length (count alphabet))


; We wish to reach the target string
; this is the solution to the search problem for our GA
(def target-string "Welcome To Genetic Algorithms!")

(defn rand-range [a b]
  (+ (rand-int (- b a)) a))

; candidates are strings between 10 and 30 character long
; randomly created from teh alphabet
(defn gen-one []
  (apply str (repeatedly (rand-range 10 30) #(rand-nth alphabet))))

; A population is simply a collection of generated candidates
(defn gen-pop [n] (repeatedly n #(gen-one)))


; we could define the fitness of our candidate to be
; related to the string distance to our target, as below.
;(defn fitness [candidate]
;  (Math/pow Math/E (- (utils/damerau–levenshtein candidate target-string)))

; However, defining the fitness more pragmatically will
; allow our algorithm to run much faster
(defn fitness [candidate]
  (-
    (apply + (map (fn [a b] (if (= a b) 1 0)) candidate target-string))
    (* 2.0 (Math/abs (- (count candidate) (count target-string))))))


; From our pool of candidates, we wish to select the best half
; every iteration.
(defn select [population]
  (let [fitness-ranked (sort-by fitness population)
        discard (int (* 0.5 (count fitness-ranked)))
        best (drop discard fitness-ranked)]
    best))

; And we will breed the candidates together by mixing their strings randomly
(defn breed [a b]
    (apply str (map (fn [a b] (rand-nth [a b])) a b)))

; which needs to be carried out over an entire population.
(defn crossover [population]
  (let [pairs (map vector population (shuffle population))
        kids (map (fn [[a b]] (breed a b)) pairs)]
    kids))

; We'll also mutate the kids, because we need to bring in new features from somehere
; to do this we define a bunch of mutation functions
(defn mutate-delete [i where]
  (apply str (concat
              (take (dec where) i)
              (drop where i))))

(defn mutate-add [i where]
  (apply str (concat
              (doall (take where i))
              [(rand-nth alphabet)]
              (doall (drop where i)))))

(defn mutate-change [i where]
  (apply str (concat
              (doall (take (dec where) i))
              [(rand-nth alphabet)]
              (doall (drop where i)))))

(defn mutate-shift [i where]
  (apply str (concat
              (take (dec where) i)
              [(char ((rand-nth [inc dec]) (int (nth i where))))]
              (drop where i))))

(defn dont-mutate [i where] i)

; Which we can then use in our mutate function, randomly selecting a given mutation
(defn mutate [individual]
  (let [spot  (rand-int (dec (count individual)))
        f     (rand-nth [mutate-delete mutate-add mutate-change mutate-shift])
        new   (f individual spot)]
    new))

; now, we need to apply mutations to entire populations which we can do as below
; depedning on the mutation rate.
(defn population-mutator [rate]
  (fn [population]
    (map
     (fn [individual]
       (if (< (rand) rate)
         (mutate individual)
         individual))
     population)))

; we bring it all together with some debugging output to run a number of generations of our
; genetic algorithm.
(defn run-generations [ngens initial-population mutate-pop crossover select]
  (loop [population initial-population
         generation 0]
    (let [selected     (select population)
          children     (mutate-pop (crossover selected))
          newpop       (sort-by fitness (concat selected children))
          best         (last newpop)
          best-fitness (fitness best)]
      (println "Generation:" generation "best fitness:" best-fitness "[" best "]" )
      (if (< best-fitness (count target-string))
        (recur newpop (inc generation))
        population))))


(def config {
             :mutation-rate 1.0
             :population 500
             :generations 500
             })

; and trigger the search when the class is called as main.
(defn -main [& args]
 (run-generations
   (config :generations)
   (gen-pop (config :population))
   (population-mutator (config :mutation-rate))
   crossover
   select))
