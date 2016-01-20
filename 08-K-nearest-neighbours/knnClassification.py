import csv
import random
import math
import collections

def distance(flower1, flower2):
    sum = 0
    for a,b in zip(flower1[:4], flower2[:4]):
        sum += pow((a-b), 2)
    return math.sqrt(sum)

def calculate_all_distances(candidate, flowers):
    return_val = []
    for flower in flowers:
        return_val.append((distance(candidate, flower),flower[4]))
    return return_val

def first(value):
    return value[0]

def second(value):
    return value[1]

def most_common_label(top_labels):
    label_counter = collections.defaultdict(int)
    for label in top_labels:
        label_counter[label]+=1
    return sorted(label_counter.items(), key = second)[-1][0]

def knn(candidate, flowers, k):
    sorted_flowers = sorted(calculate_all_distances(candidate, flowers), key=first)
    top_k = sorted_flowers[:k]
    top_labels = [second(element) for element in top_k]
    return most_common_label(top_labels)


flowers = []

with open("iris.data.txt") as datafile:
    reader = csv.reader(datafile)
    for flower in reader:
        flowers.append([float(x) for x in flower[:4]] + [flower[4]])
random.shuffle(flowers)

twentyPer = int( (0.2 * len(flowers)) )
validation = flowers[:twentyPer]
training = flowers[twentyPer:]

def accuracy(classifier, training, validation):#-> fraction between 0.0 and 1.0
    return sum([1.0 if classifier(flower,training, 3) == flower[4] else 0.0 for flower in validation])/len(validation)

print accuracy(knn, training, validation)
