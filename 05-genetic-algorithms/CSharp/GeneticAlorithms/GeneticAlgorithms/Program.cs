using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Timers;

namespace GeneticAlgorithms
{
    class Program
    {
        static void Main(string[] args)
        {
			var target = "to be or not to be that is the question whether tis nobler in " +
			             "the mind to suffer the strings and arrows of outrageous fortune or to " +
						 "take up arms against a sea of troubles and by opposing end them";
            var population = new Population(1000, 0.01, target);

            var stopwatch = new Stopwatch();
            stopwatch.Start();

            Genome first;
            int index = 0;
            do
            {
                population.GetNextGeneration();
				first = population.Genomes.First();
				Console.WriteLine("Generation {0}, fitness = {1}: {2}", index++, first.Fitness, JoinPhrase(first.Phrase));
            } while (JoinPhrase(first.Phrase) != target);

            stopwatch.Stop();
            Console.WriteLine(stopwatch.Elapsed.ToString());
        }

        static string JoinPhrase(IEnumerable<char> phrase)
        {
            var stringBuilder = new StringBuilder();
            phrase.ToList().ForEach(c => stringBuilder.Append(c));
            return stringBuilder.ToString();
        }
    }
}
