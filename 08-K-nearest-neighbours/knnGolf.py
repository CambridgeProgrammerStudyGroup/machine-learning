#!/usr/bin/env python3
import os, csv, math, random
from collections import defaultdict
from functools import reduce

def partition(items, key):
	"""
	Partition a sequence of items according to key function.
	Will work when sequence is not ordered. 
	Key function must return a comparabel value.
	"""
	bykey = defaultdict(list)
	for item in items:
		bykey[key(item)].append(item)
	return [v for k,v in bykey.items()]

first  = lambda a:a[0]
second = lambda a:a[1]
identity = lambda a:a

def knn_classify(candidate, items, k, categoriser, distance):
	"""The knn algorithm in essence"""
	distances = [(distance(candidate,item),item) for item in items]
	topk = [ item for dist, item in sorted(distances, key=first)[:k]]
	categories = partition(topk, lambda item: categoriser(item))
	return max([(len(items), categoriser(items[0])) for items in categories], key=first)[1]

def KNN(items, k, categoriser, distance):
	"""Utility function to create a classifier"""
	return lambda x: knn_classify(x, items, k, categoriser, distance)

def accuracy(classifier, testing, categoriser):
	"""Returns the accuracy rate of a classifier give the 
	testing data and a categoriser function"""
	return sum([1 if categoriser(item) == classifier(item) else 0 for item in testing ])/len(testing)

# on dataset...
with open("iris.data") as datafile:
    reader = csv.reader(datafile)
    testing, training = partition([flower for flower in reader], lambda x: random.random() > 0.2)
    k = 3
    categoriser = lambda x: x[4]
    distance = lambda a,b: math.sqrt(sum([(float(x)-float(y))**2 for x,y in list(zip(a,b))[:4]]))
    
    for k in range(2,10):
    	accuracy_percent = accuracy(KNN(training, k, categoriser, distance), testing, categoriser)
    	print("k={} -> {:>7.2%}".format(k, accuracy_percent))





