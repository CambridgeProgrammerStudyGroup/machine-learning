#!/usr/bin/python

# Cambridge Programmer Study Group
#
# Ole Schulz-Trieglaff

#from collections import defaultdict
import math
import random
#import operator
#import itertools

def most_common(lst):
    return max(set(lst), key=lst.count)

def distance(p1,p2):
    return math.sqrt(sum([(a-b)*(a-b) for a,b in zip(p1,p2)]))

def find_k_neighbours(trainData,trainLabels,point,k):
    # assign every point to its closest centroid
    distances = [ (distance(point, tpoint),index) for index, tpoint in enumerate(trainData)]
    neighbours = sorted(distances, key=lambda x: x[0])[:k]
    neighbours_idx = [n[1] for n in neighbours]
    pred_labels = [trainLabels[idx] for idx in neighbours_idx]
    return most_common(pred_labels)    
    
def main():

    # set this to keep reproducible results
    #random.seed(42)

    datafile = "iris.data"
    data = []
    labels = []

    with open(datafile,'r') as input:
        for line in input:
            fields = line.split(',')
            label = fields[-1]
            data.append(map(float,fields[:-1]))
            labels.append(label)

    print "Have",len(data)," examples"

    num_folds = 10
    for k in [1,2,3,5,7,9,11]:
    # number of test data points, we keep them separate from the training data
        accs = []
        for f in range(0,num_folds):
            num_test = 40
            testData   = []
            testLabel  = []
    
            trainData  = []
            trainLabel = []
            # generate num_test indizes
            test_idx = set(random.sample(range(len(data)),num_test))
            for idx,item in enumerate(data):
                if idx in test_idx:
                    testData.append(item)
                    testLabel.append(labels[idx])
                else:
                    trainData.append(item)
                    trainLabel.append(labels[idx])
    
            pred_labels = []
            for idx,item in enumerate(testData):
                l = find_k_neighbours(trainData,trainLabel,testData[idx],k)
                pred_labels.append(l)
            
            res = [p for p, t in zip(pred_labels, testLabel) if p == t]
            acc= len(res)/float(len(testLabel))
            accs.append(acc)
        print "at k=",k," mean accuracy=",sum(accs)/float(len(accs))
        


if __name__ == "__main__":
	main()
