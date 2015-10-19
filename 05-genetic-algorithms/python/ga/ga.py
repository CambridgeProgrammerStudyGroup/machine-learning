#!/usr/bin/env python

import sys
import random
import time
from collections import namedtuple

Candidate = namedtuple("Candidate", "this fitness")
Population = namedtuple("Population", "average min max size best")


def generate_population(generator_func, fitness_func, population_size=100):
    candidates = []
    for i in xrange(population_size):
        candidate = generator_func()
        fitness = fitness_func(candidate)
        candidate = Candidate(this=candidate, fitness=fitness)
        candidates.append(candidate)
    return candidates


def calculate_population_stats(population):
    population = sorted(population, key=lambda x: -x.fitness)
    population_size = len(population)
    fitnesses = [candidate.fitness for candidate in population]
    average_fitness = sum(fitnesses) / population_size
    min_fitness = min(fitnesses)
    max_fitness = max(fitnesses)
    best_candidate = population[0]
    population = Population(average=average_fitness,
                            min=min_fitness,
                            max=max_fitness,
                            size=population_size,
                            best=best_candidate.this)
    return population


def select_candidates(population):
    ordered_population = sorted(population, key=lambda x: x.fitness)
    return ordered_population[len(ordered_population)/2:]


def breed_population(candidates, breed_func, fitness_func):
    shuffled_candidates = sorted(candidates,
                                 key=lambda x: random.randint(1, 100))
    pairs = zip(candidates, shuffled_candidates)
    next_gen = []
    for parent1, parent2 in pairs:
        child_dna = breed_func(parent1.this, parent2.this)
        child_fitness = fitness_func(child_dna)
        child = Candidate(this=child_dna, fitness=child_fitness)
        next_gen.extend([parent1, child])
    return next_gen


def run_genetic_algorithm(spawn_func,
                          breed_func,
                          fitness_func,
                          stop_condition,
                          population_size=100):
    start = time.time()
    candidates = generate_population(spawn_func,
                                     fitness_func,
                                     population_size)
    num_iter = 0
    while True:
        print calculate_population_stats(candidates)
        for candidate in candidates:
            if stop_condition(candidate):
                end = time.time()
                print candidate.this
                print "Number of Iterations: %d" % num_iter
                print "Time Taken: %.1f seconds" % (end - start)
                sys.exit(0)
        candidates = select_candidates(candidates)
        candidates = breed_population(candidates, breed_func, fitness_func)
        num_iter += 1
