module Trainer

open System
open Network
open Sample

type stats = 
  { mutable CurrentSampleCount : int64
    mutable TotalSampleCount : int64
    mutable EpochCount : int64 }

let trainAllCases net samples stats =
    samples 
    |> Array.iter(fun sample ->
        feedForward net sample.Input |> ignore
        propagateBack net sample.Target |> ignore
        stats.CurrentSampleCount <- stats.CurrentSampleCount + 1L
        stats.TotalSampleCount <- stats.TotalSampleCount + 1L )
    stats.EpochCount <- stats.EpochCount + 1L
    stats.CurrentSampleCount <- 0L

let measureAccuracy net samples checkCorrect =
    let correctCount =
        samples
        |> Seq.map (fun sample -> feedForward net sample.Input |> checkCorrect sample.Target)
        |> Seq.filter (id)
        |> Seq.length
    (correctCount, Array.length samples)

let writeAccuracy correctCount sampleCount sampleName =
    let getPercent n d = 100. * (float n) / (float d)
    Console.WriteLine("{0}: {1:n2}% correct ({2:n0}/{3:n0})",
        sampleName, (getPercent correctCount sampleCount), correctCount,sampleCount);

let rnd = new Random()
let shuffle cases =
    let caseCount = Array.length cases
    for i in seq{ 0 .. (caseCount - 1)} do
        let randomIndex = int (rnd.NextDouble() * float caseCount)
        let rTemp = cases.[randomIndex]
        cases.[randomIndex] <- cases.[i]
        cases.[i] <- rTemp

let trainUntilDone net (cases : Sample[]) (testCases : Sample[]) checkCorrect showEpochStats =
    let now() = DateTime.UtcNow
    let start = now()
    Console.WriteLine("Got {0:n0} training cases and {1:n0} test cases", cases.Length, testCases.Length);
    let mutable trainCorrect, trainCount = measureAccuracy net cases checkCorrect
    writeAccuracy trainCorrect trainCount "Training"
    let testCorrect, testCount = measureAccuracy net testCases checkCorrect
    writeAccuracy testCorrect testCount "    Test"
    Console.WriteLine("Starting first epoch...")
    Console.WriteLine("================================")
    let stats = {CurrentSampleCount = 0L; TotalSampleCount = 0L; EpochCount = 0L}
    while trainCorrect < trainCount do
        let epochStart = now()
        shuffle cases
        let trainingStart = now()
        let trainResult = trainAllCases net cases stats
        let trainingEnd = now()
        let newTrainCorrect, newTrainCount = measureAccuracy net cases checkCorrect
        trainCorrect <- newTrainCorrect
        trainCount <- newTrainCount
        if showEpochStats || trainCorrect = trainCount then
            Console.WriteLine("Epoch {0} complete:", stats.EpochCount)
            writeAccuracy trainCorrect trainCount "Training"
            let testCorrect, testCount = measureAccuracy net testCases checkCorrect
            writeAccuracy testCorrect testCount "    Test"
            Console.WriteLine("Training time: {0:mm\\:ss}", (trainingEnd - trainingStart))
            Console.WriteLine("   Epoch time: {0:mm\\:ss}", (DateTime.UtcNow - epochStart))
            Console.WriteLine("     Run time: {0:mm\\:ss}", (DateTime.UtcNow - start))
            Console.WriteLine("================================")
