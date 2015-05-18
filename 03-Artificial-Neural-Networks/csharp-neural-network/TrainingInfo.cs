using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    class TrainingInfo
    {
        public TrainingInfo(double learnRate)
        {
            this.LearnRate = learnRate;
        }

        public double LearnRate { get; private set; }
    }
}
