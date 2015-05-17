using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    class MnistSamples
    {
        public Sample[] Training { get; private set; }
        public Sample[] Testing { get; private set; }

        public MnistSamples()
        {
            var sourceDir = new DirectoryInfo(AppDomain.CurrentDomain.BaseDirectory).Parent.Parent;
            var dataDir = Path.Combine(sourceDir.FullName, "MnistData");
            var dataFile = Path.Combine(dataDir, "Training.csv");

            var allSamples = File.ReadAllLines(dataFile)
                .Skip(1)
                .Select(line => new Sample(
                    Sample.GetValues(line.Substring(2)),
                    GetTargetArray(line.Substring(0, 1))))
                .ToArray();
            var testCount = allSamples.Length / 10;
            Training = new Sample[allSamples.Length - testCount];
            Array.Copy(allSamples, Training, Training.Length);
            Testing = new Sample[testCount];
            if (testCount > 0)
                Array.Copy(allSamples, Training.Length, Testing, 0, Testing.Length);
        }

        static double[] GetTargetArray(string targetText)
        {
            var targetValue = int.Parse(targetText);
            var targetArray = new double[10];
            for (int i = 0; i < 10; i++)
                targetArray[i] = i == targetValue ? 1 : 0;
            return targetArray;
        }

    }
}
