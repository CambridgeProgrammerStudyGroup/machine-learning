#!/usr/bin/python

import numpy as np

# Implementation based on Ex. 1 in https://www.coursera.org/course/ml
def gradDescent(x, y, theta, alpha, m, nIter):
    loss = 0
    for i in range(0, nIter):
        h = np.dot(x, theta)
        loss = (h-y)
        gradient = np.dot(x.transpose(), loss) / m
        theta = theta - alpha * gradient

    cost = np.sum(loss ** 2) / (2 * m)
    print("After %d iterations, cost is %f" % (nIter, cost))
    return theta

y = np.loadtxt('../data/ex2y.dat')
x = np.loadtxt('../data/ex2x.dat')

on = np.ones(np.shape(x))
# append ones for offset
x  = np.column_stack((on,x))

m,n     = np.shape(x)
numIter = 10000

# starting values
theta = np.array([5,5])
# learning rate
alpha = 0.05

# gradient descent
theta = gradDescent(x, y, theta, alpha, m, numIter)
print "theta=",theta

# analytical solution
t1 = np.linalg.inv(np.dot(x.transpose(),x))
theta2 = np.dot(np.dot(t1,x.transpose()),y)
print "theta2=",theta2

