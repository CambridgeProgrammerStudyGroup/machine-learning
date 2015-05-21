using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Neuron;

namespace NeuralNetwork
{
    public class Program
    {
        private static readonly IActivationFunction Activation = new SigmoidActivation();
        public static void Main()
        {
            const int numInHiddenLayer = 500;
            const int numOfOutputs = 10;
            const double normalisation = 255.0d;

            var inputFileReader = new InputFileReader();
            IList<Tuple<int, IEnumerable<double>>> csvInputs = inputFileReader.ReadTrainingInputFile(@"C:\Users\Pavlos\Desktop\training.csv", normalisation);

            int validationFraction = csvInputs.Count / 10; // use all but ten percent for training, hold the rest back for validation
            var trainingInputs = csvInputs.Skip(validationFraction).ToList(); 
            var validationInputs = csvInputs.Take(validationFraction).ToList();
            
            // create inputs and the three layers
            List<SensoryInput> sensoryInputs = trainingInputs[0].Item2.Select(i => new SensoryInput()).ToList();
            List<INeuron> inputLayer = CreateLayer(sensoryInputs.Count, sensoryInputs.Cast<IInput>().ToList());
            List<INeuron> hiddenLayer = CreateLayer(numInHiddenLayer, inputLayer.Cast<IInput>().ToList());
            List<INeuron> outputLayer = CreateLayer(numOfOutputs, hiddenLayer.Cast<IInput>().ToList());

            double previousGlobalError = double.MaxValue;
            double globalErrorDelta;

            // training:
            do
            {
                foreach (var specimen in trainingInputs)
                {
                    UpdateNetwork(specimen.Item2.ToList(), sensoryInputs, inputLayer, hiddenLayer, outputLayer);

                    // train output layer
                    for (int k = 0; k < outputLayer.Count; k++)
                    {
                        double desired = k == specimen.Item1 ? 1.0d : 0.0d;
                        double output = outputLayer[k].GetValue();
                        double error = desired - output;
                        outputLayer[k].Train(error);
                    }
                    // train hidden layer, then train input layer
                    BackPropagate(hiddenLayer, outputLayer);
                    BackPropagate(inputLayer, hiddenLayer);
                }

                // calculate global error using the validation set that was excluded from training:
                double globalError = 0.0d;
                foreach (var specimen in validationInputs)
                {
                    UpdateNetwork(specimen.Item2.ToList(), sensoryInputs, inputLayer, hiddenLayer, outputLayer);

                    for (int i = 0; i < outputLayer.Count; i++)
                    {
                        double desired = i == specimen.Item1 ? 1.0d : 0.0d;
                        globalError += Math.Abs(desired - outputLayer[i].GetValue());
                    }
                }

                globalErrorDelta = Math.Abs(previousGlobalError - globalError);
                previousGlobalError = globalError;
                Console.WriteLine("Global error: {0}", globalError);

            } while (globalErrorDelta > 1000.0d); // train until global error begins to level off

            // Run on real testing data and write output to console:
            var testingInputs = inputFileReader.ReadTestingInputFile(@"C:\Users\Pavlos\Desktop\testing.csv", normalisation);
            foreach (var specimen in testingInputs)
            {
                UpdateNetwork(specimen.ToList(), sensoryInputs, inputLayer, hiddenLayer, outputLayer);
                int mostLikelyAnswer = GetMostLikelyAnswer(outputLayer);

                Console.WriteLine(mostLikelyAnswer);
            }
        }

        private static void UpdateNetwork(IList<double> specimenInputs, List<SensoryInput> sensoryInputs, 
            List<INeuron> inputLayer, List<INeuron> hiddenLayer, List<INeuron> outputLayer)
        {
            for (int i = 0; i < specimenInputs.Count; i++)
                sensoryInputs[i].UpdateValue(specimenInputs[i]);

            UpdateLayer(inputLayer);
            UpdateLayer(hiddenLayer);
            UpdateLayer(outputLayer);
        }

        private static void UpdateLayer(IEnumerable<INeuron> layer)
        {
            // Output can be computed in parallel as neurons in a layer are independent of each other
            Parallel.ForEach(layer, neuron => neuron.Update());
        }

        private static void BackPropagate(IEnumerable<INeuron> leftLayer, List<INeuron> rightLayer)
        {
            // backpropagation can also be done in parallel for a particular layer
            Parallel.ForEach(leftLayer, leftNeuron =>
            {
                // the compute the error contribution of this neuron, by looking at the 
                // error of the neurons it is connected to on the right
                var errorContribution =
                    rightLayer.Sum(rightNeuron => rightNeuron.Inputs[leftNeuron].Weight*rightNeuron.Error);
                leftNeuron.Train(errorContribution);
            });
        }

        private static List<INeuron> CreateLayer(int layerSize, List<IInput> inputs)
        {
            var layerBias = new BiasInput(1.0d);
            var layer = new List<INeuron>();
            for (var i = 0; i < layerSize; i++)
            {
                var neuron = new Neuron.Neuron(Activation);
                neuron.RegisterInput(layerBias);
                inputs.ForEach(neuron.RegisterInput);
                layer.Add(neuron);
            }
            return layer;
        }

        private static int GetMostLikelyAnswer(IList<INeuron> outputLayer)
        {
            double max = 0.0d;
            int answer = 0;
            for (int i = 0; i < outputLayer.Count; i++)
            {
                double value = outputLayer[i].GetValue();
                if (value > max)
                {
                    max = value;
                    answer = i;
                }
            }
            return answer;
        }
    }
}
