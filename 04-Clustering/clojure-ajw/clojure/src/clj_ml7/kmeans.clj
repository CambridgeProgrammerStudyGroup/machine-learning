(ns clj-ml7.kmeans)

(defn distance [a b]
  (if (< a b) (- b a) (- a b)))

(defn closest [point means distance]
  (first (sort-by #(distance % point) means)))

(def data '(2 3 5 6 10 11 100 101 102))
(def guessed-means '(0 10))

(defn point-groups [means data distance]
  (group-by #(closest % means distance) data))

(defn average [& list]
  (/ (reduce + list)
     (count list)))

(defn new-means [average point-groups old-means]
  (for [m old-means]
    (if (contains? point-groups m)
      (apply average (get point-groups m))
      m)))

(defn iterate-means [data distance average]
  (fn [means]
    (new-means average
               (point-groups means data distance) means)))

;; ====
;; demo
;; ====

(defn demo []
  (println "data: " data)
  (println "means: " guessed-means)
    (map
   #(println "closest: " % "=>" (closest % guessed-means distance))
   [ 2 9 100])
  (println "groups: " (point-groups guessed-means data distance))
  (println "new means: " (new-means average (point-groups
                             guessed-means
                             data distance)
                             guessed-means))
  (println "iterate: ") 
  
  (let [map4  (take 4 (iterate (iterate-means data distance average) 
                                        guessed-means))]
    (map #(println (point-groups % data distance)) map4)))

(defn k-cluster [data distance means]
  (vals (point-groups means data distance)))

;; user> (k-cluster data distance '(37/6 101))
;; ([2 3 5 6 10 11] [100 101 102])

(defn take-while-unstable
  ([sq] (lazy-seq (if-let [sq (seq sq)]
                    (cons (first sq)
                          (take-while-unstable
                           (rest sq) (first sq))))))
  ([sq last] (lazy-seq (if-let [sq (seq sq)]
                         (if (= (first sq) last)
                           nil
                           (take-while-unstable sq))))))

;; user> (take-while-unstable
;;        '(1 2 3 4 5 6 7 7 7 7))
;; (1 2 3 4 5 6 7)
;; user> (take-while-unstable
;;        (iterate (iterate-means data distance average)
;;                 '(0 10)))
;; ((0 10) (10/3 55) (37/6 101))

;; user> (take-while-unstable
;;        (map #(k-cluster data distance %)
;;             (iterate (iterate-means data distance average) '(0 10))))
;; (([2 3 5] [6 10 11 100 101 102])
;;  ([2 3 5 6 10 11] [100 101 102]))

(defn k-groups [data distance average]
  (fn [guesses]
    (take-while-unstable
     (map #(k-cluster data distance %)
          (iterate (iterate-means data distance average)
                   guesses)))))

(def grouper
  (k-groups data distance average))

;; user> (grouper '(0 10))
;; (([2 3 5] [6 10 11 100 101 102])
;;  ([2 3 5 6 10 11] [100 101 102]))

;; user> (grouper '(1 2 3))
;; (([2] [3 5 6 10 11 100 101 102])
;;  ([2 3 5 6 10 11] [100 101 102])
;;  ([2 3] [5 6 10 11] [100 101 102])
;;  ([2 3 5] [6 10 11] [100 101 102])
;;  ([2 3 5 6] [10 11] [100 101 102]))


;; user> (grouper '(0 1 2 3 4))
;; (([2] [3] [5 6 10 11 100 101 102])
;;  ([2] [3 5 6 10 11] [100 101 102])
;;  ([2 3] [5 6 10 11] [100 101 102])
;;  ([2 3 5] [6 10 11] [100 101 102])
;;  ([2] [3 5 6] [10 11] [100 101 102])
;;  ([2 3] [5 6] [10 11] [100 101 102]))

;; user> (grouper (range 200))
;; (([2] [3] [100] [5] [101] [6] [102] [10] [11]))


(defn vec-distance [a b]
  (reduce + (map #(* % %) (map - a b))))

(defn vec-average [& list]
  (map #(/ % (count list)) (apply map + list)))

;; user> (vec-distance [1 2 3] [5 6 7])
;; 48
;; user> (vec-average  [1 2 3] [5 6 7])
;; (3 4 5)

(def vector-data
  '([1 2 3] [3 2 1] [100 200 300] [300 200 100] [50 50 50]))

;; user> ((k-groups vector-data vec-distance vec-average)
;;        '([1 1 1] [2 2 2] [3 3 3]))
;; (([[1 2 3] [3 2 1]] [[100 200 300] [300 200 100] [50 50 50]])

;;  ([[1 2 3] [3 2 1] [50 50 50]]
;;   [[100 200 300] [300 200 100]])

;;  ([[1 2 3] [3 2 1]]
;;   [[100 200 300] [300 200 100]]
;;   [[50 50 50]]))

;; Here our two identical guesses are both getting updated
;; user> (new-means average
;;                  (point-groups '(0 0) '(0 1 2 3 4) distance)
;;                  '(0 0))
;; (2 2)
;; Needs to be changed so that if there are two identical means only one of them will change.

(defn update-seq [sq f]
  (let [freqs (frequencies sq)]
    (apply concat
           (for [[k v] freqs]
             (if (= v 1)
               (list (f k))
               (cons (f k) (repeat (dec v) k)))))))

(defn new-means [average point-groups old-means]
  (update-seq
   old-means
   (fn [o]
     (if (contains? point-groups o)
       (apply average (get point-groups o)) o))))

;; Now only one will get updated at once
;; user> (new-means average
;;                  (point-groups '(0 0) '(0 1 2 3 4) distance)
;;                  '(0 0))
;; (2 0)

;; user> ((k-groups '(0 1 2 3 4) distance average)
;;        '(0 1))
;; (([0] [1 2 3 4]) ([0 1] [2 3 4]))
;; user> ((k-groups '(0 1 2 3 4) distance average)
;;        '(0 0))
;; (([0 1 2 3 4]) ([0] [1 2 3 4]) ([0 1] [2 3 4]))

;; user> ((k-groups vector-data vec-distance vec-average)
;;        '([1 1 1] [1 1 1] [1 1 1]))
;; (([[1 2 3] [3 2 1] [100 200 300] [300 200 100] [50 50 50]])
;;  ([[1 2 3] [3 2 1]] [[100 200 300] [300 200 100] [50 50 50]])
;;  ([[1 2 3] [3 2 1] [50 50 50]] [[100 200 300] [300 200 100]])
;;  ([[1 2 3] [3 2 1]] [[100 200 300] [300 200 100]] [[50 50 50]]))
