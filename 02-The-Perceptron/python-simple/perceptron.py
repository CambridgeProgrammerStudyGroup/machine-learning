"""
http://natureofcode.com/book/chapter-10-neural-networks/
"""
from random import random


class PerceptronWithBias:
    def __init__(self, n, c=0.01):
        self.n = n
        self.c = c
        self.weights = [0.] * n
        """
        for i in range(n):
            self.weights.append((random() * 2.) - 1.)
        """
        self.weights.append(0.)
    
    def activate(self, s):
        return 1 if s > 0 else -1
    
    def predict(self, inputs):
        s = sum([float(inputs[i]) * self.weights[i] for i in range(self.n)])
        return self.activate(s + self.weights[-1])
    
    def correction(self, inputs, error):
        for i in range(self.n):
            self.weights[i] += self.c * error * inputs[i]
        self.weights[-1] += self.c * error
    
    def train(self, inputs, desired):
        guess = self.predict(inputs)
        error = desired - guess
        self.correction(inputs, error)
    
    def __str__(self):
        return str(self.weights)


class PerceptronWithoutBias:
    def __init__(self, n, c=0.01):
        self.n = n
        self.c = c
        self.weights = []
        for i in range(n):
            self.weights.append((random() * 2.) - 1.)
    
    def activate(self, s):
        return 1 if s > 0 else -1
    
    def predict(self, inputs):
        s = sum([float(inputs[i]) * self.weights[i] for i in range(self.n)])
        return self.activate(s)
    
    def correction(self, inputs, error):
        for i in range(self.n):
            self.weights[i] += self.c * error * inputs[i]
    
    def train(self, inputs, desired):
        guess = self.predict(inputs)
        error = desired - guess
        self.correction(inputs, error)
    
    def __str__(self):
        return str(self.weights)

