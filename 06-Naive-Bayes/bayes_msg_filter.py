
from __future__ import division
from collections import defaultdict
import pickle
import re
import sys
import math

count  = 0

 
def rr():
    return 0.000000000000000000000001

def dd():
    return defaultdict(rr) 
  
def train_line(line,data_dict,prior):
    (label,text) = line.split("\t")
    # Split the text by the punctuation words
    text = re.split(r"[^\w]|[\s]",text)
    prior[label.lower()] += 1
    for word in text:
        if word == "":
            continue
        data_dict[label.lower()][word.lower()] += 1
     

def normalize_prob(data_dict,prior):
    ham_word_count = sum(data_dict["ham"].itervalues())
    spam_word_count = sum(data_dict["spam"].itervalues())
    for word in data_dict["spam"]:
        data_dict["spam"][word] /= spam_word_count
    for word in data_dict["ham"]:
        data_dict["ham"][word] /= ham_word_count
    
    prior["ham"] /= (prior["ham"] + prior["spam"])
    prior["spam"] /= (prior["ham"] + prior["spam"])
    
def classify_msg(msg,data_dict,prior):
    spam_prob= 0.0
    ham_prob  = 0.0
    
    words = re.split("[^\w]|[/s]",msg)
    #print words
    for word in words: 
        word=word.lower()
        if word == "":
            continue
        ham_prob += math.log10(data_dict["ham"][word])
        spam_prob += math.log10(data_dict["spam"][word])
        
    ham_prob += math.log10(prior["ham"])
    spam_prob += math.log10(prior["spam"])
    
        #print "Word :{}--Spam Prob->{} Ham Prob -> {}".format(word,spam_prob,ham_prob)
    if spam_prob > ham_prob:
        guess = "spam"
    else:
        guess = "ham"

    return guess



def train(filename):
    data_dict = defaultdict(dd)
    prior = defaultdict(rr)
    with open(filename,'r') as inp:
        for line in inp:
            train_line(line,data_dict,prior)
    
    normalize_prob()
    #print ham_words
    with open("m_brain", "wb") as fout:
        pickle.dump((data_dict,prior), fout)
    

def test(filename):
    with open("m_brain","r") as fin:
        (data_dict,prior) = pickle.load(fin)
    
    
    count  = 0
    success = 0
    with open(filename, "r") as inp:
        for line in inp:
            count +=1
            label,text = line.split("\t")
            guess = classify_msg(text,data_dict,prior)
            if label.lower() == guess:
                success += 1

                
    print "Success rate = {}/{}".format(success,count)            
    print "Success rate %= {}".format(success/count * 100)                      

def train_and_test():
    data_dict = defaultdict(dd)
    prior = defaultdict(rr)
   
    with open("corpus/SMSSpamCollection.txt") as inp:
        lines = inp.readlines()
        train_len = 5550
        train_data = lines[:train_len]
        test_data = lines[train_len:]
        success = 0
        for line in train_data:
            train_line(line,data_dict,prior)
        normalize_prob(data_dict,prior)
        
        for line in test_data:
            label,text = line.split("\t")
            guess = classify_msg(text,data_dict,prior)
            if label.lower() == guess:
                success += 1

    print "Success rate = {}/{}".format(success,len(test_data))            
    print "Success rate %= {}".format(success/len(test_data) * 100)            

def main():
    if len(sys.argv) == 3:
        if sys.argv[1] == "-train" or sys.argv[1] == "--t":
            train(sys.argv[2])
        elif sys.argv[1] == "-run" or sys.argv[1] == "--r":
            test(sys.argv[2])
    else:
        train_and_test()


    
if __name__ == "__main__":
    main()
