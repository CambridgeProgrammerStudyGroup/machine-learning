module Mnist

open System
open Network
open Trainer
open MnistSamples

let train trainingCount learnRate = 
    Console.WriteLine("Starting MNIST training");
    Console.Write("Loading samples... ");
    let mnistTraining, mnistTest = mnistSamples()
    Console.WriteLine("done");

    Console.Write("Creating network... ");
    let net = createNetwork 784 [500; 100] 10 learnRate
    Console.WriteLine("done");

    let cases = 
        mnistTraining 
        |> Seq.take (min mnistTraining.Length trainingCount)
        |> Seq.toArray
    let testCases = 
        mnistTest 
        |> Seq.take (min mnistTest.Length cases.Length)  
        |> Seq.toArray

    let indexOfMaxValue numbers =
        numbers
        |> Seq.mapi(fun i n -> i, n)
        |> Seq.maxBy snd
        |> fst

    let checkCorrect target output = 
        indexOfMaxValue target = indexOfMaxValue output

    trainUntilDone net cases testCases checkCorrect true
