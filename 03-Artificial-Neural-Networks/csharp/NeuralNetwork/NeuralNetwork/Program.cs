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
            const int numInHiddenLayer = 5;
            const int numOfOutputs = 10;
            
            var csvInputs = new InputFileReader().ReadInputFile();

            //take first specimen to start with
            var specimen = csvInputs[0];
            var specimenInputs = specimen.Item2.ToList();
            var sensoryInputs = new List<IInput>();
            foreach (double input in specimenInputs)
            {
                var sensoryInput = new SensoryInput();
                sensoryInput.UpdateValue(input);
                sensoryInputs.Add(sensoryInput);
            }

            List<IInput> inputLayer = CreateLayer(sensoryInputs.Count, sensoryInputs);
            List<IInput> hiddenLayer = CreateLayer(numInHiddenLayer, inputLayer);
            List<IInput> outputLayer = CreateLayer(numOfOutputs, hiddenLayer);

            for (int i = 0; i < numOfOutputs; i++)
            {
                double desired = i == specimen.Item1 ? 1.0d : 0.0d;
                double output = outputLayer[i].GetValue();
                double error = desired - output;
                ((INeuron)outputLayer[i]).Train(error);
            }

            outputLayer.ForEach(neuron=>Console.WriteLine(neuron.GetValue()));
        }

        private static IEnumerable<double> DesiredOutputsForSpecimen(int decimalAnswer)
        {
            for (int i = 0; i < 10; i++)
            {
                yield return i == decimalAnswer ? 1.0d : 0.0d;
            }
        } 

        private static List<IInput> CreateLayer(int layerSize, List<IInput> inputs)
        {
            var layerBias = new BiasInput(1.0d);
            var layer = new List<IInput>();
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
