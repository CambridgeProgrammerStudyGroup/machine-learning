using System;
using System.Collections.Generic;
using System.Linq;

namespace Neuron
{
    public class Neuron : INeuron
    {
        public IEnumerable<double> Weights { get; private set; }
        public double BiasWeight { get; private set; }
        private const double BiasValue = 1.0d;
        private const double TrainingConstant = 0.1d;
        private readonly Func<double, double> _activationFunction;
        
        public Neuron(IEnumerable<double> weights, double biasWeight, Func<double, double> activationFunction)
        {
            Weights = weights;
            BiasWeight = biasWeight;
            _activationFunction = activationFunction;
        }

        public double GetOutput(IEnumerable<double> inputs)
        {
            var signals = Weights.Zip(inputs, (weight, input) => weight * input);
            var output = signals.Sum() + (BiasValue * BiasWeight);
            return _activationFunction(output);
        }

        public void Train(IEnumerable<double> inputs, Func<double> errorFunction)
        {
            var listInputs = inputs.ToList();
            double error = errorFunction();
            Weights = Weights.Zip(listInputs, (weight, input) => ComputeNewWeight(weight, error, input)).ToList();
            BiasWeight = ComputeNewWeight(BiasWeight, error, BiasValue);
        }

        private double ComputeNewWeight(double weight, double error, double input)
        {
            return weight + (TrainingConstant * error * input);
        }
    }
}
