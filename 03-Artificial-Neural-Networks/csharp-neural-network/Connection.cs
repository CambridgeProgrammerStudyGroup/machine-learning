using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    class Connection
    {
        readonly Neuron from;
        readonly Neuron to;
        double weight;

        public Connection(Neuron from, Neuron to, double weight)
        {
            from.AddOutboundConnection(this);
            this.from = from;

            to.AddInboundConnection(this);
            this.to = to;

            this.weight = weight;
        }

        public double WeightedValue
        {
            get { return from.Value *  weight; }
        }

        public double WeightedError { get; private set; }

        public void PropagateBack(double learnRate, double error)
        {
            // weightDelta  = learnRate * error * Xij  
            //              = learnRate * toError * from.value?

            WeightedError = error * weight; //

            var weightDelta = learnRate * error * from.Value;
            weight += weightDelta;
         }
    }
}
