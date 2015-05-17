using System;
using System.Collections.Generic;
using System.Linq;

namespace Neuron
{
    public class Neuron : INeuron
    {
        public double Error { get; private set; }
        public List<ISynapse> Inputs { get; private set; }

        private readonly IActivationFunction _activationFunction;

        public Neuron(IActivationFunction activationFunction)
        {
            _activationFunction = activationFunction;
            Inputs = new List<ISynapse>();
        }

        public void RegisterInput(IInput input)
        {
            Inputs.Add(new Synapse(input));
        }

        public double GetValue()
        {
            var output = Inputs.Sum(i => i.GetValue());
            return _activationFunction.Activate(output);
        }

        public void Train(double error)
        {
            Error = error;
            Inputs.ForEach(a => a.UpdateWeight(Error));
        }
    }
}
