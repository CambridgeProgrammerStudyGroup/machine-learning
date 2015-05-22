using System;

namespace Neuron
{
    public class SigmoidActivation : IActivationFunction
    {
        public double Activate(double input)
        {
            var negInput = -1.0d * input;
            return 1.0d / (1.0d + Math.Exp(negInput));
        }

        public double Derivative(double input)
        {
            var exp = Math.Exp(input);
            var denomPart = 1.0d + exp;
            return exp / (denomPart * denomPart);
        }
    }
}
