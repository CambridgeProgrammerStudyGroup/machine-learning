#import sys

import csv
import random

from math import log
from collections import defaultdict 
from operator import itemgetter
from itertools import groupby

continuous_features = ["fare", "age", "sibsp", "parch"]

# Nice but probably inefficient way to compute the entropy
def entropy(data):
    survived_idx = len(data[0])-1
    # need to sort the data, otherwise group by won't work
    data.sort(key=itemgetter(survived_idx))    
    entropy = 0.0
    num = float(len(data))
    for outcome,iterList in groupby(data, itemgetter(survived_idx)):  
        p = sum(1 for _ in iterList)/num        
        entropy -= p*log(p,2)
    return entropy

def splitData(data,column,value):
    res = []    
    for dt in data:
        if dt[column] == value:
            res.append(dt)
    return res            

def splitDataContinuous(data,column):
    up = []
    lo = []
    mean = 0.0
    for dt in data:
        mean += data[column]
    mean /= float(len(data))
    for dt in data:
        if dt[column] > value:
            up.append(dt)
        else:
            lo.append(dt)
    return (up,lo) 
            
        
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

def buildTree(data,header,excluded_attributes,offset=""):
    num_cols = len(data[0])-1
    best_gain = 0.0
    best_attribute = 0 
    best_values = set()

    if len(excluded_attributes)==num_cols:
        print offset,"No attributes left, done"
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
            
    
    print offset,"best gain is",best_gain,"for",header[best_attribute]
    #print "best_values=",best_values
    if best_gain == 0.0:
        #print "Done"
        #result = [row[len(row)-1] for row in data]
        #print "Result =",result
        print offset,"Gain is zero. Done?"
        return

    excluded_attributes.append(header[best_attribute])
    offset += " "
    for bv in best_values:
        bv_subset = splitData(data,best_attribute,bv)
        #print offset,"Split at",bv,"with",round(len(bv_subset)/float(len(data)),4) 
        buildTree(bv_subset,header,excluded_attributes,offset)
        
        
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