#!/usr/bin/env python
import random
import string
import sys
from collections import namedtuple

TARGET="I LOVE GENETIC ALGORITHMS"
MUTATION_RATE = 0.75
Candidate = namedtuple("Candidate", "string fitness")
Population = namedtuple("Population", "average min max size best")

def generate_ran_string():
    return "".join([random.choice(string.ascii_letters + " ") for i in xrange(random.randint(1, 100))])

def generate_population():
   candidates = []
   for i in xrange(500):
        string = generate_ran_string()
        fitness = calculate_fitness(string)
        candidate = Candidate(string=string, fitness=fitness)
        candidates.append(candidate)
   return candidates

def calculate_fitness(string):
    fitness = 0
    for (a,b) in zip(string, TARGET):
        if a==b:
            fitness +=1
    diff_length = abs(len(string) - len(TARGET))
    fitness -= (diff_length*1.1)
    return fitness

def calc_population_stats(pop):
    pop = sorted(pop, key = lambda x: -x.fitness)
    fitnesses = [cand.fitness for cand in pop]
    average = sum(fitnesses) / len(pop)
    min_fit = min(fitnesses)
    max_fit = max(fitnesses)
    length = len(fitnesses)
    best = pop[0]
    population = Population(average=average, min=min_fit, max=max_fit, size=length, best=best.string)
    return population

def select_candidates(pop):
    ordered_pop = sorted(pop, key=lambda x: x.fitness)
    return ordered_pop[len(ordered_pop)/2:]

def breed_population(candidates):
    shuffled_cands = sorted(candidates, key=lambda x: random.randint(1,100))
    pairs = zip(candidates, shuffled_cands)
    cands = []
    for pa, pb in pairs:
        c= breed_candidates(pa, pb)
        cands.extend([pa, c])
    return cands

def breed_candidates(candA, candB):
    (a1, a2) = split_cand(candA)
    (b1, b2) = split_cand(candB)
    string = mutate_child(a1 + b2)
    child = Candidate(string=string, fitness=calculate_fitness(string))
    return child

def split_cand(cand):
     return (cand.string[:len(cand.string)/2], cand.string[len(cand.string)/2:])

def mutate_child(dna):
    new_dna = dna
    if random.random() < MUTATION_RATE:
        cut_point = random.randint(0, len(dna))
        new_dna = dna[:cut_point-1] + random.choice(string.ascii_letters + " ") + dna[cut_point:]
    return new_dna

def main():
    candidates = generate_population()
    num_iter = 0
    while True:
        print calc_population_stats(candidates)
        for cand in candidates:
            if cand.fitness == calculate_fitness(TARGET):
                print cand.string
                print num_iter
                sys.exit(0)
        candidates = select_candidates(candidates)
        candidates = breed_population(candidates)
        num_iter += 1

if __name__ == "__main__":
    main()
