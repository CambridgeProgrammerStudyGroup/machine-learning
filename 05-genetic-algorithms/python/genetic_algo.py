# -*- coding: utf-8 -*-
"""

Simple implementation of a genetic algorithm

Created on Wed Jul 15 19:31:42 2015

@author: Ole Schulz-Trieglaff and Brice Fernandes
"""

from difflib import SequenceMatcher
import random

#sol="WELCOME TO GENETIC ALGORITHMS"
sol="GENETIC ALGORITHMS"

alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZ "

num_candidates = 100

candidate_len = len(sol)

# 
def fitness(candidate):
    #return SequenceMatcher(None,candidate,sol).ratio()
    return sum([ a==b for a,b in zip(candidate,sol) ])    
    
def mutate(candidate):
    randChar = random.choice(alphabet)
    idx = random.randint(0,len(candidate)-1)
    
    new_cd = []
    for j in range(len(candidate)):
        if j==idx:
            new_cd.append(randChar)
        else:
            new_cd.append(candidate[j])
    return "".join(new_cd)
    
def genRanCandidate(candLength):
    return "".join([ random.choice(alphabet) for x in range(candLength) ])
  
# returns population with candidates selected
def selectCandidates(population):    
    populationSorted =  sorted(population,None,fitness)
    len_half = len(population)/2
    return populationSorted[len_half:]

def crossover(c1,c2):
    len_half = len(c1)/2
    c1a = "".join(c1[:len_half])
    c1b = "".join(c1[len_half:])
    c2a = "".join(c2[:len_half])
    c2b = "".join(c2[len_half:])
    
    c1_new = "".join([c1a,c2b])
    c2_new = "".join([c2a,c1b])
    
    return (c1_new,c2_new)

population = [genRanCandidate(candidate_len) for i in range(num_candidates) ]    

print "Population size : ",len(population)
print "Some random samples :",population[1:10]

num_iter = 250
rate =  0.1
for i in range(num_iter):
    selected = selectCandidates(population)    
    cv = [ crossover(a,b) for a,b in zip(selected,reversed(selected))]
    for a,b in cv:
        selected.append(a)
        #selected.append(b)
        
    mutated = []    
    for s in selected:
        r = random.random()
        if r < rate:
            mutated.append(mutate(s))
        else:
            mutated.append(s)
                
    population =  sorted(mutated,None,fitness)            
    print "pop size=",len(population)    
    print "Iter",i," top candidate ",population[len(population)-1]
            
    
    
