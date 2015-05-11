using System;
using System.Collections.Generic;

namespace Neuron
{
    public interface INeuron
    {
        double GetOutput(IEnumerable<double> inputs);
        void Train(IEnumerable<double> inputs, Func<double> errorFunction);
    }
}