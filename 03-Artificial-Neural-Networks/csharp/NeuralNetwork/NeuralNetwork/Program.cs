using System;
using System.Collections.Generic;
using System.Linq;
using Neuron;

namespace NeuralNetwork
{
    public class Program
    {
        private static readonly IActivationFunction Activation = new SigmoidActivation();
        public static void Main()
        {
            const int numInHiddenLayer = 500;
            const int numOfOutputs = 10;

            var csvInputs = new InputFileReader().ReadInputFile();
            var sensoryInputs = new List<SensoryInput>();
            sensoryInputs = csvInputs[0].Item2.Select(i => new SensoryInput()).ToList();

            // create the three layers
            List<INeuron> inputLayer = CreateLayer(sensoryInputs.Count, sensoryInputs.Cast<IInput>().ToList());
            List<INeuron> hiddenLayer = CreateLayer(numInHiddenLayer, inputLayer.Cast<IInput>().ToList());
            List<INeuron> outputLayer = CreateLayer(numOfOutputs, hiddenLayer.Cast<IInput>().ToList());

            Console.WriteLine("Training...");

            //var specimen = csvInputs[0];
            foreach (var specimen in csvInputs.Take(2))
            {
                var specimenInputs = specimen.Item2.ToList();
                for (int i = 0; i < specimenInputs.Count; i++)
                {
                    sensoryInputs[i].UpdateValue(specimenInputs[i]);
                }

                inputLayer.ForEach(neuron => neuron.Update());
                hiddenLayer.ForEach(neuron => neuron.Update());
                outputLayer.ForEach(neuron => neuron.Update());

                for (int k = 0; k < numOfOutputs; k++)
                {
                    double desired = k == specimen.Item1 ? 1.0d : 0.0d;
                    double output = outputLayer[k].GetValue();
                    double error = desired - output;
                    outputLayer[k].Train(error);
                }
                for (int j = 0; j < numInHiddenLayer; j++)
                {
                    var thisNeuron = hiddenLayer[j];
                    var errorContribution =
                        outputLayer.Sum(outputNeuron => outputNeuron.Inputs[thisNeuron].Weight * outputNeuron.Error);
                    thisNeuron.Train(errorContribution);
                }
                for (int i = 0; i < numInHiddenLayer; i++)
                {
                    var thisNeuron = inputLayer[i];
                    var errorContribution =
                        hiddenLayer.Sum(hiddenNeuron => hiddenNeuron.Inputs[thisNeuron].Weight * hiddenNeuron.Error);
                    thisNeuron.Train(errorContribution);
                }
            }
            inputLayer.ForEach(neuron => neuron.Update());
            hiddenLayer.ForEach(neuron => neuron.Update());
            outputLayer.ForEach(neuron => neuron.Update());

            outputLayer.ForEach(neuron => Console.WriteLine(neuron.GetValue()));
        }

        private static List<INeuron> CreateLayer(int layerSize, List<IInput> inputs)
        {
            var layerBias = new BiasInput(1.0d);
            var layer = new List<INeuron>();
            for (var i = 0; i < layerSize; i++)
            {
                var neuron = new Neuron.Neuron(Activation);
                neuron.RegisterInput(layerBias);
                inputs.ForEach(neuron.RegisterInput);
                layer.Add(neuron);
            }
            return layer;
        }
    }
}
