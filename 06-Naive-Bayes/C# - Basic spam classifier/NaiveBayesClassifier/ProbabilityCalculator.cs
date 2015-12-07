using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NaiveBayesClassifier
{
    public class Message
    {
        public string Label { get; set; }
        public string Content { get; set; }
        public double Spamicity { get; set; }
    }

    public static class ProbabilityCalculator
    {
        public static List<Message> Messages { get; set; }
        public static double ProbSpam { get; set; }
        public static double ProbHam { get; set; }
        public static int SpamMessageCount { get; set; }
        public static int HamMessageCount { get; set; }      

        public static void CalculateGlobalProbabilities()
        {
            ProbSpam = (double)Messages.Count(m => m.Label == "spam") / Messages.Count;
            ProbHam = (double)Messages.Count(m => m.Label == "ham") / Messages.Count;
            //SpamMessageCount = 
        }

        public static void CalculateSpamicityOfMessage(Message message)
        {
            var words = message.Content.Split(' ').ToList();
            var wordProbs = words.Select(GetSpamicityOfWord).ToList();

            var eta = wordProbs.Sum(wordProb => Math.Log(1 - wordProb) - Math.Log(wordProb));
            
            message.Spamicity = 1.0 / (1 + Math.Exp(eta));
        }

        public static double GetSpamicityOfWord(string word)
        {
            var probWordSpam = (double) Messages.Where(m => m.Label == "spam").Count(m => m.Content.Contains(word)) / Messages.Count(m => m.Label == "spam");
            var probWordHam = (double) Messages.Where(m => m.Label == "ham").Count(m => m.Content.Contains(word)) / Messages.Count(m => m.Label == "ham");
            
            return (probWordSpam * ProbSpam) / (probWordSpam * ProbSpam + probWordHam * ProbHam);
        }
    }
}
