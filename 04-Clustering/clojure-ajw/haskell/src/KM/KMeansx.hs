module Chapter6.KMeans where

import Chapter6.Vector

import Data.List
import qualified Data.Map as M

-- ---------------------
-- clusterAssigmentPhase
-- ---------------------

clusterAssignmentPhase :: (Vector v, Vectorizable e v) => [v] -> [e] -> M.Map v [e]
clusterAssignmentPhase centroids points =
  let initialMap = M.fromList $ zip centroids (repeat [])
   in foldr (\p m -> let chosenCentroid = minimumBy (\x y -> compare (distance x $ toVector p)
                                                                     (distance y $ toVector p))
                                                    centroids
                      in M.adjust (p:) chosenCentroid m)
            initialMap points


newCentroidPhase :: (Vector v, Vectorizable e v) => M.Map v [e] -> [(v,v)]
newCentroidPhase = M.toList . fmap (centroid . map toVector)

shouldStop :: (Vector v) => [(v,v)] -> Double -> Bool
shouldStop centroids threshold = foldr (\(x,y) s -> s + distance x y) 0.0 centroids < threshold

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



-- -------
-- kMeans4
-- -------

kMeans4 :: (Vector v, Vectorizable e v) => (Int -> [e] -> [v]) -- initialization function
                                        -> Int                 -- number of centroids
                                        -> [e]                 -- the information
                                        -> Double              -- threshold
                                        -> [v]                 -- final centroids
kMeans4 i k points = kMeans' (i k points) points

kMeans4' :: (Vector v, Vectorizable e v) => [v] ->  [e] -> Double -> [v]
kMeans4' centroids points threshold = do
  let assignments     = clusterAssignmentPhase centroids points
      oldNewCentroids = newCentroidPhase assignments
      newCentroids    = map snd oldNewCentroids
   in if shouldStop oldNewCentroids threshold
      then newCentroids
      else kMeans4' newCentroids points threshold

-- ----------------
-- initializeSimple
-- ----------------

initializeSimple :: Int -> [e] -> [(Double,Double)]
initializeSimple 0 _ = []
initializeSimple n v = (fromIntegral n, fromIntegral n) : initializeSimple (n-1) v

-- -----------------
-- initializeSimple4
-- -----------------

initializeSimple4 :: Int -> [e] -> [(Double,Double,Double,Double)]
initializeSimple4 0 _ = []
initializeSimple4 n v = (fromIntegral n, fromIntegral n, fromIntegral n, fromIntegral n) : initializeSimple4(n-1) v

