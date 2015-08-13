using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeneticAlgorithms
{
    class Population
    {
        private readonly int _size;
        private readonly double _mutationRate;
        private readonly int _lengthOfAnswer;
        private static string _target;
        public IList<Genome> Genomes { get; set; }

        public Population(int size, double mutationRate, string target)
        {
            _size = size;
            _mutationRate = mutationRate;
            _lengthOfAnswer = target.Length;
            _target = target;
            Genomes = InitialisePopulation().ToList();
        }

        public void GetNextGeneration()
        {
            Genomes = Rank().ToList();
            var random = new Random(DateTime.Now.Millisecond);
            for (int i = (_size/2)+1; i < _size; i++)
            {
                int random1 = random.Next(_size/2);
                int random2 = random.Next(_size/2);
                Genomes[i] = FitnessEvaluator.CrossoverGenomes(Genomes[random1], Genomes[random2]);
                Genomes[i] = FitnessEvaluator.MutateGenome(Genomes[i], _mutationRate);
            }
        }

        public IOrderedEnumerable<Genome> Rank()
        {
            return Genomes.OrderByDescending(g => FitnessEvaluator.GetFitness(g, _target));
        }

        private IEnumerable<Genome> InitialisePopulation()
        {
            for (int i = 0; i < _size; i++)
            {
                yield return new Genome(_lengthOfAnswer);
            }
        }
    }


}
