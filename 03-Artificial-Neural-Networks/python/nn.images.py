#!/usr/bin/env python

# TODO:
# * train in batches or use all training examples
# * customize error function
# * proper command line parsing

import nn
import numpy as np
import pandas as pd
import time

def main():

    act = 'sigmoid'
    nhidden = 50
    ep = 200
    #ep=5

    start = time.time()
    X = pd.read_csv('../training.csv')
    labels = np.array(X)[:, 0]
    features = np.array(X)[:, 1:].astype(float) / 256

    nrows = features.shape[0]
    nfeatures = features.shape[1]
    nlabels = 10

    labels_expanded = np.zeros((nrows, nlabels))
    for i in xrange(nrows):
            labels_expanded[i][labels[i]] = 1
    end = time.time()

    print "Read ",nrows," training examples"
    print "Features",nfeatures
    print "Time elapsed ",(end-start)

    start = time.time()
    nnet = nn.NeuralNetwork([nfeatures,nhidden,nlabels],activation=act)
    nnet.fit(features, labels_expanded, max_iter=ep)
    end = time.time()
    print "Time elapsed ",(end-start)

    """
    num_examples = 20
    print "Showing ",num_examples," examples"
    for i in range(0,num_examples):
        print (whichIsOne(y[i]),whichIsOne(nn.predict(X[i])))
    """
    preds = nn.predict_label(features)
    ncorrect = np.sum(preds == labels)
    print "%f%% percent correct on training set" % (100.0 * ncorrect / nrows)


if __name__ == '__main__':
    main()