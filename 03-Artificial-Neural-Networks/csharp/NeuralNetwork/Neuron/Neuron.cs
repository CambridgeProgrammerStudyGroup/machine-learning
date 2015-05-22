using System;
using System.Collections.Generic;
using System.Linq;

namespace Neuron
{
    public class Neuron : INeuron
    {
        public double Error { get; private set; }
        public Dictionary<IInput, ISynapse> Inputs { get; private set; }
        private double? _cachedOutput;
        private readonly IActivationFunction _activationFunction;

        public Neuron(IActivationFunction activationFunction)
        {
            _activationFunction = activationFunction;
            Inputs = new Dictionary<IInput, ISynapse>();
        }

        public void RegisterInput(IInput input)
        {
            Inputs.Add(input, new Synapse(input));
        }

        public void Update()
        {
            var dotProduct = Inputs.Select(kvp => kvp.Value)
                .Sum(synapse => synapse.Input.GetValue()*synapse.Weight);
            double output = _activationFunction.Activate(dotProduct);
            _cachedOutput = output;
        }

        public double GetValue()
        {
            if (_cachedOutput.HasValue)
                return _cachedOutput.Value;

            throw new Exception("You're doing it wrong! Call Update() first.");
        }

        public void Train(double errorContribution)
        {
            Error = errorContribution * _activationFunction.Derivative(GetValue());
            Inputs.Select(i => i.Value).ToList().ForEach(a => a.UpdateWeight(Error));
            _cachedOutput = null;
        }
    }
}
