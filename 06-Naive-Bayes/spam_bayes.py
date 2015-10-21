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

def is_spam(text, ham_words, spam_words):
    probability_ham = 0.5
    probability_spam = 0.5

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

    return probability_spam > probability_ham

def train_from_file(fin):
    ham_words = collections.defaultdict(int)
    spam_words = collections.defaultdict(int)

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

def classify_file(fin):
    with open("brain", "rb") as brain_fin:
        (ham_words, spam_words) = pickle.load(brain_fin)

    num_correct = 0
    num_incorrect = 0

    for line in fin:
        (true_class, text) = line.split("\t")

        spam = is_spam(text, ham_words, spam_words)

        guess = "spam" if spam else "ham"

        if (spam and (true_class == "spam")) or \
           (not spam and (true_class == "ham")):
            num_correct += 1
        else:
            num_incorrect += 1

    total = num_correct + num_incorrect
    print("Correct: {}/{} ({:.2%})".format(num_correct, total,
                                           num_correct / total))

def classify_text(text):
    with open("brain", "rb") as fin:
        (ham_words, spam_words) = pickle.load(fin)

    if is_spam(text, ham_words, spam_words):
        print("spam")
    else:
        print("ham")

def main():
    parser = argparse.ArgumentParser(description="Spam bayes classifier")

    parser.add_argument("--train", action="store_true")
    parser.add_argument("--test", action="store_true")
    parser.add_argument("-c", "--classify_text", nargs=1)
    parser.add_argument("file", nargs="?", default="corpus/SMSSpamCollection.txt", help="the file to read the text from.  Use - to read from stdin.  Don't use the same file for training and testing ;)")

    args = parser.parse_args()

    if args.classify_text:
        return classify_text(args.classify_text[0])

    if args.train:
        if args.file == "-":
            train_from_file(sys.stdin)
        else:
            with open(args.file) as fin:
                train_from_file(fin)

    if args.test:
        if args.file == "-":
            classify_file(sys.stdin)
        else:
            with open(args.file) as fin:
                classify_file(fin)

    if not args.train and not args.test:
        parser.print_help()

if __name__ == "__main__":
    main()
