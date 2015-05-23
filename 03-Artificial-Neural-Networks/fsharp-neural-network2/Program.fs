open System

[<EntryPoint>]
let main argv = 
    LogicGates.train()
    Mnist.train 18900 0.0015
    Console.ReadKey() |> ignore
    0 
