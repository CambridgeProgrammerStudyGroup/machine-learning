open Common

open System
open System.Collections.Generic
open System.Text.RegularExpressions

type HamOrSpam =  Ham | Spam

let readDataset () = 
    let getDataAndExpectedText (line : string) = 
        let parts = line.Split( [|'\t'|], 2)
        (parts.[1], parts.[0])
        
    let getExpectedFromText = 
        function 
        | "ham" -> Ham
        | "spam" -> Spam
        | unknown -> failwith ("Oops! don't know about: " + unknown)
        
    let getDataFromText = id

    let skipLines = 0
    let testPortion = 0.1

    readLines "SMSSpamCollection.txt" skipLines
    |> Array.map getDataAndExpectedText
    |> Array.map (fun (dataText, expectedText) -> (getDataFromText dataText, getExpectedFromText expectedText))
    |> getDataset testPortion

type WordInfo = {
    mutable HamCount : int
    mutable PrWH : float
    mutable SpamCount : int
    mutable PrWS : float 
    mutable PrSW : float}

let getWords text =
    Regex.Split(text, @"\s+")
    |> Array.distinct

let train trainingSamples =
    let wordDict = new Dictionary<string, WordInfo>()
    let updateWordDict hamOrSpam word =
        let wordInfo = 
            if not (wordDict.ContainsKey word) then do
                wordDict.Add (word,
                    {HamCount = 0; PrWH = 0.0; 
                    SpamCount = 0; PrWS = 0.0;
                    PrSW = 0.0}) |> ignore
            wordDict.[word]
        match hamOrSpam with
        | Ham -> wordInfo.HamCount <- wordInfo.HamCount + 1
        | Spam -> wordInfo.SpamCount <- wordInfo.SpamCount + 1
        
    let trainSample (totalHam, totalSpam) sample =
        let distinctWords = 
            sample.Data
            |> getWords
            |> Array.iter (updateWordDict sample.Expected)
        match sample.Expected with
        | Ham -> (totalHam + 1, totalSpam)
        | Spam -> (totalHam , totalSpam + 1)

    let (totalHam, totalSpam) = 
        ((0, 0), trainingSamples) 
        ||> Seq.fold trainSample

    wordDict.Values
    |> Seq.iter(fun wi -> 
        let prWH = float wi.HamCount / float totalHam
        let prWS = float wi.SpamCount / float totalSpam
        wi.PrWH <- prWH
        wi.PrWS <- prWS
        wi.PrSW <- prWS / (prWS + prWH) )

    totalHam, totalSpam, wordDict

let categorize (wordDict : Dictionary<string, WordInfo>) text =
    let words = getWords text |> Array.filter (wordDict.ContainsKey)
    let prodSW, prodOneMinus = 
        ((1.0, 1.0), words)
        ||> Seq.fold (fun (pSW, pOneMinus) word ->
            let wordInfo = wordDict.[word]
            (pSW * wordInfo.PrSW), (pOneMinus * (1.0 - wordInfo.PrSW)))
    if prodSW / (prodSW + prodOneMinus) > 0.5 
        then Spam
        else Ham

[<EntryPoint>]
let main argv = 
    let dataset = readDataset ()
    let _, _, wordDict =  dataset.Training |> train
    let correctCount = 
        dataset.Test
        |> Seq.filter (fun sample ->
           (categorize  wordDict sample.Data) = sample.Expected)
        |> Seq.length

    printfn "got %.1f%% accuracy" ((100.0 * float correctCount) / float dataset.Test.Length)

    Console.ReadKey() |> ignore
    0
