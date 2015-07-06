(ns clj-ml7.som
  (:use [incanter core som stats charts datasets]))

(def iris-features (to-matrix (sel (get-dataset :iris)
                                   :cols [:Sepal.Length
                                          :Sepal.Width
                                          :Petal.Length
                                          :Petal.Width])))

(def som (som-batch-train
          iris-features :cycles 10 :alpha 0.5 :beta 3)) 


(defn plot-means [i-f f-m]
  (let [x (range (ncol i-f))
        cluster-name #(str "Cluster " %)]
    (-> (xy-plot x (nth f-m 0)
                 :x-label "Feature"
                 :y-label "Mean value of feature"
                 :legend true
                 :series-label (cluster-name 0))
        (add-lines x (nth f-m 1)
                   :series-label (cluster-name 1))
        (add-lines x (nth f-m 2)
                   :series-label (cluster-name 2))
        view)))

(defn print-clusters []
  (doseq [[pos rws] (:sets som)]
    (println pos \:
             (frequencies
              (sel (get-dataset :iris)
                   :cols :Species :rows rws)))))


(defn demo []
    
  (println (:dims som))  
  ;; [10.0 2.0]
  
  (println (:sets som))
  ;; {[4 1] (144 143 141 ... 102 100),
  ;;  [8 1] (149 148 147 ... 50),
  ;;  [9 0] (49 48 47 46 ... 0)}
  
  ;; plot the means of the feature vectors in each cell/cluster
  (def feature-mean
    (map #(map mean (trans
                   (sel iris-features :rows ((:sets som) %))))
       (keys (:sets som))))
  
  (plot-means iris-features feature-mean)
  
  (print-clusters)
  ;; [4 1] : {virginica 23}
  ;; [8 1] : {virginica 27, versicolor 50}
  ;; [9 0] : {setosa 50}
  ;; nil
)






