module LogicGates

open System
open Sample
open Network
open Trainer
open LogicGateCases

let checkCorrect (target : float[]) (output : float []) =
    let t = target.[0];
    let o = output.[0];
    (t = 1. && o > 0.7) || (t = 0. && o < 0.3);

let train() =
    let learnRate = 0.2

    Console.WriteLine("Training Not:")
    Console.WriteLine("=============")
    let net = createNetwork 1 [] 1 learnRate
    trainUntilDone net notCases [||] checkCorrect false

    let create21Network() = createNetwork 2 [] 1 learnRate

    Console.WriteLine("Training Or:")
    Console.WriteLine("=============")
    trainUntilDone (create21Network()) orCases [||] checkCorrect false

    Console.WriteLine("Training And:")
    Console.WriteLine("=============")
    trainUntilDone (create21Network()) andCases [||] checkCorrect false

    Console.WriteLine("Training Nand:")
    Console.WriteLine("=============")
    trainUntilDone (create21Network()) nandCases [||] checkCorrect false

    Console.WriteLine("Training Xor:")
    Console.WriteLine("=============")
    let net = createNetwork 2 [2] 1 learnRate
    trainUntilDone net xorCases [||] checkCorrect false


