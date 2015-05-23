module Network

open Layer
open Activation
open TrainingInfo

type Network = 
   {InputLayer : Layer
    NonInputLayers : Layer[] // hiden layers + output layer
    HiddenLayers : Layer[]
    OutputLayer : Layer
    Activation : Activation
    TrainingInfo : TrainingInfo}

let createNetwork inputSize hiddenSizes outputSize learnRate = 
    let activation = Activation.sigmoidActivation
    let inputLayer = createLayer inputSize 0 activation.GetRandomWeight 
    let allLayers = 
        (inputLayer, hiddenSizes @ [outputSize])
        ||> List.scan (fun previousLayer size ->
            createLayer size (previousLayer.Neurons.Length + 1) activation.GetRandomWeight )
    let nonInputLayers = allLayers.Tail |> List.toArray
    let hiddenLayers = nonInputLayers.[0 .. (nonInputLayers.Length-2)]
    {   InputLayer = inputLayer
        NonInputLayers = nonInputLayers
        HiddenLayers = hiddenLayers
        OutputLayer = nonInputLayers.[nonInputLayers.Length - 1]
        Activation = activation
        TrainingInfo = {LearnRate = learnRate} }

let getLayerValues layer =
    seq{ for neuron in layer.Neurons do yield neuron.Value}

let getLayerValuesAndBias layer = 
    seq{ 
        yield! getLayerValues layer 
        yield 1.}
    |> Seq.toArray


let feedForward net input =
    (net.InputLayer.Neurons, input) 
        ||> Seq.iter2 (fun n i -> n.Value <- i)
    let outputLayer =
        (net.InputLayer, net.NonInputLayers)
        ||> Array.fold (fun previousLayer layer ->
            let previousLayerValues = getLayerValuesAndBias previousLayer
            layer.Neurons 
            |> Array.iter (fun neuron ->
                let sumOfIncoming = 
                    (0., neuron.InboundWeights, previousLayerValues) 
                    |||> Array.fold2 (fun sum weight pv -> sum + weight * pv)
                neuron.Value <- net.Activation.CalcValue sumOfIncoming)
            layer)
    getLayerValues outputLayer |> Seq.toArray


let propagateBack net target =
    let adjustWeightsBetweenLayers inputLayer layer =
        let inputLayerValues = getLayerValuesAndBias inputLayer
        layer.Neurons
        |> Array.iter (fun neuron ->
            inputLayerValues |> Array.iteri (fun index inputValue->
                let weightDelta = net.TrainingInfo.LearnRate * neuron.Error * inputValue
                neuron.InboundWeights.[index] <- neuron.InboundWeights.[index] + weightDelta))

    // set output layer errors
    (target, net.OutputLayer.Neurons)
    ||> Array.iter2 (fun targetValue neuron -> 
        let valueDelta = targetValue - neuron.Value
        neuron.Error <- valueDelta * net.Activation.CalcDerivative neuron.Value)
         
    // foldback updating neuron errors and adjusting weights
    let layerAfterInput = 
        (net.HiddenLayers, net.OutputLayer)
        ||> Array.foldBack(fun layer outLayer ->
            layer.Neurons
            |> Array.iteri(fun index neuron -> 
                let valueDelta =  
                    (0., outLayer.Neurons)
                    ||> Array.fold (fun error outNeuron ->
                        error + (outNeuron.InboundWeights.[index] * outNeuron.Error))
                neuron.Error <- valueDelta * net.Activation.CalcDerivative neuron.Value)
            adjustWeightsBetweenLayers layer outLayer
            layer)
    
    adjustWeightsBetweenLayers net.InputLayer layerAfterInput
    
   
