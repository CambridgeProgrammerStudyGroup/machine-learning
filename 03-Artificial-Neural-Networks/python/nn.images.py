#!/usr/bin/env python

# TODO:
# * train in batches
# * customize error function
# * proper command line parsing
# * look at misclassified images

import nn
import numpy as np
import pandas as pd
import time
import sys


def buildXorTrainingData(n):
    X = np.array([[0,0],[0,1],[1,0],[1,1]])
    XX =np.repeat(X,n,axis=0)
    np.random.shuffle(XX)
    # convert booleans to int
    y = np.array([ [int(np.logical_xor(x[0],x[1]))] for x in XX])
    return XX, y

def main():

    if (len(sys.argv) > 1 and sys.argv[1] == 'test'):
        
        print "Running XOR test"
        
        numTrain  = 500
        numIter   = 50
        nlabels   = 1
        nhidden   = 5

        (Xtrain, ytrain) = buildXorTrainingData(numTrain)
        #print "Xtrain=",Xtrain
        #print "ytrain=",ytrain        
        nrows = Xtrain.shape[0]
        nfeatures = Xtrain.shape[1]

        nnet = nn.NeuralNetwork([nfeatures, nhidden, nlabels])
        nnet.fit(Xtrain, ytrain, max_iter=numIter)

        # real-valued output, need to round
        pred = np.round(nnet.predict(Xtrain))
        ncorrect = np.sum(pred == ytrain)
        print "%f%% percent correct on training set" % (100.0 * ncorrect / nrows)
    else:
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
        preds = nnet.predict_label(features)
        ncorrect = np.sum(preds == labels)
        print "%f%% percent correct on training set" % (100.0 * ncorrect / nrows)


if __name__ == '__main__':
    main()
