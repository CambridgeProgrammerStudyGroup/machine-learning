(ns clj-ml7.hierarchical
  
  (:require [incanter.core :as ic] 
        [incanter.datasets :as id]
          [incanter.stats :as st] ; pca
          [incanter.charts :as ch]
                [clj-ml.data :as cd]
                [clojure.math.numeric-tower :as math]
                [clojure.java.io :as io]
                [clojure.data.csv :as csv]
                
           [clj-ml7.utils :as ut]     
                )
  
  
  )

; ===
; AJW
; ===

(defn h-cluster
  "Performs hierarchical clustering on a
  sequence of maps of the form { :vec [1 2 3] } ."
  [nodes]
  (loop [nodes nodes]
    (if (< (count nodes) 2)
      nodes
      (let [vectors    (vec (map :vec nodes))
            [l r]      (ut/closest-vectors vectors)
            node-range (range (count nodes))
            new-nodes  (vec
                        (for [i node-range
                              :when (and (not= i l)
                                         (not= i r))]
                          (nodes i)))]
        (recur (conj new-nodes
                     {:left (nodes l) :right (nodes r)
                      :vec (ut/centroid
                            (:vec (nodes l))
                            (:vec (nodes r)))}))))))

;; include more from tests
;; user> (h-cluster [{:vec [1 2 3]} {:vec [3 4 5]} {:vec [7 9 9]}])
;; [{:left {:left {:vec [3 4 5]},
;;          :right {:vec [1 2 3]},
;;          :vec [2.0 3.0 4.0]},
;;   :right {:vec [7 9 9]},
;;   :vec [4.5 6.0 6.5]}]

;; =============
;; iris data set
;; =============

(def features [:Sepal.Length
               :Sepal.Width
               :Petal.Length
               :Petal.Width])

;(def iris-data (ic/to-vect (ic/sel (id/get-dataset :iris)
;                                :cols features)))

;(def iris-dataset
;  (cd/make-dataset "iris" features iris-data))

;(defn ltm [l] (map (fn [x] {:vec x}) l)) 

;(def iris-data-ajw (vec (ltm (ic/to-vect (ic/sel (id/get-dataset :iris)
;                                :cols features)))))

;(def iris (first (h-cluster iris-data-ajw)))


;; ===
;; AJW
;; ===

(declare iris-data-ajw iris iris-map iris-n iris-n-join
         cluster-join iris-join)

;; -----------------------
;; Get iris-data from file
;; -----------------------

(defn line-read []
  (with-open [rdr (io/reader "resources/iris.data")]
    (doseq [line (line-seq rdr)]
      (println line))))

(defn get-iris-data []
    (with-open [in-file (io/reader "resources/iris.data")]
    (let [lis (doall (csv/read-csv in-file))
          k (map (fn [x] {:vec (vec (map read-string (take 4 x)))}) lis)]
        (vec k))))

(defn get-iris-map []
  (with-open [in-file (io/reader "resources/iris.data")]
    (let [lis (doall (csv/read-csv in-file))
          k (map (fn [x] (vec (map read-string (take 4 x)))) lis) 
          v (map (fn [x] (nth x 4)) lis)]
        (zipmap k v))))





;; ---------
;; Utilities
;; ---------

(defn v [x] (:vec x))
(defn l [x] (:left x))
(defn r [x] (:right x))

(defn leaves [m] (if (nil? (l m)) (list (v m)) (concat (leaves (l m)) (leaves (r m)))))

;; ============
;; depth search
;; ============
;; (clusters iris)
;; (find-cluster iris [7.35 2.8499999999999996 6.199999999999999 1.85])



(defn clusters-depth [m route] (if (not (nil? (l m))) 
                           (do
                            ; (println (count (leaves m))) 
                            (concat 
                               (list (list (v m) (count (leaves m)) route)) 
                               (clusters-depth (l m) (vec (concat route [:L]))) 
                               (clusters-depth (r m) (vec (concat route [:R]))))) 
                            nil))

(defn clusters [d] (clusters-depth d))

(defn find-cluster [m c] 
    (cond (nil? m) nil
          (= (v m) c) (list (v m) (count (leaves m)) (leaves m))
          :else  (or   
                   (find-cluster (l m) c) 
                   (find-cluster (r m) c))))


;; =====
;; ?????
;; =====

(defn clusters-width [m route] (println "ajw")
          (let [op2  (fn [op k] (if (not (nil? (l m))) 
                            (concat (list (list (v m) (count (leaves m)) route))    
                                  (concat (clusters-width (op m) (cons k route)) (clusters-width (op m) (cons k route))) 
                            nil)))]
           (println "---")
           (println (first route))
           
           (if (or (empty? route) (= (first route) :R))
                 (op2 l :L) 
                 (op2 r :R))))
               
(defn gen-list [x]
   
   (if (empty? x) [[:L] [:R]]
       (let [xlast (count (last x))
             seed (reverse (take (math/expt 2 xlast) (reverse x)))
             new-routes (map vec (mapcat (fn [x] (list (concat x [:L]) (concat x [:R]))) seed))]
                 (concat x new-routes))))   
         
;; ==============
;; breadth search
;; ==============
;; Make a lazy list of breadth first routes through the network


;; --------
;; examples
;; ---------

(defn positive-numbers 
	([] (positive-numbers 1))
	([n] (cons n (lazy-seq (positive-numbers (inc n))))))


(defn times-two [number]
 (print "- ")
 (* 2 number))

(def powers-of-two (lazy-cat [1 2] (map times-two (rest powers-of-two))))

;(println (take 10 powers-of-two))
;(println (take 12 powers-of-two))

;; -------
;; [:L :R]
;; -------
;; (nth powers-of-lr 1000001)

(defn next-lr [l] 
  
  (cond 
     (empty? l) [:L]
     (= (last l) :L) (vec (concat (vec (reverse (rest (reverse l)))) [:R])) 
     :else (vec (concat (next-lr (vec (rest (reverse l)))) [:L]))))  

(def powers-of-lr (lazy-seq (iterate next-lr nil)))  

;; ===============
;; make n clusters
;; ===============
;; (def iris-3 (h-cluster-n iris-data-ajw 3))
      
(defn h-cluster-n
  "Performs hierarchical clustering on a
  sequence of maps of the form { :vec [1 2 3] } ."
  [nodes n]
  (loop [nodes nodes]
    (println (count nodes))
    (if (= (count nodes) n)
          nodes
      (let [vectors    (vec (map :vec nodes))
            [l r]      (ut/closest-vectors vectors)
            node-range (range (count nodes))
            new-nodes  (vec
                        (for [i node-range
                              :when (and (not= i l)
                                         (not= i r))]
                          (nodes i)))]
        (recur (conj new-nodes
                     {:left (nodes l) :right (nodes r)
                      :vec (ut/centroid
                            (:vec (nodes l))
                            (:vec (nodes r)))}))))))

;; -----------------------------------------
;; Make clusters and keep record of sequence
;; -----------------------------------------
;; (def iris-join (first (h-cluster-join iris-data-ajw)))


(defn h-cluster-join
  "Performs hierarchical clustering on a
  sequence of maps of the form { :vec [1 2 3] } ."
  [nodes]
  (loop [nodes nodes
         joins (count nodes)]
    (println (count nodes))
    (if (< (count nodes) 2)
          nodes
      (let [vectors    (vec (map :vec nodes))
            [l r]      (ut/closest-vectors vectors)
            node-range (range (count nodes))
            new-nodes  (vec
                        (for [i node-range
                              :when (and (not= i l)
                                         (not= i r))]
                          (nodes i)))]
        (recur (conj new-nodes
                     {:left (nodes l) :right (nodes r)
                      :vec (ut/centroid
                            (:vec (nodes l))
                            (:vec (nodes r))) :joins joins}) (dec joins))))))

                            
(defn find-join [m c] 
    (cond (nil? m) nil
          (= (:joins m) c) m  ;(list (:joins m) (count (leaves m)) (leaves m))
          :else  (or   
                   (find-join (l m) c) 
                   (find-join (r m) c))))



(defn clusters-join 
  ([joins n] (clusters-join joins 2 n))
  ([joins s n]
    (if (= n 1) joins
      (clusters-join (cluster-join joins s) (inc s) (dec n)))))   
         
(defn cluster-join [joins n]   
   (loop [lis joins]
    (let [fj (find-join (first lis) n)]
      (if fj (concat [(l fj)] [(r fj)] (vec (remove (fn [x] (= x fj)) joins)))   
          (recur (rest lis))))))


;; -------
;; results
;; -------

; Make list of types in a particular cluster

(defn iris-types [i n]
  
  (let [vecs (leaves (nth i n))]
    (map (fn [x] (iris-map x)) vecs)))

; Make map of counts of types in a particular cluster 

(defn count-list
  ([lis] (count-list lis {}))
  ([lis mp]
   (if (empty? lis) mp
   (let [head (first lis)
         lis2 (vec (filter (fn [x] (not (= x head))) lis))] 
      (count-list lis2 (assoc mp head (- (count lis) (count lis2))))))))    

; Print out summary results for a cluster

(defn iris-results [i]
   (doseq [x (vec (range (count i)))]
     (println "Cluster " x (count-list (iris-types i x)))))

;; ----------------------------------------
;; Hierarchical clustering of iris data set
;; ----------------------------------------


; Generates clusters each time

(defn iris-cluster [n]
  
  (if (not (bound? #'iris-data-ajw))
     (def iris-data-ajw (get-iris-data)))
  
  (if (not (bound? #'iris))
    (def iris (first (h-cluster iris-data-ajw))))

  (if (not (bound? #'iris-map))
    (def iris-map (get-iris-map)))
  
  (if (or (not (bound? #'iris-n))
          (not (= (count iris-n) n)))
    (def iris-n (h-cluster-n iris-data-ajw n)))
  
  (iris-results iris-n))
  
; Generates supplements hierarchy once and recovers information
; (iris-cluster-join 3) 
; 1. makes iris-n-join for 3 clusters 
; 2. prints out results of clustering

(defn iris-cluster-join [n]
  
  (if (not (bound? #'iris-data-ajw))
     (def iris-data-ajw (get-iris-data)))
  
  (if (not (bound? #'iris))
    (def iris (first (h-cluster iris-data-ajw))))

  (if (not (bound? #'iris-map))
    (def iris-map (get-iris-map)))
  
  (if (not (bound? #'iris-join))
    (def iris-join (h-cluster-join iris-data-ajw)))

  (if (or (not (bound? #'iris-n-join))
          (not (= (count iris-n-join) n)))
    (def iris-n-join (clusters-join iris-join n)))

  (iris-results iris-n-join))  


(defn make-data-set [i]
  
   (let [cnt (count i)
         fn1 (fn [n] (let [vecs (leaves (nth i n))]
                      (map (fn [x] (vec (concat x [n]))) vecs)))]
     (loop [n 0 lis []]
       (if (= n cnt) lis
          (recur (inc n) (vec (concat (fn1 n) lis)))))))  
     
     
     ;(vec (concat (fn1 0) (fn1 1) (fn1 2)))))

(defn iris-pca [ds]
     
    (let [iris-matrix (ic/to-matrix (ic/to-dataset ds))
          iris-features (ic/sel iris-matrix :cols (range 4))
          iris-species (ic/sel iris-matrix :cols 4)
          pca (st/principal-components iris-features)
          U (:rotation pca)
          U-reduced (ic/sel U :cols (range 2))
          reduced-features (ic/mmult iris-features U-reduced)]

     (ic/view (ch/scatter-plot (ic/sel reduced-features :cols 0)
                         (ic/sel reduced-features :cols 1)
                         :group-by iris-species
                         :x-label "PC1"
                         :y-label "PC2"))))

(defn hi-pca [] (iris-pca (make-data-set iris-n-join)))

(defn fisher-pca [] (iris-pca (id/get-dataset :iris)))
         
;; =========
;; namespace
;; =========

(defn core [] (ns clj-ml7.core))
