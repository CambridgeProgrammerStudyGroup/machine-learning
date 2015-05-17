using System.Collections.Generic;

namespace Neuron
{
    public class Output : INeuron
    {
        public double Error { get; private set; }
        public List<ISynapse> Inputs { get; private set; }

        public Output()
        {
            
        }

        public double GetValue()
        {
            throw new System.NotImplementedException();
        }

        public void Train(double error)
        {
            throw new System.NotImplementedException();
        }

        public void RegisterInput(IInput input)
        {
            throw new System.NotImplementedException();
        }
    }
}