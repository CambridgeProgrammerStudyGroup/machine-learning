using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeneticAlgorithms
{
    public static class RandomGenomeGenerator
    {
        private static readonly Random Random = new Random(DateTime.Now.Millisecond);

        public static IEnumerable<char> RandomChars(int lengthOfPhrase)
        {
            
            for (int i = 0; i < lengthOfPhrase; i++)
            {
                var randomInt = Random.Next(0, 27);
                yield return (randomInt == 26 ? ' ' : (char)('a' + randomInt));
            }
        }
    }
}
