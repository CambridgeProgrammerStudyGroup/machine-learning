using System.Collections.Generic;

namespace Perceptron
{
    public interface IPerceptron
    {
        double GetOutput(IEnumerable<double> inputs);
        void Train(IEnumerable<double> inputs, double desired);
    }
}