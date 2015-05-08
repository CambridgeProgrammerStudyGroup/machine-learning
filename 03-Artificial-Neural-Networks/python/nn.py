
import numpy as np
import cost as cst

# Neural network with backpropagation. Inspired by
# http://www.bogotobogo.com/python/files/NeuralNetworks/nn3.py

#np.random.seed(42)

def sigmoid(x):
    return 1.0/(1.0 + np.exp(-x))
def sigmoid_prime(x):
    return sigmoid(x)*(1.0-sigmoid(x))

def tanh(x):
    return np.tanh(x)
def tanh_prime(x):
    return 1.0 - x**2

class NeuralNetwork:

    def __init__(self, layout, activation='tanh', cst=cst.CrossEntropyCost):

        self.cost = cst

        if activation == 'sigmoid':
            self.activation = sigmoid
            self.activation_prime = sigmoid_prime
        elif activation == 'tanh':
            self.activation = tanh
            self.activation_prime = tanh_prime

        # init weights, sampled from standard normal distribution
        # add bias for each layer
        nw=len(layout)-1
        self.weights = [ [] for i in range(nw) ]
        for i in range(0, nw-1):
            self.weights[i] = np.random.randn(layout[i]+1,layout[i+1]+1)
        # output layer
        self.weights[i+1] = np.random.randn(layout[nw-1]+1,layout[nw])

    def backprop(self,x,y,alpha):
        a = [x]
        nweights = len(self.weights)
        for l in range(nweights):
            dot_value = np.dot(a[l], self.weights[l])
            activation = self.activation(dot_value)
            a.append(activation)

        # output layer, quadratic cost function
        error = y - a[-1]
        deltas = [error * self.activation_prime(a[-1])]

        for l in range(len(a)-2, 0, -1):
            deltas.insert(0,deltas[-1].dot(self.weights[l].T)*self.activation_prime(a[l]))

        # backpropagation
        # 1. Multiply its output delta and input activation
        #    to get the gradient of the weight.
        # 2. Subtract a ratio (percentage) of the gradient from the weight.
        for i in range(len(self.weights)):
            layer = np.atleast_2d(a[i])
            delta = np.atleast_2d(deltas[i])
            self.weights[i] += alpha * layer.T.dot(delta)

    def fit(self, X, y, alpha=0.2, max_iter=100):

        nrows = X.shape[0]
        ones = np.atleast_2d(np.ones(nrows))
        X = np.concatenate((ones.T, X), axis=1)

        for k in range(max_iter):
            if k % 10 == 0:
                print 'iteration:', k, ' out of ', max_iter

            # shuffle data
            p = np.random.permutation(nrows)
            _X = X[p]
            _y = y[p]

            for i in range(nrows):
                self.backprop(_X[i],_y[i], alpha)

    # computes a real-valued output
    def predict(self, X):
        nrows = X.shape[0]
        A = [ np.zeros(x.shape) for x in X ]
        for n in range(nrows):
            # add bias
            a = np.concatenate((np.ones(1).T, np.array(X[n])), axis=1)
            for l in range(0, len(self.weights)):
                a = self.activation(np.dot(a, self.weights[l]))
            A[n] = a
        return A
    
    # computes a integer output (label)
    def predict_label(self, X):
        preds = self.predict(X)
        return np.argmax(preds, axis=1)
    
    def __str__(self):
        return str(self.weights)
