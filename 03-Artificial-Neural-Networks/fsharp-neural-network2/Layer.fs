module Layer

open Neuron

type Layer = 
   {Neurons : Neuron[]}

let createLayer neuronCount inboundConnectionCount getRandomWeight = 
   {Neurons = 
        seq{ 1 .. neuronCount }
        |> Seq.map (fun _ -> createNeuron inboundConnectionCount getRandomWeight)
        |> Seq.toArray}
    