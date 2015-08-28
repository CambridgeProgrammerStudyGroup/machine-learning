#!/usr/bin/env python

import ga
import random
import string


TARGET = "I LOVE GENETIC ALGORITHMS"
MUTATION_RATE = 0.75
MAX_STRING_LENGTH = 100


def generate_random_string():
    return "".join([random.choice(string.ascii_letters + " ")
                    for i in xrange(random.randint(1, MAX_STRING_LENGTH))])


def calculate_fitness(dna):
    fitness = 0
    for (a, b) in zip(dna, TARGET):
        if a == b:
            fitness += 1
    diff_length = abs(len(dna) - len(TARGET))
    fitness -= (diff_length*1.1)
    return fitness


def breed_strings(string1, string2):
    (a1, a2) = split_string(string1)
    (b1, b2) = split_string(string2)
    child_dna = mutate(a1 + b2)
    return child_dna


def split_string(dna):
    return (dna[:len(dna)/2], dna[len(dna)/2:])


def mutate(dna):
    new_dna = dna
    if random.random() < MUTATION_RATE:
        cut_point = random.randint(0, len(dna))
        new_dna = dna[:cut_point-1] + random.choice(string.ascii_letters + " ") + dna[cut_point:]
    return new_dna


def stop_condition(candidate):
    if candidate.this == TARGET:
        return True
    else:
        return False


if __name__ == "__main__":
    ga.run_genetic_algorithm(spawn_func=generate_random_string,
                             breed_func=breed_strings,
                             fitness_func=calculate_fitness,
                             stop_condition=stop_condition,
                             population_size=100)
