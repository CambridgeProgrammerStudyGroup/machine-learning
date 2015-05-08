module MnistData

open System.IO

open Persistence

let mnistData =
    let buildAnswerList count index  =
        [for i in 0.0..(count - 1.0) -> if i = index then 1.0 else 0.0]
    let trainFile = dataDir + @"\training.csv";
    let availableSamples =
        (File.ReadAllLines trainFile).[1..] 
        |> Array.map (fun line -> line.Split(',')) 
        |> Array.map (fun fields -> fields |> Array.toList |> List.map (float))        
        |> Array.map (fun vals ->
            (vals.Tail, (buildAnswerList 10.0 vals.Head)))
    let availableCount = availableSamples.Length 
    let testingCount = availableCount / 10
    let trainingCount = availableCount - testingCount    
    (availableSamples.[..trainingCount-1], availableSamples.[trainingCount..])






