# -*- coding: utf-8 -*-

from collections import defaultdict
import pprint, math, sys, random


def tokenise(sentence):
	sentence = sentence.lower()
	return "".join([" "+char+" " if char in "$£:@!" else char for char in sentence]).split()
	

# from https://www.wikiwand.com/en/Naive_Bayes_spam_filtering

def p_word_given_spam(word):
	return corpus["spam"][word]/corpus["spamcount"]

def p_spam():
	return corpus["spamcount"]/corpus["total"]

def p_ham():
	return corpus["hamcount"]/corpus["total"]

def p_word_given_ham(word):
	return corpus["ham"][word]/corpus["spamcount"]

def spamicity(word):
	try:
		return p_word_given_spam(word) / (p_word_given_spam(word) + p_word_given_ham(word))
	except ZeroDivisionError:
		return 0.5

def hamicity(word):
	try:
		return p_word_given_ham(word) / (p_word_given_spam(word) + p_word_given_ham(word))
	except ZeroDivisionError:
		return 0.5

def spamicity_msg(msg):
	spamicities = [spamicity(word) for word in msg]
	mu = sum([math.log(1.0-p)-math.log(p) for p in spamicities if p != 0.0 and p != 1.0])
	return 1.0/(1.0+math.exp(mu))

def hamicity_msg(msg):
	hamicities = [hamicity(word) for word in msg]
	mu = sum([math.log(1.0-p)-math.log(p) for p in hamicities if p != 0.0 and p != 1.0])
	return 1.0/(1.0+math.exp(mu))


def classify(corpus, sentence):
	sms = tokenise(sentence)

	# pprint.pprint([ (t, spamicity(t), hamicity(t)) for t in sms])
	msg_spamicity = spamicity_msg(sms)
	msg_hamicity = hamicity_msg(sms)

	# print("Spamicity: {}".format(msg_spamicity))
	# print("Hamicity: {}".format(msg_hamicity))
	# print("Verdict: Ham" if msg_hamicity > msg_spamicity else "Verdict: Spam")
	return "ham" if msg_hamicity > msg_spamicity else "spam"


def makeCorpus(lines):
	corpus = {
		"hamcount": 0.0,
		"spamcount": 0.0,
		"ham": defaultdict(lambda: 0.0001),
		"spam": defaultdict(lambda: 0.0001)
	}


	for line in lines:

		line = tokenise(line)
		msgType = line[0]
		line = line[1:]

		if msgType == "spam":
			corpus["spamcount"] += 1
			for word in line:
				corpus["spam"][word] += 1
		
		if msgType == "ham":
			corpus["hamcount"] += 1
			for word in line:
				corpus["ham"][word] += 1

	corpus["total"] = corpus["spamcount"]+corpus["hamcount"]
	return corpus




if __name__ == "__main__":
	# We build our corpus with a naive tokeniser (on whitespace...)
	with open("corpus/SMSSpamCollection.txt") as of:
		lines = of.readlines()
		choices = random.sample(range(len(lines)), 1000)
		testing = []
		training = []
		for i,m in enumerate(lines):
			if i in choices:
				testing.append(m)
			else:
				training.append(m)

	corpus = makeCorpus(training)


	# allwords = set(classifier.ham.keys() + classifier.spam.keys())
	# stats = [(word, spamicity(word), hamicity(word)) for word in allwords]

	# # print("==== Spammiest Words ====")
	# # for w in map( lambda x: str(x[0]), sorted(filter(lambda x: x[1] != 1.0, stats), key=lambda x: -x[1])[:10]):
	# # 	print(w)

	# # print("\n==== Hammiest Words ====")
	# # for w in map( lambda x: str(x[0]), sorted(filter(lambda x: x[2] != 1.0, stats), key=lambda x: -x[2])[:10]):
	# # 	print(w)

	# print("\n==== Analysis of sentence ====")
	# print classify(corpus, sys.argv[1])
	

	print("\n==== Performance ====")
	
	correct = 0.0
	for line in testing:
		label, string = line.split("\t")
		if classify(corpus, string) == label:
			correct += 1

	print("Accuracy: {}".format(correct/len(testing)))



