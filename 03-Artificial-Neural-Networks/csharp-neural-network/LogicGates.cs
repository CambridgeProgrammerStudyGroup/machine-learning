using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    class LogicGates
    {

        public static void Train()
        {
            var learnRate = 0.2;

            Console.WriteLine("Training Not:");
            Console.WriteLine("=============");
            new Trainer(new Network(1, new int[0], 1, learnRate), notCases, CheckCorrect)
                .TrainUntilDone();

            Console.WriteLine("Training Or:");
            Console.WriteLine("============");
            new Trainer(Create21Network(), orCases, CheckCorrect)
                .TrainUntilDone();

            Console.WriteLine("Training And:");
            Console.WriteLine("=============");
            new Trainer(Create21Network(), andCases, CheckCorrect)
                .TrainUntilDone();

            Console.WriteLine("Training Nand:");
            Console.WriteLine("==============");
            new Trainer(Create21Network(), nandCases, CheckCorrect)
                .TrainUntilDone();

            Console.WriteLine("Training Xor:");
            Console.WriteLine("=============");
            new Trainer(new Network(2, new []{2} , 1, learnRate), xorCases, CheckCorrect)
                .TrainUntilDone();
        }

        static void HL() { Console.WriteLine(new string('=', 0)); }

        public static void TrainNot()
        {
            var net = new Network(1, new int[0], 1, 0.5);
            var trainer = new Trainer(net, notCases, CheckCorrect, notCases);
            trainer.TrainUntilDone();
        }

        static Network Create21Network()
        {
            return new Network(2, new int[0], 1, 0.5);
        }


        static bool CheckCorrect(double[] target, double[] output)
        {
            var t = target[0];
            var o = output[0];
            return (t == 1 && o > 0.7) || (t == 0 && o < 0.3);
        }


        static Sample[] notCases = new[]{
                Sample.Create("0", "1"),
                Sample.Create("1", "0"),
            };

        static Sample[] orCases = new[]{
                Sample.Create("0 0", "0"),
                Sample.Create("0 1", "1"),
                Sample.Create("1 0", "1"),
                Sample.Create("1 1", "1"),
            };

        static Sample[] andCases = new[]{
                Sample.Create("0 0", "0"),
                Sample.Create("0 1", "0"),
                Sample.Create("1 0", "0"),
                Sample.Create("1 1", "1"),
            };

        static Sample[] nandCases = new[]{
                Sample.Create("0 0", "1"),
                Sample.Create("0 1", "1"),
                Sample.Create("1 0", "1"),
                Sample.Create("1 1", "0"),
            };

        static Sample[] xorCases = new[]{
                Sample.Create("0 0", "0"),
                Sample.Create("0 1", "1"),
                Sample.Create("1 0", "1"),
                Sample.Create("1 1", "0"),
            };


    }
}
