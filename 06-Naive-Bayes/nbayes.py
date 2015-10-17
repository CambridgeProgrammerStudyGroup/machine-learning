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
import string

def tokenize(ls):
    # remove some frequent words, convert to lower case and remove
    # punctuation characters
    forbidden = ["and","to", "i","a", "you", "the", "your", "is"]
    ls = [ w.lower() for w in ls ]
    ls = [ w.translate(None, string.punctuation) for w in ls ]
    ls = [ w for w in ls if w not in forbidden ]
    return ls

def main():

    datafile = "corpus/SMSSpamCollection.txt"
    data = []
    with open(datafile) as input:
        for line in input:
            fields = line.split()
            label = fields[0]
            text  = tokenize(fields[1:])
            data.append([label,text])

    print "Have",len(data)," examples"

    # let's keep 1000 examples separate as test data
    num_test = 1000
    test = data[:num_test]
    train = data[(num_test+1):]

    # P(word|label)
    word_llhoods = defaultdict(lambda: defaultdict(lambda: 0.0001))
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

    spam_sum = sum(word_llhoods["spam"].itervalues())
    for w in word_llhoods["spam"]:
        word_llhoods["spam"][w] /= spam_sum
    ham_sum = sum(word_llhoods["ham"].itervalues())
    for w in word_llhoods["ham"]:
        word_llhoods["ham"][w] /= ham_sum

    # debugging
    print "prior=",prior
    maxSpam = sorted(word_llhoods["spam"].iteritems(), key=lambda x: x[1])[0:5]
    print "5 most freqent spam word",maxSpam
    maxHam = sorted(word_llhoods["ham"].iteritems(), key=lambda x: x[1])[0:5]
    print "5 most frequent ham word",maxHam

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
