module MnistSamples

open System
open System.IO
open Sample

let mnistSamples() = 
    let sourceDir = (new DirectoryInfo(AppDomain.CurrentDomain.BaseDirectory)).Parent.Parent
    let dataDir = Path.Combine(sourceDir.FullName, "MnistData");
    let dataFile = Path.Combine(dataDir, "Training.csv");

    let getTargetArray targetText = 
        let target = int targetText
        [|0..9|] |> Array.map (fun i -> if i = target then 1. else 0.)

    let allSamples =
        File.ReadAllLines(dataFile)
        |> Seq.skip 1
        |> Seq.map (fun line -> 
           { Input = (getFloatsFromText (line.Substring 2)) 
             Target = (getTargetArray (line.Substring(0, 1))) })
        |> Seq.toArray
    let testCount = allSamples.Length / 10
    let firstTest = allSamples.Length - testCount
    allSamples.[..(firstTest - 1)], allSamples.[firstTest..]
    
            

