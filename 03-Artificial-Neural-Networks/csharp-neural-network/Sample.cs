using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    class Sample
    {
        public readonly double[] input;
        public readonly double[] target;

        public Sample(double[] input, double[] target)
        {
            this.input = input;
            this.target = target;
        }

        public static Sample Create(string input, string targets)
        {
            var inValues = GetValues(input);
            var targetValues = GetValues(targets);
            return new Sample(inValues, targetValues);
        }

        static Regex splitRx = new Regex(@"[\s,]+", RegexOptions.Compiled);
        public static double[] GetValues(string text)
        {
            var parts = splitRx.Split(text);
            var values = parts.Select(t => double.Parse(t)).ToArray();
            return values;
        }
    }
}
