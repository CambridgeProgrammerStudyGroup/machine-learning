#!/usr/bin/env python

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

def label_frequencies(distance_label_pairs):
    label_counter = collections.defaultdict(int)
    for distance, label in distance_label_pairs:
        #print distance, label
        label_counter[label] += 1
    return label_counter

def most_common_label(top_k_labels):#-> string
    label_counter = collections.defaultdict(int)
    for label in top_k_labels:
        label_counter[label]+=1
    label_frequency_pairs = label_counter.items()
    most_frequent_pair = sorted(label_frequency_pairs, key = second)[-1]
    most_frequent_label = most_frequent_pair[0]
    return most_frequent_label

def knn_naive(candidate, flowers, k):
    sorted_flowers = sorted(calculate_all_distances(candidate, flowers), key=first)
    top_k = sorted_flowers[:k]
    top_k_labels = [second(element) for element in top_k]
    return most_common_label(top_k_labels)

def has_equal_frequency_labels(label_frequencies):
    top_k_labels = sorted(label_frequencies.items(), key=second)
    return len(top_k_labels) > 1 and top_k_labels[-1][1] == top_k_labels[-2][1]

def knn_smart(candidate, flowers, k):
    sorted_flowers = sorted(calculate_all_distances(candidate, flowers), key=first)
    top_k = sorted_flowers[:k] # [(distance_to_cand, class_name), ...]
    top_k_labels = [second(element) for element in top_k]
    

    label_freqs = label_frequencies(top_k) #{ 'label': <freq> }
    
    if not has_equal_frequency_labels(label_freqs):
        return most_common_label(top_k_labels)

    print "    equivalent choices:"+str(label_freqs)
    sorted_labels = sorted(label_freqs.items(), key=second)
    temp_label_1 = sorted_labels[-1][0]
    temp_label_2 = sorted_labels[-2][0]
    temp_distance_1 = 0
    temp_distance_2 = 0
    for distance, label in top_k:
        if label==temp_label_1:
            temp_distance_1+=distance
        else:
            temp_distance_2+=distance
    # print " temp_distance_1 "+ str(temp_distance_1)
    # print " temp_label_1 "+ str(temp_label_1)

    # print " temp_distance_2 "+ str(temp_distance_2)
    # print " temp_label_2 "+ str(temp_label_2)

    average_1 = temp_distance_1/label_freqs[temp_label_1]
    average_2 = temp_distance_2/label_freqs[temp_label_2]
    if(average_2<average_1):
        return temp_label_1
    else:
        return temp_label_2
        


flowers = []

with open("iris.data") as datafile:
    reader = csv.reader(datafile)
    for flower in reader:
        flowers.append([float(x) for x in flower[:4]] + [flower[4]])

random.shuffle(flowers)

twentyPer = int( (0.2 * len(flowers)) )
validation = flowers[:twentyPer]
training = flowers[twentyPer:]

def accuracy(classifier, training, validation, k):#-> fraction between 0.0 and 1.0
    return sum([1.0 if classifier(flower,training, k) == flower[4] else 0.0 for flower in validation])/len(validation)

# Q: Better selection of class?
# Q: Effect of k?
for k in range(2,10):
    print "k="+str(k)
    print "    accuracy: " + str(accuracy(knn_smart, training, validation, k))  

