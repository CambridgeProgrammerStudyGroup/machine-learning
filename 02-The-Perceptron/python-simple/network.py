from perceptron import PerceptronWithBias


class Network:
    def __init__(self, n, c=0.01):
        self.n = n
        self.c = c
        
        self.p = [PerceptronWithBias(n, c) for _ in range(3)]
    
    def hidden_layer(self, inputs):
        return [self.p[i].predict(inputs) for i in range(2)]
    
    def predict(self, inputs):
        return self.p[2].predict(self.hidden_layer(inputs))
    
    def train(self, inputs, desired):
        guess = self.predict(inputs)
        error = desired - guess
        
        r0, r1 = self.hidden_layer(inputs)
        self.p[0].correction(inputs, error)
        self.p[1].correction(inputs, error)
        self.p[2].correction([r0, r1], error)

