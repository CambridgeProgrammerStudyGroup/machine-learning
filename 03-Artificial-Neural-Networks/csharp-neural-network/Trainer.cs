using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    delegate bool CheckCorrect(double[] target, double[] output);

    class Trainer
    {
        readonly Network net;
        readonly Sample[] cases;
        readonly int caseCount;
        readonly CheckCorrect checkCorrect;
        readonly Sample[] testCases;

        public Trainer(Network net, Sample[] cases, 
            CheckCorrect checkCorrect, Sample[] testCases = null)
        {
            this.net = net;
            this.cases = cases;
            this.caseCount = cases.Length;

            if (caseCount == 0)
                throw new Exception("Don't have any samples to train with!");
            var sampleSample = cases[0];
            if (net.InputSize != sampleSample.input.Length)
                throw new Exception(string.Format(
                    "Net input size: {0}, doesn't match sample input size: {1}.",
                    net.InputSize, sampleSample.input.Length));
            if (net.OutputSize != sampleSample.target.Length)
                throw new Exception(string.Format(
                    "Net output size: {0}, doesn't match sample target size: {1}.",
                    net.OutputSize, sampleSample.target.Length));

            this.checkCorrect = checkCorrect;
            this.testCases = testCases ?? new Sample[0];
        }

        public long EpochSize { get { return caseCount; } }
        public long EpochCount { get; private set; }
        public long CurrentSampleCount { get; private set; }
        public long TotalSampleCount { get; private set; }

        public void TrainAllCases()
        {
            foreach (var sample in cases)
            {
                net.FeedForward(sample.input);
                net.PropagateBack(sample.target);
                CurrentSampleCount++;
                TotalSampleCount++;
            }
            EpochCount++;
            CurrentSampleCount = 0;
        }

        public void TrainUntilDone(bool showEpochStats = false)
        {
            var start = DateTime.UtcNow;
            Wr("Got {0:n0} training cases and {1:n0} test cases", caseCount, testCases.Length);
            var trainingAccuracy = MeasureAccuracy(cases);
            var testAccuracy = MeasureAccuracy(testCases);
            WriteAccuracy(trainingAccuracy, "Training");
            WriteAccuracy(testAccuracy, "    Test");
            Wr("Starting first epoch...");
            Wr("================================");
            while (trainingAccuracy.Item1 != trainingAccuracy.Item2)
            {
                var epochStart = DateTime.UtcNow;
                ShuffleCases();
                var trainingStart = DateTime.UtcNow;
                TrainAllCases();
                var trainingEnd = DateTime.UtcNow;
                trainingAccuracy = MeasureAccuracy(cases);
                if (showEpochStats || trainingAccuracy.Item1 == trainingAccuracy.Item2)
                {
                    Wr("Epoch {0} complete:", EpochCount);
                    testAccuracy = MeasureAccuracy(testCases);
                    WriteAccuracy(trainingAccuracy, "Training");
                    WriteAccuracy(testAccuracy, "    Test");
                    Wr("Training time: {0:mm\\:ss}", (trainingEnd - trainingStart));
                    Wr("   Epoch time: {0:mm\\:ss}", (DateTime.UtcNow - epochStart));
                    Wr("     Run time: {0:mm\\:ss}", (DateTime.UtcNow - start));
                    Wr("================================");
                }
            }
            Wr("All done.");
            Wr("");
        }

        static Random rnd = new Random();
        public void ShuffleCases()
        {
            for (int i = 0; i < caseCount; i++)
            {
                int r = (int)(rnd.NextDouble() * caseCount);
                var rTemp = cases[r];
                cases[r] = cases[i];
                cases[i] = rTemp;
            }
        }

        void WriteAccuracy(Tuple<int, int> t, string sampleName)
        {
            Wr("{0}: {1:n2}% correct ({2:n0}/{3:n0})",
                sampleName, GetPercent(t.Item1, t.Item2), t.Item1, t.Item2);
        }

        float GetPercent(int d, int n) { return 100 * (float)d / (float)n; }

        Tuple<int, int> MeasureAccuracy(Sample[] samples)
        {
            var correctCount = (
                from sample in samples
                let output = net.FeedForward(sample.input)
                where checkCorrect(sample.target, output)
                select 1)
                .Count();
            return Tuple.Create(correctCount, samples.Length);
        }

        void Wr(string text, params object[] args)
        {
            Console.WriteLine(text, args);
        }

    }
}
