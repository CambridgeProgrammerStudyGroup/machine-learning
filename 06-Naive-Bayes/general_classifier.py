#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# Generalised Bayesian classifier with arbitrary labels
# Brice Fernandes

from collections import defaultdict
import pprint, math, sys, random, enum, re

class Classifier(object):
	"General Bayesian classifier. Can handle any hashable features for classification"

	def __init__(self):
		self.featurecounts = defaultdict(lambda: defaultdict(lambda: 0.001))
		self.labelcounts = defaultdict(lambda: 0.001)
		self.labels = set()

	@property
	def _total_count(self):
		return float(sum(self.labelcounts.values()))

	def p_label(self, label):
		return float(self.labelcounts[label])/self._total_count

	def p_feature_given_label(self, feature, label):
		return self.featurecounts[label][feature] / self.labelcounts[label]

	def p_feature(self,feature):
		feature_total = 0.0
		for label in self.labels:
			feature_total += self.featurecounts[label][feature]
		return feature_total/self._total_count

	# P(A|B) = P(B|A) P(A) / P(B)
	def p_label_given_feature(self, label, feature):
		return self.p_feature_given_label(feature, label) * self.p_label(label) / self.p_feature(feature)	

	def train(self, label, features):
		self.labels.add(label)
		self.labelcounts[label] += len(features)
		for feature in features:
			self.featurecounts[label][feature] += 1

	def prob_label(self, label, features):
		probabilities = [self.p_label_given_feature(label,feature) for feature in features]
		mu = sum([math.log(1.0-p)-math.log(p) for p in probabilities])
		return 1.0/(1.0+math.exp(mu))

	def classify(self, features):
		by_label = [(label, self.prob_label(label, features)) for label in self.labels]	
		bestlabel = ("", 0.0)
		for label, prob in by_label:
			if prob > bestlabel[1]:
				bestlabel = (label, prob)
		return bestlabel[0]

	def accuracy(self, labels, features):
		test_data = list(zip(labels, features))
		correct = [self.classify(features) == label for label, features in test_data].count(True)
		return float(correct) / len(test_data)


class Feature(enum.Enum):
	Currency = 0
	PhoneNumber = 1
	AllCaps = 2

recognisers = [
	(Feature.Currency, 		lambda w: "£" in w or "$" in w),
	(Feature.PhoneNumber, 	lambda w: re.match("^[0-9]{5}[0-9]*$", w)),
	(Feature.AllCaps, 		lambda w: re.match("^[!?-A-Z]+$", w))
]

def featurise(msg):
	features = []

	words = msg.split()

	for word in words:
		for feature, recogniser in recognisers:
			if recogniser(word):
				features.append(feature)

	return features + words

def sample(data, fraction=0.2):
	choices = random.sample(range(len(data)), int(fraction*len(data)))
	testing = []
	training = []
	for i,m in enumerate(lines):
		if i in choices:
			testing.append(m)
		else:
			training.append(m)
	return training, testing



		



if __name__ == "__main__":
	with open("corpus/SMSSpamCollection.txt") as of:
		lines = of.readlines()

	training, testing = sample(lines)

	classifier = Classifier()

	for label, line in [line.split("\t") for line in training]:
		classifier.train(label, featurise(line))

	print("==== Performance ====")
	labels, features = list(zip( *[ (label, featurise(line)) for label, line in [line.split("\t") for line in testing]]))
	print("Accuracy: {}".format(classifier.accuracy(labels, features)))



	

