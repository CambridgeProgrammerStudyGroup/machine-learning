(ns neural.core
   (:use [neural.tan-perceptron 
          :only [tan-perceptron]]

         [neural.tan-backprop 
          :only [tan-backprop]]
         
         [neural.fatvat-perceptron 
          :only [fatvat-perceptron]]
         
         [neural.fatvat-backprop 
            :only [fatvat-backprop]]
                  
         [neural.encog 
          :only [ec-new ec-test xor-test ec-train ec-dataset]] 
              
         [neural.wali-backprop
          :only [wali-backprop]]
         
         [neural.csv 
          :only [ec-train-set ec-test-set tan-train-set write-csv]] )
    
)

;; =================
;; fatvat perceptron
;; =================

      
(defn fv-per-ls []
  
  (let [training-set [[[0 1 0] [0]]
                      [[1 0 0] [1]]]
     
        parameters {:training-set training-set}]
     
        (fatvat-perceptron parameters)))
  
(defn fv-per-xor []
  
  (let [training-set [[[0 1] [1]]
                      [[1 0] [1]]
                      [[0 0] [0]]
                      [[1 1] [0]]]
     
        parameters {:training-set training-set}]
     
        (fatvat-perceptron parameters)))


;; ===============
;; fatvat backprop
;; ===============

(defn fv-bp-xor []
  
  (let [training-set [[[0 1] [1]]
                      [[1 0] [1]]
                      [[0 0] [0]]
                      [[1 1] [0]]]
     
        parameters {:training-set training-set 
                    :iterations 100
                    :hidden 6
                    :learning-rate 0.5
                    :momentum 0.1}]
     
        (fatvat-backprop parameters)))



;; ===================
;; tanimoto perceptron
;; ===================


;; ----------------
;; tanimoto example
;; ----------------

(defn pn-computers []
 
   (let [acme-1 [5.0 7.0 90 2000]
         summit-95 [9.0 3.0 180 450]
         bright-10 [ 7.0 8.0 15 1000]
         turbox-2 [ 4.0 10.0 90 3000]

         super-2000 [1.0 200.0 730 99000]
         econo-001 [20.0 30.0 0 300]
         timbuk-2 [15.0 12.0 10 1500]
   
         parameters {:positives [acme-1 summit-95 bright-10 turbox-2]
                     :negatives [super-2000 econo-001 timbuk-2]
                     :threshold 10.0 }]
      (tan-perceptron parameters)))

;; ----
;; nand
;; ----

(defn pn-nand []
 
   (let [nn [0 0]
         ny [0 1]
         yn [1 0]
         yy [1 1] 
         
         parameters {:positives [nn ny yn] :negatives [yy] :threshold 10.0}]
   
   (tan-perceptron parameters)))  

;; ---
;; xor
;; ---

(defn pn-xor []
 
   (let [nn [0 0]
         ny [0 1]
         yn [1 0]
         yy [1 1] 
   
         parameters {:positives [ny yn] :negatives [yy nn] :threshold 10.0}]
         
   (tan-perceptron parameters)))  
     
;; ========================
;; tanimoto backpropagation
;; ========================

;; ----------------
;; tanimoto example
;; ----------------

(defn tan-stripes []
  
  (let [vertical   [1.0 0.0]
        horizontal [0.0 1.0]
        no-stripe  [0.0 0.0]
  
        training-set
        
   ;;; vertical striping examples...
  
   [[[0.0 0.0 1.0   0.0 0.0 1.0   0.0 0.0 1.0] vertical]
    [[0.0 1.0 0.0   0.0 1.0 0.0   0.0 1.0 0.0] vertical]
    [[0.0 1.0 1.0   0.0 1.0 1.0   0.0 1.0 1.0] vertical]
    [[1.0 0.0 0.0   1.0 0.0 0.0   1.0 0.0 0.0] vertical]
    [[1.0 0.0 1.0   1.0 0.0 1.0   1.0 0.0 1.0] vertical]
    [[1.0 1.0 0.0   1.0 1.0 0.0   1.0 1.0 0.0] vertical]

   ;;; horizontal striping examples... 
   
    [[0.0 0.0 0.0     0.0 0.0 0.0   1.0 1.0 1.0] horizontal]
    [[0.0 0.0 0.0     1.0 1.0 1.0   0.0 0.0 0.0] horizontal]
    [[0.0 0.0 0.0     1.0 1.0 1.0   1.0 1.0 1.0] horizontal]
    [[1.0 1.0 1.0     0.0 0.0 0.0   0.0 0.0 0.0] horizontal]
    [[1.0 1.0 1.0     0.0 0.0 0.0   1.0 1.0 1.0] horizontal]
    [[1.0 1.0 1.0     1.0 1.0 1.0   0.0 0.0 0.0] horizontal]

   ;;; Non striped examples...
   
    [[0.0 0.0 0.0     0.0 0.0 0.0   0.0 0.0 0.0] no-stripe]
    [[1.0 1.0 1.0     1.0 1.0 1.0   1.0 1.0 1.0] no-stripe]
    [[1.0 0.0 1.0     0.0 1.0 0.0   1.0 0.0 1.0] no-stripe]
    [[0.0 1.0 0.0     1.0 0.0 1.0   0.0 1.0 0.0] no-stripe]] 
  
  
      parameters {:training-set training-set :n-hidden 5 
                  :iterations 1000 :target-error 0.01
                  :beta 1.0 :eta-start 0.1 :eta-run-thresh 5}]
  
      (tan-backprop parameters)))

;; ---
;; xor
;; ---

(defn tan-xor []
  
  (let [training-set
        
        [[[0.0 0.0] [0.0]]
         [[0.0 1.0] [1.0]]
         [[1.0 0.0] [1.0]]
         [[1.0 1.0] [0.0]]] 
  
        parameters {:training-set training-set :n-hidden 3 
                    :iterations 1000 :target-error 0.01
                    :beta 1.0 :eta-start 0.1 :eta-run-thresh 5}]

  
  (tan-backprop parameters))
)  
 
;; ---
;; hcr
;; ---

(defn tan-hcr []

  (let [training-set (tan-train-set 100 2)
        parameters {:training-set training-set :n-hidden 20
                    :iterations 1000 :target-error 0.01
                    :beta 1.0 :eta-start 0.1 :eta-run-thresh 5
                    :modulo 1}]
  (tan-backprop parameters))) 
       
;; ====================
;; wali backpropagation
;; ====================

(defn wali-xor []
   (let [sample-data [[[0 0] [0]]                   
                      [[0 1] [1]]
                      [[1 0] [1]]
                      [[1 1] [0]]]
     
         parameters { :iterations 100
                      :new true
                      
                      ; default options in original code 
                     
                      :max-iters 100
                      :desired-error 0.10
                      :hidden-neurons [6]
                      :learning-rate 0.3
                      :learning-momentum 0.01
                      :weight-epsilon 0.5} ]
     
       (wali-backprop sample-data parameters)))


;; =====
;; encog
;; =====

;; ---
;; xor
;; ---

(defn encog-xor [] 
  (ec-dataset [[0.0 0.0] [1.0 0.0] [0.0 1.0] [1.0 1.0]]
              [[0.0]     [1.0]     [1.0]     [0.0]]) 
  (ec-new 3)
    (let [parameters {:iterations 1000 :target-error 0.01}]
  
  (ec-train parameters)
  (xor-test)))
  
;; ---
;; hcr
;; ---
;; training 1000 down to 1.2% accuracy gives a success rate on 
;; a test set of 897:103
;; The good and nad images are dumped in files good-[r].csv and bad-[r].csv


(defn encog-hcr [r]
  (let [ds (ec-train-set 100 1000)  ; start and number of training excamples
        ts (ec-test-set 2000 1000)]  ; start and number of testing examples
    (ec-dataset (first ds) (second ds) (first ts) (second ts)))
  
  (ec-new 200)    ; create network - specify size of hidden layer
  
  (let [parameters {:iterations 10000 :target-error 0.01}]  ; stopping points
   
  (ec-train parameters))
  (let [[good bad] (ec-test)]
    (write-csv good bad (str r)))
  )

;; =========
;; shortcuts
;; =========

(defn tanb [] (ns neural.tan-backprop)) 
(defn csv [] (ns neural.csv)) 
(defn ec [] (ns neural.encog)) 
(defn fatp [] (ns neural.fatvat-perceptron))
(defn fatb [] (ns neural.fatvat-backprop))
(defn wali [] (ns neural.wali-backprop))

