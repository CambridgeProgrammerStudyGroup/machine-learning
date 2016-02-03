using System;
using System.IO;
using System.Linq;
using System.Collections.Generic;

namespace KNN
{
	class MainClass
	{
		const int k = 3;
		const int testingFraction = 4;

		public static void Main(string[] args) {
			var specimens = File.ReadAllLines("iris.data")
								.Select((l, i) => new { Data=ParseLine(l), Index = i})
								.ToList();

			var trainingSet = specimens.Where(x => x.Index % testingFraction != 0).Select(x => x.Data).ToList(); 
			var testingSet = specimens.Where(x => x.Index % testingFraction == 0).Select(x => x.Data).ToList(); 

			var correct = 0;
			foreach (var item in testingSet) {
				var predicted = Majority(FindKNearest(item, trainingSet));
				Console.WriteLine("{0} read from file was predicted to be {1}", item.Class, predicted);
				if (predicted == item.Class)
					correct++;
			}
			Console.WriteLine("Accuracy: {0:0.00} %", ((double)correct * 100.0d / (double)testingSet.Count));
		}

		static Specimen ParseLine(string line) {
			var data = line.Split(',');
			return new Specimen {
				SepalLength = double.Parse(data[0]),
				SepalWidth = double.Parse(data[1]),
				PetalLength = double.Parse(data[2]),
				PetalWidth = double.Parse(data[3]),
				Class = data[4]
			};
		}

		static double Distance (Specimen a, Specimen b) {
			var d1 = Math.Pow(a.SepalLength - b.SepalLength, 2);
			var d2 = Math.Pow(a.SepalWidth - b.SepalWidth, 2);
			var d3 = Math.Pow(a.PetalLength - b.PetalLength, 2);
			var d4 = Math.Pow(a.PetalWidth - b.PetalWidth, 2);

			return d1 + d2 + d3 + d4;
		}

		static List<Specimen> FindKNearest (Specimen input, IEnumerable<Specimen> trainingSet) {
			return trainingSet.Select(t => new { Item = t, Dist = Distance(input, t)})
							  .OrderBy(d => d.Dist)
							  .Take(k)
							  .Select(x => x.Item)
							  .ToList();
		}

		static string Majority (List<Specimen> nearest) {
			var majorityGroup = nearest.GroupBy(x => x.Class)
									   .OrderByDescending(g => g.Count())
									   .First();
			var majorityClass = majorityGroup
									   .First()
									   .Class;
			return majorityClass;
		}
	}
}
