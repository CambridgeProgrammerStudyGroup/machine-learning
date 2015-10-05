#!/usr/bin/env python

import random


def generate_random_string(character_list, max_length):
    return "".join([random.choice(character_list)
                    for i in xrange(random.randint(1, max_length))])


def breed_strings(parent1, parent2, character_list, mutation_rate):
    (a1, a2) = split_string(parent1)
    (b1, b2) = split_string(parent2)
    child = mutate_string(a1 + b2, character_list, mutation_rate)
    return child


def split_string(dna):
    return (dna[:len(dna)/2], dna[len(dna)/2:])


def mutate_string(dna, character_list, mutation_rate):
    new_dna = dna
    if random.random() < mutation_rate:
        cut_point = random.randint(0, len(dna))
        new_char = random.choice(character_list)
        new_dna = dna[:cut_point-1] + new_char + dna[cut_point:]
    return new_dna
