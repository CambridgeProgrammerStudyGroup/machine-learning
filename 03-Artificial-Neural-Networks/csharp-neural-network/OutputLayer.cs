using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    class OutputLayer : Layer
    {
        readonly OutputNeuron[] neurons;

        public OutputLayer(Activation activation, TrainingInfo trainInfo, int size)
        {
            var neurons = new OutputNeuron[size];
            for (int i = 0; i < size; i++)
                neurons[i] = new OutputNeuron(activation, trainInfo);
            this.neurons = neurons;
        }

        public override Neuron[] Neurons
        {
            get { return neurons; }
        }

        public double[] Values
        {
            get
            {
                return neurons.Select(n => n.Value).ToArray();
            }
        }

        public void SetTargetValues(double[] targetValues)
        {
            for (int i = 0; i < targetValues.Length; i++)
                neurons[i].TargetValue = targetValues[i];
        }

        class OutputNeuron : Neuron
        {
            public OutputNeuron(Activation activation, TrainingInfo trainInfo)
                : base(activation, trainInfo) { }

            public double TargetValue { private get; set; }

            protected override double CalcValueDelta()
            {
                return TargetValue - Value;
            }
        }

    }

}
