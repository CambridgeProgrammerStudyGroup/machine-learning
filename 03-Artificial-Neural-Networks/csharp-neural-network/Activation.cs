using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    abstract class Activation
    {
        public abstract double CalcValue(double x);
        public abstract double CalcDerivative(double x);
        public abstract double GetRandomWeight();

        class SigmoidActivation : Activation
        {

            public override double CalcValue(double x)
            {
                return 1 / (1 + Math.Exp(-x));
            }

            public override double CalcDerivative(double x)
            {
                return x * (1 - x);
            }

            static Random rnd = new Random();
            public override double GetRandomWeight()
            {
                return (rnd.NextDouble() * 2) - 1;
            }
        }

        readonly static Activation sigmoid = new SigmoidActivation();
        public static Activation Sigmoid { get { return sigmoid; } }
    }
}
