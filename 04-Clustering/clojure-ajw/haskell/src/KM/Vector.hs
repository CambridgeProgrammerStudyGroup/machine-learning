{-# LANGUAGE MultiParamTypeClasses, FlexibleInstances #-}

module KM.Vector where

import Data.Default
import Data.List.Split

-- ------
-- Vector
-- ------

class (Default v, Ord v) => Vector v where
  distance :: v -> v -> Double
  centroid :: [v] -> v

instance Vector (Double, Double) where
  distance (a,b) (c,d) = sqrt $ (c-a)*(c-a) + (d-b)*(d-b)
  centroid lst = let (u,v) = foldr (\(a,b) (c,d) -> (a+c,b+d)) (0.0,0.0) lst
                     n = fromIntegral $ length lst
                  in (u / n, v / n)

instance Vector (Double, Double, Double, Double) where
  distance (a,b,c,d) (e,f,g,h) = sqrt $ (e-a)*(e-a) + (f-b)*(f-b) + (g-c)*(g-c) + (h-d)*(h-d)
  centroid lst = let (u,v,w,x) = foldr (\(a,b,c,d) (e,f,g,h) -> (a+e,b+f,c+g,d+h)) (0.0,0.0,0.0,0.0) lst
                     n = fromIntegral $ length lst
                  in (u / n, v / n, w / n, x / n)

-- ------------
-- Vectorizable
-- ------------

class Vector v => Vectorizable e v where
  toVector :: e -> v

instance Vectorizable (Double,Double) (Double,Double) where
  toVector = id

instance Vectorizable String (Double,Double) where
  toVector e = (p1, p2)
               where petals = splitOn "," e
                     p1 = read (petals !! 0) :: Double
                     p2 = read (petals !! 1) :: Double

instance Vectorizable (Double,Double,Double,Double) (Double,Double,Double,Double) where
  toVector = id

instance Vectorizable String (Double,Double,Double,Double) where
  toVector e = (p1, p2, p3, p4)
               where petals = splitOn "," e
                     p1 = read (petals !! 0) :: Double
                     p2 = read (petals !! 1) :: Double
                     p3 = read (petals !! 2) :: Double
                     p4 = read (petals !! 3) :: Double



