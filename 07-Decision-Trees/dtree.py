#!/usr/bin/env python3

import csv
from math import log
from itertools import groupby
from pprint import pprint

with open("titanic3.clean.reordered.csv") as datafile:
	reader = csv.reader(datafile)
	all_passengers = list(reader)[1:]

def log2(n):
	return log(n, 2)

def entropy_of_survival(passengers):
	survivors = len([passenger for passenger in passengers if passenger[9] == '1'])
	p_survive = survivors / len(passengers)
	p_died = 1 - p_survive
	entropy = (p_survive * log2(p_survive)) if p_survive > 0 else 0.0 \
			  + (p_died * log2(p_died)) if p_died > 0 else 0.0
	return entropy

def entropy_of_sets(datasets):
	total = sum([len(items) for items in datasets])
	total_entropy = sum([(len(items)/total) * entropy_of_survival(items) for items in datasets])
	return total_entropy

average_age = sum([float(p[3]) for p in all_passengers])/len(all_passengers)
average_fare = sum([float(p[7]) for p in all_passengers])/len(all_passengers)

def discriminate_age(passenger):
	return float(passenger[3]) > average_age

def discriminate_fare(passenger):
	return float(passenger[7]) > average_fare

def discriminate_class(passenger):
	return passenger[0]

def discriminate_gender(passenger):
	return passenger[2]

def discriminate_siblings_spouses(passenger):
	return passenger[4]

def discriminate_children_parents(passenger):
	return passenger[5]

discriminators = {
	"Greater than average age?": discriminate_age,
	"Greater than average fare paid?": discriminate_fare,
	"What class was the passenger in?": discriminate_class,
	"Male of female?": discriminate_gender,
	"How many siblings or spouses?": discriminate_siblings_spouses,
	"How many parents or children?": discriminate_children_parents
}

def split_by(passengers, discriminator):
	return [list(people) for cl, people in groupby(sorted(passengers, key=discriminator), discriminator )]


def choose_question(passengers, local_discriminators):
	lowest_entropy = 0.0
	best_discriminator = None

	for msg, discriminator in local_discriminators.items():
		discriminated_entropy = entropy_of_sets(split_by(passengers, discriminator))
		if discriminated_entropy < lowest_entropy:
			lowest_entropy = discriminated_entropy
			best_discriminator = msg

	assert( best_discriminator != None )
	
	return best_discriminator

def build_tree(passengers, indent=0, local_discriminators=discriminators):

	if len(list(local_discriminators.keys())) > 0:
		# pprint(list(local_discriminators.keys()))

		best_question = choose_question(passengers, local_discriminators)

		print( " "*indent + best_question)
		for subgroup in split_by(passengers, local_discriminators[best_question]):
			
			new_discriminators = local_discriminators.copy()
			del new_discriminators[best_question]

			assert(best_question not in new_discriminators)

			build_tree(subgroup, indent=indent+4, local_discriminators=new_discriminators)
		


build_tree(all_passengers)



