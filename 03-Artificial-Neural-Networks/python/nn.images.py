#!/usr/bin/env python

# TODO:
# * train in batches, implement stochastic gradient descent
# * customize error function
# * proper crossvalidation

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
        print "Learned weights:",nnet
    else:
        act = 'sigmoid'
        nhidden = 50
        ep = 200
        #ep=5

        start = time.time()
        ntrain = 14000 # grab this many training data examples
        Xtrain = pd.read_csv('../training.csv')
        Xtrain_arr = np.array(Xtrain)
        labels_train = Xtrain_arr[:ntrain, 0]
        features_train = Xtrain_arr[:ntrain, 1:].astype(float) / 256
        
        labels_valid = Xtrain_arr[ntrain:, 0]
        features_valid = Xtrain_arr[ntrain:, 1:].astype(float) / 256

        ntrain = features_train.shape[0]
        nvalid = features_valid.shape[0]
        nfeatures = features_valid.shape[1]
        nlabels = 10

        labels_train_expanded = np.zeros((ntrain, nlabels))
        for i in xrange(ntrain):
                labels_train_expanded[i][labels_train[i]] = 1
        end = time.time()

        print "Read %d training examples" % (Xtrain_arr.shape[0])
        print "Features=%d" % nfeatures
        print "Time elapsed=%f" % (end-start)

        start = time.time()
        nnet = nn.NeuralNetwork([nfeatures,nhidden,nlabels],activation=act)
        nnet.fit(features_train, labels_train_expanded, max_iter=ep)
        end = time.time()
        print "Time elapsed ",(end-start)

        preds_valid = nnet.predict_label(features_valid)
        ncorrect = np.sum(preds_valid == labels_valid)
        print "%f percent correct on validation set" % (100.0 * ncorrect / nvalid)
        
        print "Predicting test data"
        Xtest = pd.read_csv('../testing.csv')
        features_test = np.array(Xtest).astype(float) / 256
        preds_test = nnet.predict_label(features_test)
        np.savetxt("predictions.csv",preds_test,fmt='%d',delimiter=",")
        print "All done"

if __name__ == '__main__':
    main()
