import random
from collections import defaultdict
from perceptron import PerceptronWithBias
from network import Network

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

class Result:
    def __init__(self):
        self.perceptron = None
        self.success = False
        self.success_rate = 0.0
    
    def __str__(self):
        return "%s %.3f" % (self.success, self.success_rate)


def test(Perceptron, n_training_samples, verbose=True):
    truth_tables = defaultdict(Result)
    
    for name, data in TRUTH_TABLES.iteritems():
        num_good, num_bad = 0, 0
        truth_tables[name].success = True
        
        if truth_tables[name].perceptron is None:
            truth_tables[name].perceptron = Perceptron(len(data[0][0]))
        
        for _ in range(n_training_samples):
            x, y = random.choice(data)
            truth_tables[name].perceptron.train(x, y)
        
        for inputs, expected in data:
            if truth_tables[name].perceptron.predict(inputs) == expected:
                num_good += 1
            else:
                truth_tables[name].success = False
                num_bad += 1
        
        truth_tables[name].success_rate = float(num_good) / float(num_good + num_bad)
    
    if verbose:
        for name, result in truth_tables.iteritems():
            print "%s: %s" % (name, result)
    
    return truth_tables


#test(Network, 1000)

random.seed(3)
print "PerceptronWithBias"
x, y0, y1 = range(45), [], []
for n in x:
    results = test(PerceptronWithBias, n, False)
    y0.append(results['OR'].success_rate)
    y1.append(results['AND'].success_rate)

import matplotlib.pyplot as plt
plt.plot(x, y0)
plt.plot(x, y1)
plt.show()
