module Neuron

type Neuron = 
   {InboundWeights : float[]
    mutable Value : float
    mutable Error : float}

let createNeuron inboundConnectionCount getRandomWeight = 
   {InboundWeights = 
        seq{1..inboundConnectionCount} 
        |> Seq.map (fun _ -> getRandomWeight())
        |> Seq.toArray
    Value = 0.
    Error = 0.}