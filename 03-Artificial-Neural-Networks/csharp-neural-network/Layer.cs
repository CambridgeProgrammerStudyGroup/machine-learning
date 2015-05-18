using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    abstract class Layer
    {
        public abstract Neuron[] Neurons { get; }

        public void FeedForward()
        {
            foreach (var neuron in Neurons)
                neuron.FeedForward();
        }

        public void PropagateBack()
        {
            foreach (var neuron in Neurons)
                neuron.PropagateBack();
        }

    }
}
