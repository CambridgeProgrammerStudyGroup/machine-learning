import random
from collections import defaultdict



class NeuralNetwork(object):

  def train(self, inputs, expected):
    raise NotImplementedError("This method is not yet implemented.")

  def feedforward(self, inputs):
    raise NotImplementedError("This method is not yet implemented.")


class StepPerceptronWithBias(NeuralNetwork):
    def __init__(self, n, c=0.1):
        self.n = n
        self.rate = c
        self.weights = [random.random()*1.0 - 0.5 for i in range(n)]
        self.weights.append(random.random()*1.0 - 0.5)

    def _activate(self, s):
        return 1 if s > 0 else -1

    def feedforward(self, inputs):
        s = sum([float(inputs[i]) * self.weights[i] for i in range(self.n)])
        return self._activate(s + self.weights[-1])

    def train(self, inputs, desired):
        guess = self.feedforward(inputs)
        error = desired - guess
        for i in range(self.n):
            self.weights[i] += self.rate * error * inputs[i]
        self.weights[-1] += self.rate * error

    def update_weights(aggregate_error, inputs):
      deltas = [i*aggregate_error*rate for i in inputs]
      self.weights = [w+dw for d, dw in zip(self.weights, deltas)]

      return [i*aggregate_error * w for i,w in zip(inputs, self.weights)]

    def __str__(self):
        return str(self.weights)

TRUE, FALSE = 1, -1

TRUTH_TABLES = {
    'NOT': [
        ([FALSE], TRUE),
        ([TRUE], FALSE),
    ],

    'AND': [
        ([FALSE, FALSE], FALSE),
        ([FALSE, TRUE ], FALSE),
        ([TRUE , FALSE], FALSE),
        ([TRUE , TRUE ], TRUE ),
    ],

    'OR': [
        ([FALSE, FALSE], FALSE),
        ([FALSE, TRUE ], TRUE ),
        ([TRUE , FALSE], TRUE ),
        ([TRUE , TRUE ], TRUE ),
    ],
    'NOR':[
        ([FALSE, FALSE], TRUE),
        ([FALSE, TRUE ], FALSE ),
        ([TRUE , FALSE], FALSE ),
        ([TRUE , TRUE ], FALSE ),
    ],

    'NAND': [
        ([FALSE, FALSE], TRUE ),
        ([FALSE, TRUE ], TRUE ),
        ([TRUE , FALSE], TRUE ),
        ([TRUE , TRUE ], FALSE ),
    ],

    'XOR': [
        ([FALSE, FALSE], FALSE ),
        ([FALSE, TRUE ], TRUE ),
        ([TRUE , FALSE], TRUE ),
        ([TRUE , TRUE ], FALSE ),
    ]
}



def test (p, data):
  results = [p.feedforward(inputs) for inputs, expected in data]
  #print( results )
  tests = [1 if result == d[1] else 0 for result, d in zip(results, data)]
  return sum(tests) / (1.0 * len(tests))


def train (p, data, iterations):
  for i in range(iterations):
    sample = random.choice(data)
    p.train(sample[0], sample[1])
  return p


def run_test(per, data):
  print("")
  # display and test the perceptron without training
  print("before training {} success rate {}".format(per, test(per, data)))

  train(per, data, 100)

  print("after training {} success rate {}".format(per, test(per, data)))


class XORMLP(NeuralNetwork):
  def __init__(self):
    self.input_layer = [PerceptronWithBias(2), PerceptronWithBias(2)]
    self.output_layer = [PerceptronWithBias(2)]

  def feedforward(self, inputs):
    o1 = i1.feedforward(inputs)
    o2 = i2.feedforward(inputs)
    return o.feedforward([o1,o2])

  def train(self, inputs, target):
    hidden_values = [neuron.feedforward(i) for neuron, i in zip(input_layer, inputs)]
    outputs = [neuron.feedforward(hidden_values) for neuron in self.output_layer]
    difference = target - output
    output_error = output * difference

    hidden_errors = o.update_weights(output_error, hidden_values)

    [neuron.update_weights(e,i) for neuron, e, i in zip(input_layer, hidden_errors, inputs)]






run_test(StepPerceptronWithBias(2, c=0.02), TRUTH_TABLES["OR"])
run_test(StepPerceptronWithBias(2, c=0.02), TRUTH_TABLES["AND"])
run_test(StepPerceptronWithBias(2, c=0.02), TRUTH_TABLES["NAND"])
run_test(StepPerceptronWithBias(2, c=0.02), TRUTH_TABLES["NOR"])
run_test(StepPerceptronWithBias(2, c=0.02), TRUTH_TABLES["XOR"])



