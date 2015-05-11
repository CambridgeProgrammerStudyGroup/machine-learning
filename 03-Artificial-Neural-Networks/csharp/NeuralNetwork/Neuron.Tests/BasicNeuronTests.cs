using System;
using System.Collections.Generic;
using System.Linq;
using NUnit.Framework;

namespace Neuron.Tests
{
    [TestFixture]
    public class BasicNeuronTests
    {
        private readonly Func<double, double> _passthroughActivation =
            output => output;

        [Test]
        public void GetOutput_WithoutActivationFunction_ShouldReturnBiasValue_WhenThereAreNoInputs()
        {
            double biasWeight = 0.2d;
            var inputs = new double[] { };
            var weights = new double[] { };

            var neuron = new Neuron(weights, biasWeight, _passthroughActivation);
            double result = neuron.GetOutput(inputs);

            Assert.AreEqual(0.2d, result);
        }

        [Test]
        public void GetOutput_WithoutActivationFunction_ShouldReturnSumOfInputs_WhenWeightsAreAllUnity()
        {
            double biasWeight = 0.1d;
            var inputs = new[] {0.5d, 0.25d};
            var weights = new[] {1.0d, 1.0d};

            var neuron = new Neuron(weights, biasWeight, _passthroughActivation);
            double result = neuron.GetOutput(inputs);

            Assert.AreEqual(0.85d, result);
        }

        [Test]
        public void GetOutput_WithoutActivationFunction_ShouldReturnDotProductOfInputValuesAndWeights()
        {
            var biasWeight = 0.5d;
            var inputs = new[] {-1.0d, -0.5d};
            var weights = new[] {0.1d, 0.5d};

            var neuron = new Neuron(weights, biasWeight, _passthroughActivation);
            double result = neuron.GetOutput(inputs);

            Assert.AreEqual(0.15d, result, 0.01d);
        }

        [Test]
        public void Train_ShouldUpdateBiasWeight_WhenInputsAreZero_AndAnswerIsIncorrect()
        {
            var biasWeight = 0.0d;
            var inputs = new[] {0.0d, 0.0d};
            var weights = new[] {0.5d, 0.5d};

            double desiredAnswer = 1.0d;
            var neuron = new Neuron(weights, biasWeight, _passthroughActivation);
            neuron.Train(inputs, () => (desiredAnswer - neuron.GetOutput(inputs)));

            Assert.AreEqual(0.1d, neuron.BiasWeight);
        }
    }
}
