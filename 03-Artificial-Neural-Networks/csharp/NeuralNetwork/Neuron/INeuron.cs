using System.Collections.Generic;

namespace Neuron
{
    public interface INeuron : IInput
    {
        double Error { get; }
        List<ISynapse> Inputs { get; }
        void Train(double error);
        void RegisterInput(IInput input);
    }
}
