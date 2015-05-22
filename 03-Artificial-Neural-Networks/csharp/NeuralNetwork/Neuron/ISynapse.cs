namespace Neuron
{
    public interface ISynapse
    {
        IInput Input { get; }
        double Weight { get; }
        void UpdateWeight(double error);
    }
}
