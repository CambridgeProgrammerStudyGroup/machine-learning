#!/usr/bin/env python

import copy
import ga
import operator
import random
import sys


INBREEDING_RATE = 0.25
MAX_TREE_DEPTH = 25
POPULATION_SIZE = 100


def target(x):
    return 3*x - 5


class RandomOperator(object):
    def __init__(self):
        self.function, self._str = random.choice([(operator.add, "+"),
                                                  (operator.mul, "*"),
                                                  (operator.sub, "-"),
                                                  (operator.div, "/")])

    def __call__(self, *args, **kwargs):
        return self.function(*args, **kwargs)

    def __repr__(self):
        return self._str


class Oper(object):
    def __init__(self, depth):
        self.depth = depth
        self.oper = RandomOperator()
        self.children = [generate_tree(depth+1), generate_tree(depth+1)]

    def __call__(self, x_val):
        return self.oper(self.children[0](x_val), self.children[1](x_val))

    def __repr__(self):
        return "("+str(self.children[0])+str(self.oper)+str(self.children[1])+")"


class Leaf(object):
    def __init__(self, depth):
        self.oper = None
        self.depth = depth
        self.value = _choose_terminal()

    def __call__(self, x_val):
        return float(self.value) if self.value is not "x" else x_val

    def __repr__(self):
        return str(self.value)


def _choose_terminal():
    return random.choice(["x", random.randint(-100, 100)])


def _is_leaf(tree):
    if hasattr(tree, "children"):
        return False
    else:
        return True


def generate_tree(depth=0):
    if depth < MAX_TREE_DEPTH:
        return random.choice([Oper, Leaf])(depth)
    else:
        return Leaf(depth)


def combine_trees(tree1, tree2):
    tree1_copy = copy.deepcopy(tree1)
    tree2_copy = copy.deepcopy(tree2)

    if not _is_leaf(tree1):
        if not _is_leaf(tree2):
            tree1_copy.children[1] = tree2_copy.children[0]
        else:
            tree1_copy.children[1] = tree2_copy

        child = tree1_copy
    else:
        if not _is_leaf(tree2):
            tree2_copy.children[1] = tree1_copy
        else:
            tree2_copy.value = _choose_terminal()

        child = tree2_copy

    return child


def choose_random_tree_element(tree):
    depth = random.randint(0, MAX_TREE_DEPTH)

    tree_element = tree
    for i in xrange(depth):
        if not _is_leaf(tree_element):
            child = random.choice([0, 1])
            tree_element = tree_element.children[child]
        else:
            break

    return tree_element


def mutate(tree):
    tree_element = choose_random_tree_element(tree)

    if _is_leaf(tree_element):
        tree_element.value = _choose_terminal()
    else:
        tree_element.oper = RandomOperator()

    return tree


def avoid_inbreeding():
    if random.random() < INBREEDING_RATE:
        return False
    else:
        return True


def breed(parent1, parent2):
    if avoid_inbreeding():
        new_blood = generate_tree()
        child = combine_trees(parent1, new_blood)
    else:
        child = combine_trees(parent1, parent2)

    child = mutate(child)

    return child


def calc_fitness(func):
    try:
        x_vals = xrange(-100, 100, 1)
        reference_vals = map(target, x_vals)
        tested_vals = map(func, x_vals)
        differences = [(r - t) for (r, t) in zip(reference_vals, tested_vals)]
        sum_of_squares = sum([a*a for a in differences])
        return -sum_of_squares

    except ZeroDivisionError:
        return -sys.maxint


def stop_condition(candidate):
    if candidate.fitness == 0:
        return True
    else:
        return False


if __name__ == "__main__":
    ga.run_genetic_algorithm(spawn_func=generate_tree,
                             breed_func=breed,
                             fitness_func=calc_fitness,
                             stop_condition=stop_condition,
                             population_size=POPULATION_SIZE)
