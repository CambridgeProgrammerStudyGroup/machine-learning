#import sys

import csv
from math import log
from collections import defaultdict
import random

# http://nbviewer.ipython.org/gist/kevindavenport/c4b377f9c0626c9dd856
class decisionnode:
    def __init__(self,col=-1,results=None,children=[]):
        self.col=col # column index of criteria being tested
        self.results=results # dict of results for a branch, None for everything except endpoints
        self.children = children

# This returns a dictionary with the target values keys
# with counts as values. Needed to calculate the entropy below 
def uniquecounts(data):
    results = defaultdict(lambda: 0)
    for passengers in data:
        # this assumes that your target (the variable which you want to predict)i
        # is at position zero in each row. 
        survived = passengers[len(passengers)-1]
        results[survived]+=1
    return results 

# Entropy - our criterion used to create nodes in the decision tree
# https://en.wikipedia.org/wiki/Entropy_%28information_theory%29
def entropy(data):
    cnt=uniquecounts(data)
    # Now calculate the entropy
    entropy=0.0
    for r in cnt.keys():
        # current probability of class
        p=float(cnt[r])/len(data) 
        entropy=entropy-p*log(p,2)
    return entropy

def splitData(data,column,value):
    res = []    
    for dt in data:
        if dt[column] == value:
            res.append(dt)
    return res            

def informationGain(data,column):
    values = set()
    values_freq = defaultdict(int)    
    num_rows = len(data)
    for dt in data:
        values.add(dt[column])
        values_freq[dt[column]]+=1        
    
    gain = entropy(data)
    for v in values:
        pv = values_freq[v]/float(num_rows)
        subset=splitData(data,column,v)
        gain -= pv*entropy(subset)

    return (gain,values)

def buildTree(data,header,excluded_attributes,offset=''):
    num_cols = len(data[0])-1
    best_gain = 0.0
    best_attribute = 0 
    best_values = set()

    if len(excluded_attributes)==num_cols:
        return
        
    print offset,"entropy=",entropy(data)
    
    for i in range(0,num_cols):
        if header[i] in excluded_attributes:
            continue
        (g,vals) = informationGain(data,i)
        #print "gain for",header[i],"=",g    
        if g>best_gain:
            best_gain=g
            best_attribute=i
            best_values = vals
            
    
    #print "best gain is",best_gain,"for",header[best_attribute]
    #print "best_values=",best_values
    if best_gain == 0.0:
        #print "Done"
        #result = [row[len(row)-1] for row in data]
        #print "Result =",result
        return

    excluded_attributes.append(header[best_attribute])
    offset += ' '
    for bv in best_values:
        bv_subset = splitData(data,best_attribute,bv)
        print offset,"Split at",bv,"with",round(len(bv_subset)/float(len(data)),4) 
        buildTree(bv_subset,header,excluded_attributes)
        
        
def main():
    random.seed(42)
    data = []
    hdr = "NA"    
    with open('titanic3.clean.reordered.new.csv','r') as csvfile:
        rdr = csv.reader(csvfile, delimiter=',',quotechar='"')
        hdr=next(rdr, None)
        data = list(rdr)    

    #print data[0]
    
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

    print "Entropy of data",entropy(trainData)
    print "Have",len(data)," examples"
    #print informationGain(trainData,2)
        
    excluded_attributes = ["name","ticket","fare","cabin"]  
    buildTree(trainData,hdr,excluded_attributes)

if __name__ == "__main__": 
    main()