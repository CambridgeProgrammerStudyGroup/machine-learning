using System;

namespace Neuron
{
    public static class Util
    {
        private static readonly Random Random = new Random(DateTime.Now.Millisecond);

        public static double GetRandomWeight()
        {
            return (Random.NextDouble() * 2) - 1;
        }
    }
}