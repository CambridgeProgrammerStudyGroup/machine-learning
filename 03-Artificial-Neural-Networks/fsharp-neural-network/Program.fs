open System
open System.IO
open System.Text.RegularExpressions

open XOr
open Mnist

[<EntryPoint>]
let main argv =
    printfn "Hello"
    
    //CompareTheSampleDotCom.compare()
    //xor()
    
    let session = argv.[0]
    let learnRate = float argv.[1]
    let trainSize = int argv.[2]   
    mnist  session learnRate trainSize

    printfn "Done."
    Console.ReadKey() |> ignore
    0