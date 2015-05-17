using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    class Mnist
    {
        public static void Train(int trainingCount, double learnRate)
        {
            Console.WriteLine("Starting MNIST training");
            Console.Write("Loading samples... ");
            var mnistSamples = new MnistSamples();
            Console.WriteLine("done");

            Console.Write("Creating network... ");
            var net = new Network(784, new[] { 500, 100 }, 10, learnRate);
            Console.WriteLine("done");

            var training = mnistSamples.Training.Take(trainingCount).ToArray();
            var testing = mnistSamples.Testing.Take(training.Length).ToArray();

            var trainer = new Trainer(net, training, CheckCorrect, testing);
            trainer.TrainUntilDone(true);
        }

        static bool CheckCorrect(double[] target, double[] output)
        {
            return IndexOfMaxValue(target) == IndexOfMaxValue(output);
        }

        static int IndexOfMaxValue(double[] numbers)
        {
            var index = 0;
            return numbers
                .Select(number => new { index = index++, number })
                .OrderBy(t => t.number)
                .Last().index;
        }


    }
}
