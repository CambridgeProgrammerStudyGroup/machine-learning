module Common

open System.IO

type Sample<'data, 'expected> = 
    { Data : 'data
      Expected : 'expected }

type DataSet<'data, 'expected> = 
    { Training : Sample<'data, 'expected> []
      Test : Sample<'data, 'expected> [] }

let readLines fileName skip = 
    __SOURCE_DIRECTORY__ + "/Data/" + fileName
    |> File.ReadAllLines
    |> Array.skip (skip)

let getDataset testPortion dataExpectedCouples  =
   let allSamples = 
        dataExpectedCouples
        |> Array.map (fun (data, expected) -> {Data = data; Expected = expected})
   let total = allSamples.Length
   let trainingCount = int (float total * testPortion)
   {    Training = allSamples.[..(total - (trainingCount + 1))]
        Test = allSamples.[(total - trainingCount)..] }


