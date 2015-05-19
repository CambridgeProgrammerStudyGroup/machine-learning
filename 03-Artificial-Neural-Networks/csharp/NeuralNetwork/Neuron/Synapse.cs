namespace Neuron
{
    public class Synapse : ISynapse
    {
        public IInput Input { get; private set; }
        public double Weight { get; private set; }
        private const double TrainingConstant = 0.01d;

        public Synapse(IInput input)
        {
            Input = input;
            Weight = Util.GetRandomWeight();
        }

        public void UpdateWeight(double error)
        {
            Weight += (TrainingConstant * error * Input.GetValue());
        }
    }
}
