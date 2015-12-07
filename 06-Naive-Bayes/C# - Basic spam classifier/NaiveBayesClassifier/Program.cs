using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NaiveBayesClassifier
{
    class Program
    {
        static void Main(string[] args)
        {
            var allMessagesWithLabels = File.ReadAllLines("SMSSpamCollection.txt");
            var trainingMessagesWithLabels = allMessagesWithLabels.Take(allMessagesWithLabels.Length/2).ToList();
            var testingMessagesWithLabels = allMessagesWithLabels.Skip(allMessagesWithLabels.Length/2).ToList();
            var trainingMessages = trainingMessagesWithLabels.Select(l => l.Split('\t')).Select(l => new Message { Label = l[0], Content = l[1] }).ToList();
            var testingMessages = testingMessagesWithLabels.Select(l => l.Split('\t')).Select(l => new Message { Label = l[0], Content = l[1] }).ToList();

            ProbabilityCalculator.Messages = trainingMessages;
            ProbabilityCalculator.CalculateGlobalProbabilities();

            var correctCount = 0;
            var incorrectCount = 0;
            foreach (var message in testingMessages)
            {
                ProbabilityCalculator.CalculateSpamicityOfMessage(message);
                var category = message.Spamicity > 0.5 ? "spam" : "ham";
                
                if (category == message.Label)
                {
                    correctCount++;
                }
                else
                {
                    incorrectCount++;
                }

                Console.WriteLine(category + " - " + message.Content);
            }
            Console.WriteLine("Accuracy: " + (double)correctCount / (correctCount + incorrectCount));
            
            Console.ReadLine();
        }
    }
}
