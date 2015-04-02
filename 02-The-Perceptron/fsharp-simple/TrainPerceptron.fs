(*  Sample Output
    =============
   
    got 48 samples for 'And'
        Accuracy:  25% with Bias 0.269006 and Weights: 0.889654, 0.563575
         -> Accuracy: 100% with Bias -0.730994 and Weights: 0.489654, 0.363575

    got 48 samples for 'Nand'
        Accuracy:  25% with Bias -0.169786 and Weights: -0.869274, 0.164414
         -> Accuracy: 100% with Bias 0.630214 and Weights: -0.469274, -0.235586

    got 48 samples for 'Or'
        Accuracy:  50% with Bias 0.068984 and Weights: 0.504184, -0.490573
         -> Accuracy: 100% with Bias -0.131016 and Weights: 0.504184, 0.309427

    got 24 samples for 'Not'
        Accuracy:   0% with Bias -0.448670 and Weights: 0.799302
         -> Accuracy: 100% with Bias 0.151330 and Weights: -0.200698

    got 48 samples for 'Xor'
        Accuracy:  50% with Bias 0.504136 and Weights: -0.179205, -0.897013
         -> Accuracy:  25% with Bias 0.304136 and Weights: -0.379205, -0.497013
*)

open System
open SampleData

let learningConstant = 0.1
let bias = 1.0

let activate sum =
    if sum > 0.0 then 1 else -1

let feedforward  weights inputs =
    let sum = (0.0, bias::inputs, weights) |||> List.fold2 (fun sum input weight -> sum + input * weight) 
    activate sum

let trainWithSample weights inputs desired =
    let error = float (desired - feedforward weights inputs)
    (bias::inputs, weights) ||> List.map2 (fun input weight -> weight + learningConstant * error * input) 

let trainWithSeries weights samples =
    (weights, samples) ||> List.fold (fun weights sample -> 
        (trainWithSample weights (sample.inputs) sample.desired)) 

let measureAccuracy weights samples = 
    let correctCount = 
        List.filter (fun sample -> (feedforward weights sample.inputs) = sample.desired) samples
        |> List.length
    float correctCount / float samples.Length

let rnd = System.Random()
let getRandomWeights sample =
    List.init (sample.inputs.Length + 1) 
        (fun _ -> rnd.NextDouble() * 2.0 - 1.0)           

let displayAccuracy weights samples = 
   printf "Accuracy: %3.0f%% with " (100.0 * (measureAccuracy weights samples))
   printfn "Bias %f and Weights: %s" weights.Head 
        (String.concat ", " (List.map (sprintf "%f")  weights.Tail))    

let trainAndDisplay (name, (samples: sample list)) =
    printfn "got %i samples for '%s'" samples.Length name 
    let weights = getRandomWeights samples.Head
    printf "    "; displayAccuracy weights samples
    let weights = trainWithSeries weights samples
    printf "     -> "; displayAccuracy weights samples
    printfn ""

[<EntryPoint>]
let main argv =     
    allSamples |> List.iter trainAndDisplay
    Console.ReadKey() |> ignore
    0 
