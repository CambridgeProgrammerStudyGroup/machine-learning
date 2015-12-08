#!/usr/bin/env python2.7


import sys, os
import re
import filtering as filt
import numpy as np
import re
from collections import defaultdict

class PhonePresent:
	pass

PHONE_NUMBER = PhonePresent()

def create_dictionary(filename):
	features_freq = defaultdict(float)

	with open(filename, 'r') as fp:
		for line in fp:
			words = re.split('\W+', line)
			sentence = words[1:]
			features = featurise(sentence)
			# Start from the second word (ignore the ham/spam marker).
			for f in features:
				features_freq[f] += 1
	return features_freq


def featurise(temp_sentence):
	sentence = []
	for word in temp_sentence:
		if re.match("^[0-9]{5}[0-9]*$", word):
			sentence.append(PHONE_NUMBER)
		else:
			sentence.append(word)
	bigrams = zip(sentence, sentence[1:])
	trigrams = zip(sentence, sentence[1:], sentence[2:])

	return sentence + bigrams #+ trigrams

def calc_spamprob(message, good, bad):
	# Split the line in words.
	features = featurise(re.split('\W+', message))
	probv = np.array([ filt.spamicity_of_given_word(good,bad,f,4827,747) for f in features])
	return probv.prod() / (probv.prod()+(1-probv).prod())

def is_spam(message, good, bad):
	spamprob = calc_spamprob(message, good, bad)

	return spamprob > 0.5

def main(argv):
	good = create_dictionary('halfnonspam.txt')
	bad = create_dictionary('halfspam.txt')
	count_correct = 0.0
	count_incorrect = 0.0

	with open('2ndhalfspam.txt', 'r') as fp:
		for line in fp:
			if is_spam(line[len("spam "):], good, bad):
				count_correct+=1
			else:
				count_incorrect+=1

	with open('2ndhalfnonspam.txt', 'r') as fp:
		for line in fp:
			if not is_spam(line[len("ham "):], good, bad):
				count_correct+=1
			else:
				count_incorrect+=1

	accuracy = (count_correct/ (count_correct + count_incorrect))
	print("Accuracy is: "+ str(accuracy))

	# for word in good:
	# 	probabilty[word] = filt.populate_third_dict(good, bad, word, 4827, 747)
	#
	# for word in bad:
	# 	probabilty[word] = filt.populate_third_dict(good, bad, word, 4827, 747)


if __name__ == '__main__':
	main(sys.argv[1:])
