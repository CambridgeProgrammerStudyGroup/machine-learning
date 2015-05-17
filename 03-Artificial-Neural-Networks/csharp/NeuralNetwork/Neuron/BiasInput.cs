namespace Neuron
{
    public class BiasInput : IInput
    {
        private readonly double _biasValue;

        public BiasInput(double biasValue)
        {
            _biasValue = biasValue;
        }

        public double GetValue()
        {
            return _biasValue;
        }
    }
}