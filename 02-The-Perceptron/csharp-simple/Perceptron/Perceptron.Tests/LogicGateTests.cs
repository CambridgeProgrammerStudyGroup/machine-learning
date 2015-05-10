using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using NUnit.Framework;

namespace Perceptron.Tests
{
    [TestFixture]
    public class LogicGateTests
    {
        private const string NandTruthTable = @"
        -1,-1, 1;
         1,-1, 1;
        -1, 1, 1;
         1, 1,-1;";

        private const string AndTruthTable = @"
        -1,-1,-1;
         1,-1,-1;
        -1, 1,-1;
         1, 1, 1;";

        private const string OrTruthTable = @"
        -1,-1,-1;
         1,-1, 1;
        -1, 1, 1;
         1, 1, 1;";

        private readonly Func<double, double> _stepActivation =
        output => output > 0 ? 1 : -1;

        [TestCase(NandTruthTable)]
        [TestCase(AndTruthTable)]
        [TestCase(OrTruthTable)]
        public void Train_WithActivationFunction_ShouldPredictLogicGateOutput(string truthTableString)
        {
            var biasWeight = 0.0d;
            var weights = new[] { 0.0d, 0.0d };
            var perceptron = new Perceptron(weights, biasWeight, _stepActivation);
            IList<double[]> truthTable = ParseTable(truthTableString);

            for (var i = 0; i < 10; i++)    // train on the table 10 times through
            {
                PerformActionOnTruthTable(truthTable, perceptron.Train);
            }

            // assert the predition of the perceptron
            PerformActionOnTruthTable(truthTable, (inputs, expected) => Assert.AreEqual(expected, perceptron.GetOutput(inputs)));
        }

        private void PerformActionOnTruthTable(IEnumerable<double[]> table, Action<IEnumerable<double>, double> action)
        {
            foreach (double[] row in table)
            {
                var inputs = row.Take(2);
                var expected = row[2];
                action(inputs, expected);
            }
        }

        private IList<double[]> ParseTable(string truthTable)
        {
            var rows = truthTable.Split(';').Where(r => !string.IsNullOrWhiteSpace(r)).Select(r => r.Trim());
            return rows.Select(r => r.Split(',').Select(item => item.Trim()).Select(double.Parse).ToArray()).ToList();
        }
    }
}