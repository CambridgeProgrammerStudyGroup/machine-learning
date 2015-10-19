(ns clj-ml7.pca
  (:use [incanter core stats charts datasets]))

  (def iris-matrix (to-matrix (get-dataset :iris)))
  (def iris-features (sel iris-matrix :cols (range 4)))
  (def iris-species (sel iris-matrix :cols 4))

  (def pca (principal-components iris-features))

  (def U (:rotation pca))
  (def U-reduced (sel U :cols (range 2)))

  (def reduced-features (mmult iris-features U-reduced))
  
  (defn plot-reduced-features []
    (view (scatter-plot (sel reduced-features :cols 0)
                      (sel reduced-features :cols 1)
                      :group-by iris-species
                      :x-label "PC1"
                      :y-label "PC2")))

  (defn demo []
     (plot-reduced-features)
  )
  
  (defn fisher-pca []
    
      (let [iris-matrix (to-matrix (get-dataset :iris))
            iris-features (sel iris-matrix :cols (range 4))
            iris-species (sel iris-matrix :cols 4)
            pca (principal-components iris-features)
            U (:rotation pca)
            U-reduced (sel U :cols (range 2))
            reduced-features (mmult iris-features U-reduced)]
    
     (view (scatter-plot (sel reduced-features :cols 0)
                      (sel reduced-features :cols 1)
                      :group-by iris-species
                      :x-label "PC1"
                      :y-label "PC2"))))