import random
import string
from collections import namedtuple


target = "Hello World"
MAX_LEN = 50
POPULATION_SIZE = 200
Candidate = namedtuple("Candidate", "string fitness")


def random_string():
    ret_val = ""
    for i in range(random.randint(1, MAX_LEN)):
        ret_val += random.choice(string.ascii_letters + " ")
    return ret_val


def calc_fitness(candidate_string):
    fitness = 0
    for index, target_char in enumerate(target):
        try:
            if candidate_string[index] != target_char:
                fitness -= 1
        except IndexError:
            fitness -= 2
        else:
            if len(candidate_string) > len(target):
                fitness -= 2 * (len(candidate_string) - len(target))

    return fitness


def crossover(parent1, parent2):
    child = parent1[:(len(parent1)/2)] + parent2[len(parent2)/2:]
    return child


def mutation(dna):
    mutation_pt = random.choice(range(len(dna)))
    dna = dna[:mutation_pt] + random.choice(string.ascii_letters + " ") + dna[mutation_pt+1:]
    return dna


def population(size):
    candidates = []
    for i in range(0, size):
        cand_string = random_string()
        fitness = calc_fitness(cand_string)
        candidates.append(Candidate(string=cand_string, fitness=fitness))

    return candidates


def selection(candidates):
    sorted_candidate_list = sorted(candidates, key=lambda candidate: candidate.fitness)
    return sorted_candidate_list[len(sorted_candidate_list)/2:]


def breeding(candidates):
    breeding_pool = selection(candidates)
    breeding_pool_copy = list(breeding_pool)
    random.shuffle(breeding_pool)
    cand_pairs = zip(breeding_pool, breeding_pool_copy)
    for cand1, cand2 in cand_pairs:
        child = crossover(cand1.string, cand2.string)
        child = mutation(child)
        child_fitness = calc_fitness(child)
        breeding_pool.append(Candidate(string=child, fitness=child_fitness))
    return breeding_pool


def main():
    candidates = population(POPULATION_SIZE)
    print candidates
    while True:
        candidates = breeding(candidates)
        candidates = sorted(candidates, key=lambda candidate: candidate.fitness)
        print("Best candidate is: " + candidates[-1].string + " with fitness: %d" % candidates[-1].fitness)
        if candidates[-1].fitness == 0:
            break


if __name__ == "__main__":
    main()