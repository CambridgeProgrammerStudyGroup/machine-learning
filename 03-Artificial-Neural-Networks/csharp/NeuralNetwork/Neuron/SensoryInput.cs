namespace Neuron
{
    public class SensoryInput : IInput
    {
        private double _value;

        public void UpdateValue(double value)
        {
            _value = value;
        }

        public double GetValue()
        {
            return _value;
        }
    }
}
