(ns neural.core
   (:use [neural.perceptron 
          :only [positives negatives threshold
                 init-p learn-all show-all]]

         [neural.backprop 
          :only [n-inputs n-hidden n-outputs 
               example-inputs example-outputs n-examples
               beta eta-start eta-run-thresh
               init-b backprop]]

         [neural.encog 
          :only [ec-new ec-test ec-train
                 mlp MLP]]
))

;; -------------------
;; tanimoto perceptron
;; tanimoto example
;;      as template
;; ----------------

(defn computers []
 
   (def acme-1 [5.0 7.0 90 2000])
   (def summit-95 [9.0 3.0 180 450])
   (def bright-10 [ 7.0 8.0 15 1000])
   (def turbox-2 [ 4.0 10.0 90 3000])

   (def super-2000 [1.0 200.0 730 99000])
   (def econo-001 [20.0 30.0 0 300])
   (def timbuk-2 [15.0 12.0 10 1500])
   
   (reset! positives [acme-1 summit-95 bright-10 turbox-2])
   (reset! negatives [super-2000 econo-001 timbuk-2])
   (reset! threshold 10.0)
   
   (init-p)
   (learn-all 0)
   (show-all)
)

;; -------------------
;; tanimoto perceptron
;; nand
;; ----

(defn nand []
 
   (def nn [0 0])
   (def ny [0 1])
   (def yn [1 0])
   (def yy [1 1]) 
   
   (reset! positives [nn ny yn])
   (reset! negatives [yy])
   (reset! threshold 10.0)
   
   (init-p)
   (learn-all 0)
   (show-all)
)

(defn xor []
 
   (def nn [0 0])
   (def ny [0 1])
   (def yn [1 0])
   (def yy [1 1]) 
   
   (reset! positives [ny yn])
   (reset! negatives [yy nn])
   (reset! threshold 10.0)
   
   (init-p)
   (learn-all 100)
   (show-all)
)


;; ------------------------
;; tanimoto backpropagation
;; tanimoto example extracted as template
;; --------------------------------------

(defn stripes []
  
  ; Size of network
  
  (reset! n-inputs 9)
  (reset! n-hidden 5)
  (reset! n-outputs 2)

  ; Training set

  (def vertical   [1.0 0.0])
  (def horizontal [0.0 1.0])
  (def no-stripe  [0.0 0.0])
  
  (def training-set
        
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
    [[0.0 1.0 0.0     1.0 0.0 1.0   0.0 1.0 0.0] no-stripe]] )
    
  (reset! example-inputs (into [] (map first training-set)))
  (reset! example-outputs (into [] (map second training-set)))  
  (reset! n-examples (count training-set))

  ; Training parameters

  (reset! beta 1.0)  
  (reset! eta-start 0.1)
  (reset! eta-run-thresh 5)
  
  (init-b)
  (backprop)
  )

;; ------------------------
;; tanimoto backpropagation
;; applied to xor
;; --------------

(defn xor []
  
  ; Size of network
  
  (reset! n-inputs 2)
  (reset! n-hidden 3)
  (reset! n-outputs 1)

  ; Training set

  (def training-set
        
   ;;; vertical striping examples...
  
   [[[0.0 0.0] [0.0]]
    [[0.0 1.0] [1.0]]
    [[1.0 0.0] [1.0]]
    [[1.0 1.0] [0.0]]] )
    
  (reset! example-inputs (into [] (map first training-set)))
  (reset! example-outputs (into [] (map second training-set)))  
  (reset! n-examples (count training-set))

  ; Training parameters

  (reset! beta 1.0)  
  (reset! eta-start 0.1)
  (reset! eta-run-thresh 5)
  
  (init-b)
  (backprop)
  )

;; ----------
;; encog demo
;; ----------

(defn encog-xor []
  (ec-new mlp)
  (ec-train mlp)
  (ec-test MLP))
  

(defn nso [] (ns neural.perceptron)) 