using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace NeuralNetwork
{
    public class InputFileReader
    {
        public IList<Tuple<int, IEnumerable<double>>> ReadInputFile()
        {
            IList<Tuple<int, IEnumerable<double>>> csvInputs = new List<Tuple<int, IEnumerable<double>>>();
            using (var reader = new StreamReader(File.OpenRead(@"C:\Users\Pavlos\Desktop\training.csv")))
            {
                while (!reader.EndOfStream)
                {
                    var line = reader.ReadLine();
                    if (line == null || line.StartsWith("label"))
                    {
                        continue;
                    }

                    var raw = line.Split(',');
                    var tuple = new Tuple<int, IEnumerable<double>>(int.Parse(raw[0]),
                        raw.Skip(1).Select(s => double.Parse(s)/255.0d));
                    csvInputs.Add(tuple);
                }
            }
            return csvInputs;
        } 
    }
}