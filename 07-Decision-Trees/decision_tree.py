#!/usr/bin/env python

import csv

from collections import namedtuple, defaultdict
from itertools import groupby


Passenger = namedtuple("Passenger", "name gender age ticket_class fare ticket cabin sibsp parch survived")

discrete_features = ["gender", "ticket_class"]
continuous_features = ["fare", "age", "sibsp", "parch"]
features = discrete_features + continuous_features

def split_discrete_feature(dataset, feature):
    return groupby(dataset, lambda passenger: getattr(passenger, feature))

def split_continuous_feature(dataset, feature):
    total = sum([getattr(passenger, feature) for passenger in dataset])
    mean = float(total) / len(passengers)
    return groupby(dataset, lambda passenger: getattr(passenger, feature) < mean)

def prob_survive(passengers):
    liveness_groups = split_discrete_feature(passengers, "survived")
    survivors =list(dict(liveness_groups)[True])
    return len(survivors)/len(list(passengers))

def calculate_entropy(dataset):
    p1 = prob_survive(dataset)
    p2 = 1 - p1
    entropy = p1 * log(p1, 2) + p2 * log(p2, 2)
    return entropy

with open("titanic3.clean.reordered.csv", "r") as data_file:
    data = csv.reader(data_file, delimiter=",", quotechar="\"")
    data.next()

    passengers = []
    for row in data:
        passengers.append(Passenger(ticket_class=row[0],
                                    name=row[1],
                                    gender=row[2],
                                    age=float(row[3]),
                                    sibsp=int(row[4]),
                                    parch=int(row[5]),
                                    ticket=row[6],
                                    fare=float(row[7]),
                                    cabin=row[8],
                                    survived=bool(int(row[9]))))

    entropies = {}
    for feature in features:
        if feature in discrete_features:
            groups = split_discrete_feature(passengers, feature)
        else:
            groups = split_continuous_feature(passengers, feature)
        entropy = sum([calculate_entropy(group) for key, group in groups])
        entropies[entropy] = feature

    min_entropy = min(entropies.keys())
    feature = entropies[min_entropy]
    print feature
