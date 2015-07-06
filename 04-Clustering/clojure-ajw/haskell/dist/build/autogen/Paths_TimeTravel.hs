module Paths_TimeTravel (
    version,
    getBinDir, getLibDir, getDataDir, getLibexecDir,
    getDataFileName
  ) where

import qualified Control.Exception as Exception
import Data.Version (Version(..))
import System.Environment (getEnv)
import Prelude

catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
catchIO = Exception.catch


version :: Version
version = Version {versionBranch = [0,0,1], versionTags = []}
bindir, libdir, datadir, libexecdir :: FilePath

bindir     = "/home/andrew/.cabal/bin"
libdir     = "/home/andrew/.cabal/lib/TimeTravel-0.0.1/ghc-7.6.3"
datadir    = "/home/andrew/.cabal/share/TimeTravel-0.0.1"
libexecdir = "/home/andrew/.cabal/libexec"

getBinDir, getLibDir, getDataDir, getLibexecDir :: IO FilePath
getBinDir = catchIO (getEnv "TimeTravel_bindir") (\_ -> return bindir)
getLibDir = catchIO (getEnv "TimeTravel_libdir") (\_ -> return libdir)
getDataDir = catchIO (getEnv "TimeTravel_datadir") (\_ -> return datadir)
getLibexecDir = catchIO (getEnv "TimeTravel_libexecdir") (\_ -> return libexecdir)

getDataFileName :: FilePath -> IO FilePath
getDataFileName name = do
  dir <- getDataDir
  return (dir ++ "/" ++ name)
