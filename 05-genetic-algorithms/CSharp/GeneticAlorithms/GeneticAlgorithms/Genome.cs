using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeneticAlgorithms
{
    class Genome
    {
        public char[] Phrase { get; set; }
        public int Fitness { get; set; }
		public double NormalisedFitness { get; set; }

        public Genome(int lengthOfPhrase)
        {
            Phrase = RandomGenomeGenerator.RandomChars(lengthOfPhrase).ToArray();
        }

        public Genome(char[] chars)
        {
            Phrase = chars;
        }

        public char this[int i]
        {
            get { return Phrase[i]; }
            set { Phrase[i] = value; }
        }
    }
}
