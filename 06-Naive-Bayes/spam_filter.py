#!/usr/bin/env python

import math
import sys


TRAINING_SET = "corpus/SMSSpamCollection.txt"


class Feature(object):
    def __init__(self, feature):
        self.feature = feature
        self.spam_count = 0
        self.ham_count = 0
        self.prob_given_spam = 0
        self.prob_given_ham = 0


class TrainingSet(object):
    def __init__(self, dataset):
        self.dataset = dataset
        self.features = {}
        self.num_spam = 0
        self.num_ham = 0

        self._parse_dataset()
        self._calculate_feature_probabilities()

    def _learn_from(self, msg, category):
        if category == "spam":
            self.num_spam += 1
        else:
            self.num_ham += 1

        unique_features = list(set(msg.split()))

        for feature in unique_features:
            if feature not in self.features.keys():
                self.features[feature] = Feature(feature)

            if category == "spam":
                self.features[feature].spam_count += 1
            else:
                self.features[feature].ham_count += 1

    def _parse_dataset(self):
        with open(self.dataset, "r") as f:
            dataset = f.readlines()

        for datum in dataset:
            parts = datum.split()
            category = parts[0]
            msg = " ".join(parts[1:])
            self._learn_from(msg, category)

    def _calculate_feature_probabilities(self):
        for feature in self.features.values():
            feature.prob_given_spam = float(feature.spam_count) / self.num_spam
            feature.prob_given_ham = float(feature.ham_count) / self.num_ham


def combine_feature_probabilities(message, training_set):
    eta = 0
    infinity_count = 0

    for feature in message.split():
        if feature in training_set.features.keys():
            prob_f_given_s = training_set.features[feature].prob_given_spam
            prob_f_given_h = training_set.features[feature].prob_given_ham
            prob_s_given_f = (prob_f_given_s / (prob_f_given_s + prob_f_given_h))
        else:
            prob_s_given_f = 0.5

        if prob_s_given_f == 1:
            infinity_count -= 1
        elif prob_s_given_f == 0:
            infinity_count += 1
        else:
            eta += (math.log(1 - prob_s_given_f) - math.log(prob_s_given_f))

    if infinity_count:
        if infinity_count > 0:
            prob_spam = 0
        else:
            prob_spam = 1
    else:
        prob_spam = 1 / float(1 + math.exp(eta))

    return prob_spam


def categorise_message(message, training_set):
    prob_spam = combine_feature_probabilities(message, training_set)

    if prob_spam > 0.5:
        category = "spam"
        probability = prob_spam
    else:
        category = "ham"
        probability = 1 - prob_spam

    return category, probability


def process_messages(message_file, training_set):
    with open(message_file, "r") as f:
        messages = f.readlines()

    for message in messages:
        category, probability = categorise_message(message, training_set)
        print "%s with P=%f: %s" % (category, probability, message)


def main(message_file=None):
    if message_file:
        training_file = TRAINING_SET
    else:
        training_file = "half_training_set.txt"
        message_file = "other_half_to_be_categorised.txt"

        with open(TRAINING_SET, "r") as f:
            lines = f.readlines()

        with open(training_file, "w") as f:
            f.writelines(lines[:len(lines)/2 - 1])

        with open(message_file, "w") as f:
            lines = [" ".join(line.split()[1:]) for line in lines]
            f.write("\n".join(lines[len(lines)/2:]))

    training_set = TrainingSet(training_file)
    process_messages(message_file, training_set)


if __name__ == "__main__":
    if len(sys.argv) > 1:
        main(sys.argv[1])
    else:
        main()
