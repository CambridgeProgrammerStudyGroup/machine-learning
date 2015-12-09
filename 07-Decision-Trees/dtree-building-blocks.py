#import sys

import csv
from math import log
from collections import defaultdict
import random

# http://nbviewer.ipython.org/gist/kevindavenport/c4b377f9c0626c9dd856
class decisionnode:
    def __init__(self,col=-1,value=None,results=None,tb=None,fb=None):
        self.col=col # column index of criteria being tested
        self.value=value # vlaue necessary to get a true result
        self.results=results # dict of results for a branch, None for everything except endpoints
        self.tb=tb # true decision nodes 
        self.fb=fb # false decision nodes

# This returns a dictionary with the target values keys
# with counts as values. Needed to calculate the entropy below 
def uniquecounts(data):
    results = defaultdict(lambda: 0)
    for d in data:
        # this assumes that your target (the variable which you want to predict)i
        # is at position zero in each row. 
        target = d[0]
        results[target]+=1
    return results 

# Entropy - our criterion used to create nodes in the decision tree
# https://en.wikipedia.org/wiki/Entropy_%28information_theory%29
def entropy(data):
    log2=lambda x:log(x)/log(2)  
    results=uniquecounts(data)
    # Now calculate the entropy
    entropy=0.0
    for r in results.keys():
        # current probability of class
        p=float(results[r])/len(data) 
        entropy=entropy-p*log2(p)
    return entropy



def main():
    data = []
    with open('titanic3.clean.csv','r') as csvfile:
        rdr = csv.reader(csvfile, delimiter=',',quotechar='"')
        hdr = next(rdr, None)
        print "header=",hdr
        data = list(rdr)    

    #print data

    res = uniquecounts(data)
    print "res=",res
    print entropy(data)
    
    random.seed(42)

    print "Have",len(data)," examples"

    # number of test data points, we keep them separate from the training data
    num_test = 500
    testData   = []
    trainData  = []
    # generate num_test indizes
    test_idx = set(random.sample(range(len(data)),num_test))
    for idx,item in enumerate(data):
        if idx in test_idx:
            testData.append(item)
        else:
            trainData.append(item)    

if __name__ == "__main__": 
    main()