(ns clj-ml7.incanter

  
 ; (:use [incanter core charts datasets]) 
   (:use [incanter core datasets charts stats io pdf])
 
 
   (:import (org.jfree.chart.renderer.xy XYBarRenderer XYErrorRenderer StandardXYBarPainter)
            (org.jfree.chart.renderer GrayPaintScale)
            (org.jfree.data.xy YIntervalSeries YIntervalSeriesCollection XYBarDataset)
   
            ; line replace
   
            (org.jfree.chart.plot PlotOrientation DatasetRenderingOrder SeriesRenderingOrder)
            (org.jfree.data.xy DefaultHighLowDataset XYSeries XYSeriesCollection)
            (org.jfree.chart.renderer.xy XYLineAndShapeRenderer))
   
   
 )



;; ========
;; INCANTER
;; ========


  ; (import (org.jfree.chart.renderer.xy.XYBarRenderer))
        ;   ’org.jfree.chart.renderer.xy.XYErrorRenderer
        ;   ’org.jfree.chart.renderer.GrayPaintScale
        ;   ’org.jfree.data.xy.YIntervalSeries
        ;   ’org.jfree.data.xy.YIntervalSeriesCollection
        ;   ’org.jfree.data.xy.XYBarDataset
        ;   ’org.jfree.chart.renderer.xy.StandardXYBarPainter)

;(:use [incanter core charts pdf]))

;(use '(incanter core datasets charts stats io pdf))

;(use '(incanter core  io))

;(view (function-plot sin -4 4))
;(dataset ["x1" "x2" "x3"] [[1 2 3] [4 5 6] [7 8 9]])

;(get-dataset :iris)
;($ ($ :Sepal.Length (get-dataset :iris))

   
;(with-data (get-dataset :iris) [(mean ($ :Sepal.Length)) (sd ($ :Sepal.Length))]

; ----------------
; Column Selection
; ----------------

(defn column-1 []
  (with-data (get-dataset :iris) 
        (view $data)
        (view ($ [:Sepal.Length :Sepal.Width :Species]))
        (view ($ [:not :Petal.Width :Petal.Length]))
        (view ($ 0 [:not :Petal.Width :Petal.Length]))))

; -------------
; Row Selection
; -------------

(defn row-1 []
  ($where {"Species" "setosa"} (get-dataset :iris)))

(defn row-2 []
  ($where {"Petal.Width" {:lt 1.5}} (get-dataset :iris)))

(defn row-3 []
  ($where {"Petal.Width" {:gt 1.0, :lt 1.5}} (get-dataset :iris)))

(defn row-4 []
  ($where {"Petal.Width" {:gt 1.0, :lt 1.5} 
           "Species" {:in #{"virginica" "setosa"}}} 
               (get-dataset :iris)))
(defn row-5 []
  ($where (fn [row] 
           (or (< (row "Petal.Width") 1.0)
               (> (row "Petal.Length") 5.0)))
        (get-dataset :iris)))

;; =======
;;
;; =======

; 1 Loading Incanter's sample datasets

(defn demo-1 []
   (let [iris (get-dataset :iris)]
     (println (col-names iris))
     (println (nrow iris))
     (println (set ($ :Species iris)))
))

; 2 Viewing datasets interactively with view

(defn demo-2 []
  (let [iris (get-dataset :iris)]
    (view iris)))

; 3 Selecting columns with $

(defn demo-3 []
  (let [data-file "resources/all_160_P3_small.csv"
        race-data (read-dataset data-file :header true)]
    (println  ($ :POP100 race-data))
    (println  ($ (:STATE :POP100 :POP100.2000) race-data))  
    (println  ($ [:POP100] race-data))
    (println  ($ [:STATE :POP100 :POP100.2000] race-data)) 
 ;   (println ($ (:STATE :POP100 :P003002 :P003003 
 ;          :P003004 :P003005 :P003006 :P003007
 ;          :P003008)
 ;         race-data))
))


; 4 Filtering datasets with where

(defn demo-4 []
    (let [data-file "resources/all_160_in_51.P35.csv"
          va-data (read-dataset data-file :header true)
     ;     va-matrix (to-matrix ($ [:POP100 :HU100 :P035001] va-data))
          richmond ($where {:NAME "Richmond city"} va-data)
          small ($where {:POP100 {:lte 1000}} va-data)
          medium ($where {:POP100 {:gt 1000 :lt 40000}} va-data)]
      (view richmond)
      (println (nrow small))
      (view small)
      (view medium)))

;; ====
;; GREG
;; ====

(def xs  [1.0 11.0 25.0 33.0 49.0 100.0])
(def ys1 [2.2  3.8  4.1  2.2  2.5   3.5])
(def ys2 [1.0  3.5  4.7  3.1  1.4   4.5])


;; -------
;; XY plot
;; -------

(defn greg-0 []
  (let [greg (xy-plot xs ys1 :x-label "The x axis" 
                           :y-label "The y axis" 
                           :title "XY Plot"
                           :legend true 
                           :series-label "The 1st Series" 
                           :points true)]
  (add-lines greg xs ys2 :series-label "The 2nd Series" :points true)
  greg
  ))

  
; --------------------
; 1. Make axes visible
; --------------------
; http://www.jfree.org/jfreechart/api/javadoc/org/jfree/chart/JFreeChart.html
; http://www.jfree.org/jfreechart/api/javadoc/org/jfree/chart/axis/Axis.html
; http://www.jfree.org/jfreechart/api/javadoc/org/jfree/chart/plot/XYPlot.html

(defn greg-1 []
  (let [greg (greg-0)
        plot (.getPlot greg)]
  (.setAxisLineVisible (.getDomainAxis plot) true)
  (.setAxisLineVisible (.getRangeAxis plot) true)
  greg))

; ----------------
; 2. Set Tick Unit
; ----------------
; http://www.jfree.org/jfreechart/api/javadoc/org/jfree/chart/axis/NumberAxis.html
; http://www.jfree.org/jfreechart/api/javadoc/org/jfree/chart/axis/NumberTickUnit.html

(defn greg-2 []
    (let [greg (greg-1)
          plot (.getPlot greg)]
  (.setTickUnit (.getDomainAxis plot) (org.jfree.chart.axis.NumberTickUnit. 5.0))
  (.setTickUnit (.getRangeAxis plot) (org.jfree.chart.axis.NumberTickUnit. 1.0))
  greg))

; ---------------------------
; 3. Logarithmic Axis Style 1
; ---------------------------
; http://www.jfree.org/jfreechart/api/javadoc/org/jfree/chart/axis/LogarithmicAxis.html

(defn greg-3 []
    (let [greg (greg-1)
          plot (.getPlot greg)]
   (.setDomainAxis plot (org.jfree.chart.axis.LogarithmicAxis. "Logarithmic Axis Style 1"))     
  greg))


; ---------------------------
; 4. Logarithmic Axis Style 2
; ---------------------------
; http://www.jfree.org/jfreechart/api/javadoc/org/jfree/chart/axis/LogAxis.html

(defn greg-4 []
    (let [greg (greg-1)
          plot (.getPlot greg)]
   (.setDomainAxis plot (org.jfree.chart.axis.LogAxis. "Logarithmic Axis Style 2"))     
  greg))

; -------------------------
; 5. Change the Line Colour
; -------------------------
; http://docs.oracle.com/javase/6/docs/api/java/awt/Color.html

(defn greg-5 []
    (let [greg (greg-1)
          plot (.getPlot greg)]
       (.setSeriesPaint (.getRenderer plot 0) 0 java.awt.Color/black)
       (.setSeriesPaint (.getRenderer plot 1) 0 java.awt.Color/green)
    greg))   

; --------------------
; 6. Change the Stroke
; --------------------
; http://docs.oracle.com/javase/6/docs/api/java/awt/BasicStroke.html

(defn greg-6 []
    (let [greg (greg-1)
          plot (.getPlot greg)
          stroke (java.awt.BasicStroke. 1.0  ; pen width
                        java.awt.BasicStroke/CAP_ROUND ; end caps
                        java.awt.BasicStroke/JOIN_ROUND ; line joins
                        1.0                       ; miter limit 
                        (float-array 1.0 4.0)     ; dash  
                        0.0)]                     ; dash phase  
       (.setSeriesPaint (.getRenderer plot 0) 0 java.awt.Color/black)
       (.setSeriesPaint (.getRenderer plot 1) 0 java.awt.Color/green)
       (.setSeriesStroke (.getRenderer plot 0) 0 stroke)
       (.setSeriesStroke (.getRenderer plot 1) 0 stroke)
    greg)) 

; --------------------------
; 7. Bar Chart with Whiskers
; --------------------------

(def xs [0.0 1. 2. 3. 4. 5.])
(def ys [2.2 3.8 4.1 2.2 2.5 3.5])
(def errors [1.0 0.5 0.7 1.1 0.4 0.5])
;(def xy1 (xy-plot xs ys :x-label "The x axis" :y-label "The y axis"))



(defn xy-bar-chart [xs ys errors x-label y-label]
  (let [xychart (xy-plot xs ys :x-label x-label :y-label y-label)]
    xychart))   
    
(defn greg-7a [] (xy-bar-chart xs ys errors "The X Axis" "The Y Axis"))
        ; no _ (view xychart)
   
        
(defn greg-7b [] 
   (let [xychart (greg-7a)     
        ; First, extract the plot object from the chart object
        xyplot (.getPlot xychart)
        ; Create an XYBarRenderer and set the margin to 0.2 
        ;   (this makes a space between the bars).
        xybarrenderer (XYBarRenderer. 0.2)
        ; Play with some parameters which will affect the appearance of the bars.
        _ (.setShadowVisible xybarrenderer false)
        ; Change the color of the bars to gray. 
        ; The GrayPaintScale class makes this easier to accompl
        greypaint (GrayPaintScale. 0 255 100) ; 0-255 is the scale, and 100 is the transparency.
        paint (.getPaint greypaint 150)
        _ (.setSeriesPaint xybarrenderer 0 paint) ; This makes the bars more like the R version.
        ; Change the default bar painter to the StandardXYBarPainter to get rid of the default gradien
        _ (.setBarPainter xybarrenderer (StandardXYBarPainter.))
        _ (.setDrawBarOutline xybarrenderer true)
        ; Now change the "renderer" in the xy plot to the XYBarRenderer previously defined.      
        _ (.setRenderer xyplot xybarrenderer)]
        xychart))


(defn greg-7c []
   (let [xychart (greg-7b)    
        xyplot (.getPlot xychart)
        xyerrorrenderer (XYErrorRenderer.)
        _ (.setCapLength xyerrorrenderer 30)
        _ (.setSeriesShapesVisible xyerrorrenderer 0 false)
        _ (.setErrorPaint xyerrorrenderer java.awt.Color/black)
        _ (.setRenderer xyplot 1 xyerrorrenderer)
        yintervalcoll (YIntervalSeriesCollection.)
        yintervalser (YIntervalSeries. "errors")
        ; A function to add data to a YIntervalSeries.
        yintervalserpop (fn [xs ys errors]
                 ; "Populates a YIntervalSeries with data."                   
                 (doall (map (fn [a b c] 
                     (.add yintervalser a b (- b c) (+ b c))) xs ys errors)))]
        
        (yintervalserpop xs ys errors)
        (.addSeries yintervalcoll yintervalser)
        (.setDataset xyplot 1 yintervalcoll) 
    xychart))

; ----------------------------
; 8. XY plot with rapid update
; -------------------------j--

(defn replace-line [chart x y & options]
   (let [opts (when options (apply assoc {} options))
         data (:data opts)
         _x (if (coll? x) (to-list x) ($ x data))
         _y (if (coll? y) (to-list y) ($ y data))
         data-plot (.getPlot chart)
         n (.getDatasetCount data-plot) ;; This should be 1 for a new chart.
         series-lab (or (:series-label opts) (format "%s, %s" 'x 'y))
         points? (true? (:points opts))
         line-renderer (XYLineAndShapeRenderer. true points?)
         data-set (.getDataset data-plot) ;; Extract the XYSeriesCollection from the plot.
         ;; Extract the XYSeries from the XYSeriesCollection.
         data-series (.getSeries data-set (.getSeriesKey data-set 0))
         _ (.clear data-series)] ;; Clear the original data in the series.
         ;; Populate the data series with the supplied data.
         (dorun
           (map (fn [x y]
             (if (and (not (nil? x))
                      (not (nil? y)))
               (.add data-series (double x) (double y))))
               _x _y))
         (doto data-plot
           (.setSeriesRenderingOrder org.jfree.chart.plot.SeriesRenderingOrder/FORWARD)
           (.setDatasetRenderingOrder org.jfree.chart.plot.DatasetRenderingOrder/FORWARD)
           (.setDataset n data-set)
           (.setRenderer n line-renderer))
         chart))

;(def chart1 (xy-plot [0 1 2 3 4 5][1 0 3 5 2 1]))
;(view chart1)
;(replace-line chart1 [0 1 2 3 4 5][1 2 1 4 1 2])

;; =========
;; namespace
;; =========

(defn core [] (ns clj-ml7.core))
