; -----------------
; 7 clustering Data
; -----------------

; Using K-means clustering

(defn kcluster []
  (load-file ".//src//clj_ml7//kcluster.clj")
  (ns clj-ml7.kcluster))

; Clustering data using clj-ml

(defn clj []
  (load-file ".//src//clj_ml7//clj_clusterers.clj")
  (ns clj-ml7.clj-clusterers))

; Using hierarchical clustering

(defn hcluster []
  (load-file ".//src//clj_ml7//hcluster.clj")
  (ns clj-ml7.hcluster))

; clj_clusterers.clj

; Using expectation maximisation

; clj_clusterers.clj

; Using SOMs

(defn somcluster []
  (load-file ".//src//clj_ml7//som_cluster.clj")
  (ns clj-ml7.som-cluster))

; Reducing dimensions in the data

(defn pca []
  (load-file ".//src//clj_ml7//pca.clj")
  (ns clj-ml7.pca))
