using System;
using System.Collections.Generic;
using System.Linq;

namespace GeneticAlgorithms
{
    static class FitnessEvaluator
    {
        private static readonly Random Random = new Random(DateTime.Now.Millisecond);

        public static int GetFitness(Genome genome, string answer)
        {
            return answer.Where((currentChar, i) => currentChar == genome[i]).Count();
        }

        public static IEnumerable<double> NormaliseFitnessValues(IList<int> fitnessValues)
        {
            var totalFitness = fitnessValues.Sum();
            return fitnessValues.Select(i => (double) i/totalFitness);
        }

        public static Genome CrossoverGenomes(Genome genome1, Genome genome2)
        {
            return new Genome(genome1.Phrase.Zip(genome2.Phrase, (g1, g2) => Random.Next(0,1) == 0 ? g1 : g2).ToArray());
        }

        public static Genome MutateGenome(Genome genome, double mutationRate)
        {
            for (int i = 0; i < genome.Phrase.Length; i++)
            {
                genome.Phrase[i] = Random.NextDouble() < mutationRate
                    ? RandomGenomeGenerator.RandomChars(1).First()
                    : genome.Phrase[i];
            }
            return genome;
        }
    }
}
