(ns clj-ml7.utils
  
  (:use [clojure.math.numeric-tower :only [sqrt]])
 
  )


;; -------------------------------
;; Used in hierarchical and dbscan
;; -------------------------------
;; hierarchical: closest-vectors
;;               centroid
;; dbscan:       distance

(defn sum-of-squares [coll]
  (reduce + (map * coll coll)))

(defprotocol Each
  (each [v op w]))

(defprotocol Distance
  (distance [v w]))

(defn closest-vectors [vs]
  (let [index-range (range (count vs))]
    (apply min-key
           (fn [[x y]] (distance (vs x) (vs y)))
           (for [i index-range
                 j (filter #(not= i %) index-range)]
             [i j]))))

(defn centroid [& xs]
  (each
   (reduce #(each %1 + %2) xs)
   *
   (double (/ 1 (count xs)))))

(extend-type clojure.lang.PersistentVector
  Each
  (each [v op w]
    (vec
     (cond
      (number? w) (map op v (repeat w))
      (vector? w) (if (>= (count v) (count w))
                    (map op v (lazy-cat w (repeat 0)))
                    (map op (lazy-cat v (repeat 0)) w)))))
  Distance
  ;; implemented as Euclidean distance
  (distance [v w] (-> (each v - w)
                      sum-of-squares
                      sqrt)))



;; --------------------------
;; Taken from ns dbscan
;; --------------------------

;(defn sum-of-squares [coll]
;  (reduce + (map * coll coll)))

;(defprotocol Each
;  (each [v op w]))

;(defprotocol Distance
;  (distance [v w]))

;(defn closest-vectors [vs]
;  (let [index-range (range (count vs))]
;    (apply min-key
;           (fn [[x y]] (distance (vs x) (vs y)))
;           (for [i index-range
;                 j (filter #(not= i %) index-range)]
;             [i j]))))

;(defn centroid [& xs]
;  (each
;   (reduce #(each %1 + %2) xs)
;   *
;   (double (/ 1 (count xs)))))

;(extend-type clojure.lang.PersistentVector
;  Each
;  (each [v op w]
;    (vec
;     (cond
;      (number? w) (map op v (repeat w))
;      (vector? w) (if (>= (count v) (count w))
;                    (map op v (lazy-cat w (repeat 0)))
;                    (map op (lazy-cat v (repeat 0)) w)))))
;  Distance
  ;; implemented as Euclidean distance
;  (distance [v w] (-> (each v - w)
;                      sum-of-squares
;                      sqrt)))


;(defn average [lst] (/ (reduce + lst) (count lst)))


;(defn avg [xs]
;	(loop [xs xs count 0 sum 0]
;		(if-not (seq xs)
;			(/ sum count)
;			(recur (rest xs) (inc count) (+ sum (first xs))))))
