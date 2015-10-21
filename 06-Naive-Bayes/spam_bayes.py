#!/usr/bin/python3

import argparse
import collections
import pickle
import re
import sys

def classify_words(text, word_dict):
    # Remove trailing \n
    text = text[:-1]

    words = re.split(r"(?: |,|\.|-|–|:|;|&|=|\+|#|\(|\)|\||<|…|\^|\[|\])+", text)

    for word in words:
        # Remove misc symbols
        word = re.sub(r"^(?:'|\"|“)+", r"", word)
        word = re.sub(r"(\w)'$", r"\1", word)
        word = word.lower()

        if word == "":
            continue

        word_dict[word] += 1

def train(fname):
    ham_words = collections.defaultdict(int)
    spam_words = collections.defaultdict(int)

    with open(fname) as fin:
        for line in fin:
            line_parts = line.split("\t")
            if line_parts[0] == "ham":
                classify_words(line_parts[1], ham_words)
            elif line_parts[0] == "spam":
                classify_words(line_parts[1], spam_words)
            else:
                raise RuntimeError("Unkwnown line: {}".format(line))

    with open("brain", "wb") as fout:
        pickle.dump((ham_words, spam_words), fout)

def test(fname):
    with open("brain", "rb") as fin:
        (ham_words, spam_words) = pickle.load(fin)

    probability_ham = 0.5
    probability_spam = 0.5

    with open(fname) as fin:
        for line in fin:
            (true_clas, text) = line.split("\t")

            words = re.split(r"(?: |,|\.|-|–|:|;|&|=|\+|#|\(|\)|\||<|…|\^|\[|\])+", text)

            for word in words:
                # Remove misc symbols
                word = re.sub(r"^(?:'|\"|“)+", r"", word)
                word = re.sub(r"(\w)'$", r"\1", word)
                word = word.lower()

                if word == "":
                    continue

                ham_instances = ham_words[word]
                spam_instances = spam_words[word]
                total_instances = ham_instances + spam_instances
                if total_instances == 0:
                    continue

                probability_ham *= ham_instances / total_instances
                probability_spam *= spam_instances / total_instances
                print("Spam: {} ham: {}".format(probability_spam, probability_ham))

            if probability_spam > probability_ham:
                guess = "spam"
            else:
                guess = "ham"

            if (guess == true_clas):
                correct = "Correct"
            else:
                correct = "incorrect"
                raise RuntimeError("Incorrect line: {}".format(line))

            print("Our guess: {} (spam: {}, ham: {}), {}".format(guess, probability_spam, probability_ham, correct))

def main():
    parser = argparse.ArgumentParser(description="Spam bayes classifier")

    parser.add_argument("--train", action="store_true")
    parser.add_argument("--test", action="store_true")
    parser.add_argument("file", nargs="?", default="corpus/SMSSpamCollection.txt", help="the file to read the text from.  Don't use the same file for training and testing ;)")

    args = parser.parse_args()

    if args.train:
        train(args.file)

    if args.test:
        test(args.file)

    if not args.train and not args.test:
        parser.print_help()

if __name__ == "__main__":
    main()
