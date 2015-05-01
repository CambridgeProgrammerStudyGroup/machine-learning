(ns neural.csv
   (require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


(declare ec-train-set)

;; ===
;; csv
;; ===

; ---------
; demo code
; ---------

(defn line-read []
  (with-open [rdr (io/reader "/tmp/test.txt")]
    (doseq [line (line-seq rdr)]
      (println line))))

(defn line-write []
  (with-open [wrtr (io/writer "/tmp/test.txt" :append true)]
    (.write wrtr "Line to be appended")))

(defn csv-write []
  (with-open [out-file (io/writer "resources/out-file.csv")]
    (csv/write-csv out-file
                 [["abc" "def"]
                  ["ghi" "jkl"]])))
                  
(defn csv-read []
  (with-open [in-file (io/reader "resources/ajw.csv")]
    (doseq [line (csv/read-csv in-file)]
      (println line))))

(defn lazy-read []
(with-open [rdr (io/reader "resources/training.csv")]
    (doseq [line (line-seq rdr)]
    (println line))))

; ---------
; utilities
; ---------

(defn concatv [& a] (into [] (concat a)))

(defn normalise [x]
   (map #(/ % 255.0) x))

(defn roundup [x]
  (Math/round (* (read-string x) 255.0)))

(defn plus [x] 
  (+ 10 (read-string x)))

(defn repeat-v [n1 n2]
  (into [] (repeat n1 n2)))

(defn output-vector [n]
  (assoc (repeat-v 10 0.0) n 1.0))

(defn cloj-maps [f n x]
  (let [f' (nth (iterate #(comp f %) identity) n)]
    ((fn deep [y]
        (if (vector? y)
          (mapv deep y)
          (f' y)))
       x)))


;; ----------------------------------------
;; Split training.csv into individual files
;; ----------------------------------------

(defn round-up [x]
    (mapv #(Math/round ^Double (* % 255)) x))

(defn write-csv [good bad n]
   
  ; put labels on first line 

  (with-open [rdr (io/reader "resources/training.csv")
              wrtr-g (io/writer (str "resources/good-" n ".csv") :append true)
              wrtr-b (io/writer (str "resources/bad-" n ".csv") :append true)]
    (let [line (nth (line-seq rdr) 0)] 
          (.write wrtr-g (str line \newline))
          (.write wrtr-b (str line \newline) )))

  (with-open [out-file (io/writer (str "resources/good-" n ".csv") :append true)]
        (let [good-2 (mapv #(mapv roundup %) good)] 
             (csv/write-csv out-file (mapv #(assoc % 0 (/ (first %) 255)) good-2))
             (.write out-file (str  \newline))))
  
  (with-open [out-file (io/writer (str "resources/bad-" n ".csv") :append true)]
        (let [bad-2 (mapv #(mapv roundup %) bad)] 
             (csv/write-csv out-file (mapv #(assoc % 0 (/ (first %) 255)) bad-2))
        (.write out-file (str  \newline))))
  
)
  

;; --------------------------
;; training set for tanimoto
;; --------------------------

(defn tan-train-set [start how-many] 
  (let [ects (ec-train-set start how-many)]
     (map vector (first ects) (second ects))))
  
;; -----------------------
;; training set for encog
;; -----------------------
;; Grab how-many starting at start 



(defn ec-set [start how-many file]  
  (let [inputs (atom [])
        ideal (atom [])]
     
    (doseq [x (take how-many (drop (inc start) 
                                   (csv/read-csv (io/reader file))))]
      (let [vx (into [] (map read-string (rest x)))
            nvx (into [] (normalise vx))   
            ov (output-vector (read-string (first x)))]
       
      (reset! inputs (conj @inputs nvx))      
      (reset! ideal (conj @ideal ov)) 

    ))
     [@inputs @ideal] 
  ))

(defn ec-train-set [start how-many]
     (ec-set start how-many "resources/training.csv"))

(defn ec-test-set [start how-many]
     (ec-set start how-many "resources/training.csv"))

(defn core [] (ns neural.core))
