(ns search.core-test
  (:require [expectations :refer :all]
            [search.core :refer :all]
            [search.depth-first :refer [list-difference]]))


;; An empty string should return 0
(expect '[a s d] (list-difference '(a s d) '(f g h)))

