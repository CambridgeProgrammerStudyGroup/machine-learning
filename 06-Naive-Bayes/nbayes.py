#!/usr/bin/python

# Cambridge Programmer Study Group
#
# Naive bayes implementation of a spam filter
# Keeps 1000 text messages as test data set
# Write incorrectly classified text messages
# to a file for further inspection
#
# Ole Schulz-Trieglaff

from collections import defaultdict
import math
import random
import codecs

def tokenize(ls):
    # remove some frequent words, convert to lower case and remove
    # punctuation characters
    forbidden = ["and","to", "i","a", "you", "the", "your", "is"]
    ls = [ w.lower() for w in ls ]
    ls = [ w for w in ls if w not in forbidden ]
    ls = [ w for w in ls if len(w) > 0 ]
    # This seemed like a good idea but it doesn't change results much
    ls = [ "PHONE" if w.isdigit() else w for w in ls]
    return ls
    
def main():

    # set this to keep reproducible results
    random.seed(42)

    datafile = "corpus/SMSSpamCollection.txt"
    data = []

    with codecs.open(datafile, encoding='utf-8') as input:
        for line in input:
            fields = line.split()
            label = fields[0]
            text  = tokenize(fields[1:])
            data.append([label,text])

    print "Have",len(data)," examples"

    # number of test data points, we keep them separate from the training data
    num_test = 1000
    test = []
    train = []
    # generate num_test indizes
    test_idx = set(random.sample(range(len(data)),num_test))
    for idx,item in enumerate(data):
        if idx in test_idx:
            test.append(item)
        else:
            train.append(item)
    #test = data[:num_test]
    #train = data[(num_test+1):]

    # P(word|label)
    fudge = 10**(-8) # probably for non-existing words
    word_llhoods = defaultdict(lambda: defaultdict(lambda: fudge))
    # P(label)
    prior = defaultdict(float)
    num_train = len(train)
    for d in train:
        label = d[0]
        text = d[1]
        prior[label]+=1
        for t in text:
            word_llhoods[label][t]+=1

    # normalize to get probabilities
    for k in prior:
        prior[k] /= num_train

    # debugging
    print "prior=",prior
    maxSpam = sorted(word_llhoods["spam"].iteritems(), key=lambda x: x[1],reverse=True)[0:5]
    print "5 most freqent spam word",maxSpam
    maxHam = sorted(word_llhoods["ham"].iteritems(), key=lambda x: x[1],reverse=True)[0:5]
    #maxHam = word_llhoods["ham"].iteritems()[0:5]
    print "5 most frequent ham word",maxHam

    spam_sum = sum(word_llhoods["spam"].itervalues())
    for w in word_llhoods["spam"]:
        word_llhoods["spam"][w] /= spam_sum
    ham_sum = sum(word_llhoods["ham"].itervalues())
    for w in word_llhoods["ham"]:
        word_llhoods["ham"][w] /= ham_sum

    # read test data
    correct = 0
    mistakesFile = "mistakes" # write incorrectly classified messages to a file
    with open(mistakesFile,"w") as mistakesOut:
        for d in test:
            label = d[0]
            text  = d[1]
            llhood_spam = 0.0
            llhood_ham = 0.0
            for w in text:
                #print w," ",math.log10(word_llhoods["ham"][w])," ", math.log10(word_llhoods["spam"][w])
                llhood_spam += math.log10(word_llhoods["spam"][w])
                llhood_ham += math.log10(word_llhoods["ham"][w])

            llhood_spam += math.log10(prior["spam"])
            llhood_ham += math.log10(prior["ham"])

            guess = "spam" if llhood_spam > llhood_ham else "ham"
            if label == guess:
                correct+=1
            else:
                print >> mistakesOut, text
                print >> mistakesOut, "llhood_spam=",llhood_spam
                print >> mistakesOut, "llhood_ham=",llhood_ham
                print >> mistakesOut, "true label=",label

    print "correct={} out of {} test cases".format(correct,num_test)

if __name__ == "__main__":
	main()
