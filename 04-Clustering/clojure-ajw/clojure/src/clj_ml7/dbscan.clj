(ns clj-ml7.dbscan
    
  (:require [incanter.core :as ic] 
            [incanter.datasets :as id]
            [incanter.stats :as st] ; pca
            [incanter.charts :as ch]
            
            [clj-ml.data :as cd]
            [clojure.math.numeric-tower :as math]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            
            [clj-ml7.utils :as ut]  )
 
  )


(declare get-iris-map)


;; =================================
;; code take from git: NeoTeo/DBScan
;; The original version only works on single floating point numbers
;; eg. (dbscan [1.0 2.0 99.0 3.0] 10 3)
;; ====================================

; (addIfNew [1.0 1.0] [[1.0 1.1] [2.0 2.0]]) 
;  => [[1.0 1.0] [1.0 1.1] [2.0 2.0]]

(defn addIfNew [x xs]
	(if-not (some #{x} xs)
		(into [] (cons x xs))
		xs))

(defn check-distance [new-point original-point eps]
  
 ;   (<= (Math/abs (- new-point original-point)) eps))           ;[git]
     (<= (Math/abs (ut/distance new-point original-point)) eps)) ;[ajw]

; -----------
; reqionQuery
; -----------
; return all points in data within p's eps-neighbourhood (including p)
; >(regionQuery [[1.0 1.0] [2.0 2.0] [3.0 3.0] [97.0 97.0] [98.0 98.0] [99.0 99.0]] [97.1 97.1] 3)
;  => ([97.0 97.0] [98.0 98.0] [99.0 99.0])


(defn regionQuery [data p eps]
  ; (println data)

	(if (empty? data)
		'[]
		; adding the closing parens after typing "(first data" after datval hangs the editor
		;(if-let [datval (cons (first data) (regionQuery (rest data) p eps))
		(let [datval (first data)]
;			(if (<= (Math/abs (- datval p)) eps) ;[git]
	(if (check-distance datval p eps)        ;[ajw]     
       (cons datval (regionQuery (rest data) p eps))
				(regionQuery (rest data) p eps)))))

(defn expandCluster [data p neighbourPts eps minpts visited clusters]
	(loop [neighbourPts neighbourPts visited visited cluster []]
		(if-not (seq neighbourPts)
			[cluster visited]
			(let [pp (first neighbourPts) 
				ppBeenVisited (some #{pp} visited)
				newVisited (addIfNew pp visited)
				newNeighbourPts (if-not ppBeenVisited
									(let [np (regionQuery data pp eps)]
										(if (>= (count np) minpts)
											(distinct (concat neighbourPts np)) ;investigate speed implications of distinct
											neighbourPts))
									neighbourPts)
 				newCluster (if-not (some #{pp} (apply concat (conj clusters cluster))) ;investigate speed implications of flatten
								(cons pp cluster)
								cluster)]
				(recur (rest newNeighbourPts) newVisited newCluster)))))



(defn dbscan [data eps minpts]
	(loop [unvisited data 
         visited [] 
         clusters []]
    (println "unvisited: " (count unvisited)) 
		(if-not (seq unvisited)
			(remove empty? clusters)
			(let [p (first unvisited)
				    neighbourPts (regionQuery data p eps)
				    [cluster newVisited] 
                (if (< (count neighbourPts) minpts)
					          [[] (addIfNew p visited)]
					          (expandCluster data p neighbourPts eps minpts (addIfNew p visited) clusters) )]
				(recur (rest unvisited) newVisited (cons cluster clusters)))))
	)


(defn get-iris-data []
    (with-open [in-file (io/reader "resources/iris.data")]
    (let [lis (doall (csv/read-csv in-file))
          k (map (fn [x] {:vec (vec (map read-string (take 4 x)))}) lis)]
        (vec k))))


;; -------------------------------------
;; Pincked and adapted from hierarchical
;; -------------------------------------
;; To print results at the end.

; Make list of types in a particular cluster

(defn iris-types [i n]
  
  (let [iris-map (get-iris-map)
        vecs (nth i n)]
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

(defn get-iris-map []
  (with-open [in-file (io/reader "resources/iris.data")]
    (let [lis (doall (csv/read-csv in-file))
          k (map (fn [x] (vec (map read-string (take 4 x)))) lis) 
          v (map (fn [x] (nth x 4)) lis)]
        (zipmap k v))))

; ====
; demo
; ====

(defn dbscan-iris [eps minpts]
   (let [iris-data (map (fn [x] (:vec x)) (get-iris-data))
         db-clusters-1 (dbscan iris-data eps minpts)
        ; _ (println "db" (count (apply concat db-clusters-1)))
         noise (filter (fn  [x] (not (some #{x} (apply concat db-clusters-1)))) iris-data)
        ; _ (println "noise" (count noise))   
         db-clusters (concat (list noise) db-clusters-1)]
        ; _ (println (count (apply concat db-clusters)))]
     db-clusters))
;(def db-cluster(demo 0.5 12))

(defn prepare-pca [lis]
   ;  (println "lis " lis)
   ;  (println (count lis))
     (loop [x 0
            ret-lis []]       
       (if (= x (count lis)) (vec ret-lis) 
         (recur (inc x) (concat (map (fn [z] (vec (concat z [x]))) (nth lis x)) ret-lis))) ))

(defn iris-pca [ds]
    (print 0) 
    (let [iris-matrix (ic/to-matrix (ic/to-dataset ds))
          _ (print 1)
          iris-features (ic/sel iris-matrix :cols (range 4))
          iris-species (ic/sel iris-matrix :cols 4)
          pca (st/principal-components iris-features)
          U (:rotation pca)
                    _ (print 4)
          U-reduced (ic/sel U :cols (range 2))
          reduced-features (ic/mmult iris-features U-reduced)]

     (ic/view (ch/scatter-plot (ic/sel reduced-features :cols 0)
                         (ic/sel reduced-features :cols 1)
                         :group-by iris-species
                         :x-label "PC1"
                         :y-label "PC2"))))


;; --------------
;; show-distances
;; --------------
;; histogram of distrances between data points

(defn show-distances []
    (let [iris-data (map (fn [x] (:vec x)) (get-iris-data))
          iris-distances    (loop [lis iris-data
             results []]   
        (println (count lis))        
        (if (= (count lis) 1) (vec results)
            (recur (rest lis) (concat (map (fn [x] (ut/distance (first lis) x)) (rest lis)) results) )))]
      (ic/view (ch/histogram iris-distances :nbins 10))
      ))
             
;; ------------
;; show regions
;; ------------
;; historgram of points within specified region

(defn show-regions [dist]
    (let [iris-data (map (fn [x] (:vec x)) (get-iris-data))
          hist-data (map (fn [x] 
             (count (regionQuery iris-data x dist))) iris-data)]
      (ic/view (ch/histogram hist-data :nbins 10))
      ))

;; ----
;; demo
;; ----

(defn demo [eps min-pts]
   (let [di (dbscan-iris eps min-pts)
         _ (iris-results di)
         dip (prepare-pca di)]
   (iris-pca dip)))


(defn core [] (ns clj-ml7.core))
 

