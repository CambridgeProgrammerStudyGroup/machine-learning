module KM.KMeans where

import KM.Vector

import Data.List

import Debug.Trace

import qualified Data.Map as M

-- ----------------------
-- clusterAssignmentPhase
-- ----------------------
-- Make map fro centroids to points

clusterAssignmentPhase :: (Vector v, Vectorizable e v) => [v] -> [e] -> M.Map v [e]
clusterAssignmentPhase centroids points =
  let initialMap = M.fromList $ zip centroids (repeat [])
   in foldr (\p m -> let chosenCentroid = minimumBy (\x y -> compare (distance x $ toVector p)
                                                                     (distance y $ toVector p))
                                                    centroids
                      in M.adjust (p:) chosenCentroid m)
            initialMap points

-- ----------------
-- newCentroidPhase
-- ----------------

newCentroidPhase :: (Vector v, Vectorizable e v) => M.Map v [e] -> [(v,v)]
newCentroidPhase = M.toList . fmap (centroid . map toVector)

-- ----------
-- shouldStop
-- ----------

shouldStop :: (Vector v) => [(v,v)] -> Double -> Bool
shouldStop centroids threshold = let ajw = foldr (\(x,y) s -> s + distance x y) 0.0 centroids
                                  in ajw < threshold

-- ------
-- kMeans
-- ------

kMeans :: (Vector v, Vectorizable e v) => (Int -> [e] -> [v]) -- initialization function
                                       -> Int                 -- number of centroids
                                       -> [e]                 -- the information
                                       -> Double              -- threshold
                                       -> [v]                 -- final centroids
kMeans i k points = kMeans' (i k points) points

kMeans' :: (Vector v, Vectorizable e v) => [v] -> [e] -> Double -> [v]
kMeans' centroids points threshold =
  let assignments     = clusterAssignmentPhase centroids points
      oldNewCentroids = newCentroidPhase assignments
      newCentroids    = map snd oldNewCentroids
   in if shouldStop oldNewCentroids threshold
      then newCentroids
      else kMeans' newCentroids points threshold

-- ----------------
-- initializeSimple
-- ----------------

initializeSimple :: Int -> [e] -> [(Double,Double)]
initializeSimple 0 _ = []
initializeSimple n v = (fromIntegral n, fromIntegral n) : initializeSimple (n-1) v

-- ===========
-- Ammendments
-- ===========

-- --------------
-- initializeTake
-- --------------
-- Amended to guarrantee that all initial centroids get used

initializeTake :: Int -> [(Double,Double)] -> [(Double,Double)]
initializeTake n v = take n v

initializeTake4 :: Int -> [(Double,Double,Double,Double)] -> [(Double,Double,Double,Double)]
initializeTake4 n v = take n v

-- ---
-- ajw
-- ---
-- If a centroid has an emnpty cluster thenitial code awards NaN's which blows up.

ajwf ((a,b),(c,d)) = if (isNaN c) then ((a,b),(a,b)) else ((a,b),(c,d))
ajwf2 = map ajwf

ajwf4 ((a,b,c,d),(e,f,g,h)) = if (isNaN e) then ((a,b,c,d),(e,f,g,h)) else ((a,b,c,d),(e,f,g,h))
ajwf24 = map ajwf4

-- -------
-- kMeans2
-- -------
-- Adaptation of kMeans'

kMeans2 i k points = kMeans2a (i k points) points
kMeans2a centroids points threshold =
         let assignments = clusterAssignmentPhase centroids points
             oldNewCentroids = ajwf2 $ newCentroidPhase assignments
             newCentroids = map snd oldNewCentroids
          in if shouldStop oldNewCentroids threshold
             then newCentroids
             else kMeans2a newCentroids points threshold

kMeans24 i k points = kMeans2a4 (i k points) points
kMeans2a4 centroids points threshold =
         let assignments = clusterAssignmentPhase centroids points
             oldNewCentroids = ajwf24 $ newCentroidPhase assignments
             newCentroids = map snd oldNewCentroids
          in if shouldStop oldNewCentroids threshold
          --   then newCentroids
             then clusterAssignmentPhase newCentroids points
             else kMeans2a4 newCentroids points threshold

