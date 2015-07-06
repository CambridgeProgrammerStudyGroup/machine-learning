(ns clj-ml7.core
  
  (:require [clj-ml7.kmeans :as km]
            [clj-ml7.hierarchical :as hc]
            [clj-ml7.som :as som]
            [clj-ml7.pca :as pca]
            [clj-ml7.incanter :as inc]
            
            
            )
  
  (:require [incanter.core :as ic] 
        [incanter.datasets :as id]
        [clj-ml.data :as cd]
        [clj-ml.clusterers :as cc])
  
  )


;; ===
;; AJW
;; ===

(defn iris-hi-cluster [n]
  (hc/iris-cluster-join n))

;; ==========
;; AKHIL WALI
;; ==========

;; =================
;; kmeans clustering
;; =================


(def features [:Sepal.Length
               :Sepal.Width
               :Petal.Length
               :Petal.Width])

(def iris-data (ic/to-vect (ic/sel (id/get-dataset :iris)
                                :cols features)))

(def iris-dataset
  (cd/make-dataset "iris" features iris-data))

;; user> iris-dataset
;; #<ClojureInstances @relation iris

;; @attribute Sepal.Length numeric
;; @attribute Sepal.Width numeric
;; @attribute Petal.Length numeric
;; @attribute Petal.Width numeric

;; @data
;; 5.1,3.5,1.4,0.2
;; 4.9,3,1.4,0.2
;; 4.7,3.2,1.3,0.2
;; ...
;; 4.7,3.2,1.3,0.2
;; 6.2,3.4,5.4,2.3
;; 5.9,3,5.1,1.8>

(def k-means-clusterer
  (cc/make-clusterer :k-means
                  {:number-clusters 3}))

(defn train-clusterer [clusterer dataset]
  (cc/clusterer-build clusterer dataset)
  clusterer)

(defn kmeans-demo-1 []
   (train-clusterer k-means-clusterer iris-dataset))

;; #<SimpleKMeans
;; kMeans
;; ======

;; Number of iterations: 6
;; Within cluster sum of squared errors: 6.982216473785234
;; Missing values globally replaced with mean/mode

;; Cluster centroids:
;;                             Cluster#
;; Attribute       Full Data          0          1          2
;;                     (150)       (61)       (50)       (39)
;; ==========================================================
;; Sepal.Length       5.8433     5.8885      5.006     6.8462
;; Sepal.Width        3.0573     2.7377      3.428     3.0821
;; Petal.Length        3.758     4.3967      1.462     5.7026
;; Petal.Width        1.1993      1.418      0.246     2.0795


(defn kmeans-demo-2 []
  (cc/clusterer-cluster k-means-clusterer iris-dataset))

;; #<ClojureInstances @relation 'clustered iris'

;; @attribute Sepal.Length numeric
;; @attribute Sepal.Width numeric
;; @attribute Petal.Length numeric
;; @attribute Petal.Width numeric
;; @attribute class {0,1,2}

;; @data
;; 5.1,3.5,1.4,0.2,1
;; 4.9,3,1.4,0.2,1
;; 4.7,3.2,1.3,0.2,1
;; ...
;; 6.5,3,5.2,2,2
;; 6.2,3.4,5.4,2.3,2
;; 5.9,3,5.1,1.8,0>

;; =======================
;; hierarchical clustering
;; =======================

(def h-clusterer (cc/make-clusterer :cobweb))

;; user> (train-clusterer h-clusterer iris-dataset)
;; #<Cobweb Number of merges: 0
;; Number of splits: 0
;; Number of clusters: 3

;; node 0 [150]
;; |   leaf 1 [96]
;; node 0 [150]
;; |   leaf 2 [54]

;; >

; ========================
; Expectation Maximization
; ========================

(defn em-demo [] 
  
   (def em-clusterer (cc/make-clusterer :expectation-maximization
                                  {:number-clusters 3}))

   (train-clusterer em-clusterer iris-dataset))

;; #<EM
;; EM
;; ==

;; Number of clusters: 3


;;                Cluster
;; Attribute            0       1       2
;;                 (0.41)  (0.25)  (0.33)
;; =======================================
;; Sepal.Length
;;   mean           5.9275  6.8085   5.006
;;   std. dev.      0.4817  0.5339  0.3489

;; Sepal.Width
;;   mean           2.7503  3.0709   3.428
;;   std. dev.      0.2956  0.2867  0.3753

;; Petal.Length
;;   mean           4.4057  5.7233   1.462
;;   std. dev.      0.5254  0.4991  0.1719

;; Petal.Width
;;   mean           1.4131  2.1055   0.246
;;   std. dev.      0.2627  0.2456  0.1043

;; >


;; ====================
;; self-organising-maps
;; ====================

(defn som-demo []
  (som/demo))

;; ============================
;; principal component analysis
;; ============================

(defn pca-demo []
  (pca/demo))

(defn ajw []
  (println "ajw"))

(defn km [] (ns clj-ml7.kmeans))
(defn hi [] (ns clj-ml7.hierarchical))
(defn incanter [] (ns clj-ml7.incanter))

