module Activation
open System

type Activation = 
   {CalcValue : float -> float
    CalcDerivative : float -> float
    GetRandomWeight : unit -> float}

let rnd = new Random()
let public sigmoidActivation = 
   {CalcValue = (fun x -> 1. / (1. + exp -x))
    CalcDerivative = (fun x -> x * (1. - x))
    GetRandomWeight = (fun () -> rnd.NextDouble() * 2. - 1.)} 