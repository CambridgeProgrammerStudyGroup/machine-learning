(ns clojurega.utils
  (:refer-clojure :exclude [* - + == /])
  (:require [clojure.core.matrix :refer :all]
            [clojure.core.matrix.operators :refer :all]))

(defn damerau–levenshtein [str1 str2]
  (let [l1 (count str1)
        l2 (count str2)
        mx (new-matrix :ndarray (inc l1) (inc l2))]
   (mset! mx 0 0 0)
   (dotimes [i l1]
     (mset! mx (inc i) 0 (inc i)))
   (dotimes [j l2]
     (mset! mx 0 (inc j) (inc j)))
   (dotimes [i l1]
     (dotimes [j l2]
       (let [i+ (inc i) j+ (inc j)
             i- (dec i) j- (dec j)
             cost (if (= (.charAt str1 i)
                         (.charAt str2 j))
                    0 1)]
         (mset! mx i+ j+
                (min (inc (mget mx i j+))
                     (inc (mget mx i+ j))
                     (+ (mget mx i j) cost)))
         (if (and (pos? i) (pos? j)
                  (= (.charAt str1 i)
                     (.charAt str2 j-))
                  (= (.charAt str1 i-)
                     (.charAt str2 j)))
           (mset! mx i+ j+
                  (min (mget mx i+ j+)
                       (+ (mget mx i- j-) cost)))))))
   (mget mx l1 l2)))