{-# LANGUAGE CPP, TemplateHaskell, TransformListComp, ParallelListComp #-}
-----------------------------------------------------------------------------
--
-- Module      :  Main
-- Copyright   :
-- License     :  AllRightsReserved
--
-- Maintainer  :
-- Stability   :
-- Portability :
--
-- |
--
-----------------------------------------------------------------------------

module Main (
    main
) where

-- ------
-- Leksah
-- ------

import System.Exit (exitFailure)
import Test.QuickCheck.All (quickCheckAll)
-- import Data.List (stripPrefix)

-- ------
-- kMeans
-- ------

import KM.Vector
import KM.KMeans
import System.IO
import Debug.Trace
import Control.Monad (unless, liftM)
import Data.List.Split
import qualified Data.Map as M
import Data.Maybe
import Data.List

--instance (Show (a0 -> M.Map (Integer, [Char]) a0)) where
--   show a0 = show a

-- ------------------
-- Leksah boilerplate
-- ------------------

-- Simple function to create a hello message.

hello s = "Hello " ++ s

-- Tell QuickCheck that if you strip "Hello " from the start of
-- hello s you will be left with s (for any s).

prop_hello s = stripPrefix "Hello " (hello s) == Just s

-- Hello World

exeMain = do
    putStrLn (hello "World")

-- Entry point for unit tests.

testMain = do
    allPass <- $quickCheckAll -- Run QuickCheck on all prop_ functions
    unless allPass exitFailure

-- This is a clunky, but portable, way to use the same Main module file
-- for both an application and for unit tests.
-- MAIN_FUNCTION is preprocessor macro set to exeMain or testMain.
-- That way we can use the same file for both an application and for tests.

#ifndef MAIN_FUNCTION
#define MAIN_FUNCTION exeMain
#endif


main :: IO ()
main = do (pPoints,pKinds) <- getPetals

          let irisMap = M.fromList $ zip pPoints pKinds
              result = kMeans24 initializeTake4 3 (pPoints::[(Double,Double, Double, Double)]) 0.001
              results = getResults irisMap (M.toList result)

          putStrLn $ "Results: "
          putLstLn results

          return ()

getPetals = do
              withFile "iris3.data"  ReadMode  $ \inHandle ->
                loop inHandle [] []
       where loop inHandle petalsList kindList = do
               isEof <- hIsEOF inHandle
               if not isEof
                  then do client <- hGetLine inHandle
                          let petals2 = toVector client :: (Double, Double, Double, Double)
                              newListP = (petals2 : petalsList)

                              petals = splitOn "," client
                              kind = case (petals !! 4) of
                                        "Iris-virginica"    -> "v"
                                        "Iris-setosa"       -> "s"
                                        "Iris-versicolor"   -> "c"
                              newListK = (kind : kindList)

                          loop inHandle newListP newListK
                  else do -- putStrLn $ show $ reverse petalsList
                          -- putStrLn $ show $ reverse kindList
                          return $ (reverse petalsList, reverse kindList)




-- ---------
-- utilities
-- ---------

majw k v =  let m = M.fromList []
            in M.insert k v m


majwf = map majw $ zip [1,2,3] ["1","2","3"]

-- let m = M.fromList [] in map ((\a (\b) -> M.insert k v m) [(1,"1"),(2,"2"),(3,"3")]

frequency s = map (\x->([head x], length x)) . group . sort $ s

getResults irisMap [] = []
getResults irisMap (x:xs) = (getResult irisMap x : getResults irisMap xs)

getResult irisMap x = frequency (map (\a -> (fromJust (M.lookup a irisMap))) (snd x))

putLstLn [] = putStrLn ""
putLstLn (x:xs) = do putStrLn $ show x
                     putLstLn xs
