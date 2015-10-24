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
import operator
import itertools

def tokenize(ls):
    # remove some frequent words, convert to lower case and remove
    # punctuation characters
    forbidden = ["and","to", "i","a", "you", "the", "your", "is"]
    ls = [ w.lower() for w in ls ]
    ls = [ w for w in ls if w not in forbidden ]
    ls = [ w for w in ls if len(w) > 0 ]
    # This seemed like a good idea but it doesn't change results much
    #ls = [ "PHONE" if w.isdigit() else w for w in ls]
    return ls
    
# Implements a naive bayes classifier
class NaiveBayesClassifier:
    
    # fudge is the probability we assign
    # to unseen words, it should be small
    def __init__(self, fudge = 10**(-8)):
        # P(word|label)
        self.word_llhoods = defaultdict(lambda: defaultdict(lambda: fudge))
        # P(label)        
        self.prior = defaultdict(float)
    
    def fit(self,data,label):
        assert(len(data)==len(label))
        num_train = len(data)
        for msg,lbl in itertools.izip(data,label):
            self.prior[lbl]+=1
            for t in msg:
                self.word_llhoods[lbl][t]+=1

        # normalize to get probabilities
        for k in self.prior:
            self.prior[k] /= num_train

        # normalize likelihoods P(w|class)            
        for cls in self.word_llhoods:
            cls_sum = sum(self.word_llhoods[cls].itervalues())
            for w in self.word_llhoods[cls]:
                self.word_llhoods[cls][w] /= cls_sum

    def predict(self,test):
             
        predicted_labels = []
        
        for msg in test:
            label_guess = defaultdict(float)
            for w in msg:
                # we sum the likelihoods since we are in log space
                for cls in self.word_llhoods:
                    label_guess[cls] += math.log10(self.word_llhoods[cls][w])
                # add prior for each class label                    
                for cls in self.prior:
                    label_guess[cls] += self.prior[cls]
            #print "predicted_label=",label_guess
            max_label = max(label_guess.iteritems(), key=operator.itemgetter(1))[0]
            #print "max_label=",max_label
  
            predicted_labels.append(max_label)
        
        assert(len(predicted_labels)==len(test))
        return predicted_labels

    # since prior is defaultdict this return 0.0 
    # for an unknown class
    def get_prior(self,label):
        return self.prior[label]
        
    def get_word_likelihood(self,word,label):
        return self.word_llhoods[label][word]
    
def main():

    # set this to keep reproducible results
    random.seed(42)

    datafile = "corpus/SMSSpamCollection.txt"
    data = []
    labels = []

    with codecs.open(datafile, encoding='utf-8') as input:
        for line in input:
            fields = line.split()
            label = fields[0]
            text  = tokenize(fields[1:])
            data.append(text)
            labels.append(label)

    print "Have",len(data)," examples"

    # number of test data points, we keep them separate from the training data
    num_test = 1000
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
   
    nbc = NaiveBayesClassifier()
    nbc.fit(trainData,trainLabel)
    predicted_labels = nbc.predict(testData)

    correct = 0
    mistakesFile = "mistakes" # write incorrectly classified messages to a file
    with open(mistakesFile,"w") as mistakesOut:
        for guess,msg,truth in itertools.izip(predicted_labels,testData,testLabel):
            if guess == truth:
                correct += 1
            else:
                print >> mistakesOut, msg
                print >> mistakesOut, "truth=",truth
                print >> mistakesOut, "guess=",guess
               
    print "accuracy on test data: {:.2f}".format(correct/float(num_test)*100)

if __name__ == "__main__":
	main()
