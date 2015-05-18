using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharp_Neural_Network
{
    class Network
    {
        readonly InputLayer inputLayer;
        readonly HiddenLayer[] hiddenLayers;
        readonly OutputLayer outputLayer;
        readonly Activation activation;

        public int InputSize { get; private set; }
        public int[] HiddenSizes { get; private set; }
        public int OutputSize { get; private set; }

        public Network(int inputSize, int[] hiddenSizes, int outputSize, double learnRate)
            : this(Activation.Sigmoid, new TrainingInfo(learnRate), inputSize, hiddenSizes, outputSize)
        { }

        public Network(Activation activation, TrainingInfo trainInfo, int inputSize, int[] hiddenSizes, int outputSize)
        {
            this.InputSize = inputSize;
            this.HiddenSizes = hiddenSizes;
            this.OutputSize = outputSize;

            this.activation = activation;
            this.inputLayer = new InputLayer(inputSize);
            this.hiddenLayers = hiddenSizes
                .Select(size => new HiddenLayer(activation, trainInfo, size))
                .ToArray();
            this.outputLayer = new OutputLayer(activation, trainInfo, outputSize);
            ConnectLayers();
        }

        void ConnectLayers()
        {
            if (!hiddenLayers.Any())
            {
                ConnectAdjacentLayers(inputLayer, outputLayer);
            }
            else
            {
                var lastHiddenIndex = hiddenLayers.Length - 1;
                ConnectAdjacentLayers(inputLayer, hiddenLayers[0]);
                for (int i = 0; i < lastHiddenIndex; i++)
                    ConnectAdjacentLayers(hiddenLayers[i], hiddenLayers[i + 1]);
                ConnectAdjacentLayers(hiddenLayers[lastHiddenIndex], outputLayer);
            }
        }

        void ConnectAdjacentLayers(Layer fromLayer, Layer toLayer)
        {
            foreach (var fromNeuron in fromLayer.Neurons)
                foreach (var toNeuron in toLayer.Neurons)
                    new Connection(fromNeuron, toNeuron, activation.GetRandomWeight());
        }

        public double[] FeedForward(double[] input)
        {
            inputLayer.SetInputValues(input);
            foreach (var hiddenLayer in hiddenLayers)
                hiddenLayer.FeedForward();
            outputLayer.FeedForward();
            return outputLayer.Values;
        }

        public void PropagateBack(double[] target)
        {
            outputLayer.SetTargetValues(target);
            outputLayer.PropagateBack();
            foreach (var hiddenLayer in hiddenLayers.Reverse())
                hiddenLayer.PropagateBack();
        }

    }
}
