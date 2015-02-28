#!/usr/bin/env python

import resource


points = []

with open("../data/ex2x.dat", 'r') as fx:
  with open("../data/ex2y.dat", 'r') as fy:
    xs = [float(xi) for xi in fx.readlines()]
    ys = [float(yi) for yi in fy.readlines()]
    points = zip(xs,ys) 

def RSS(points, fn):
  return sum([ (fn(x)-y)**2 for (x,y) in points])

def linear(a,c):
  def fn(x):
    return (a*x)+c
  return fn

step = 0.00001

a0 = 0.0
c0 = 0.0

def solve(points,a,c):
  rss = RSS(points, linear(a,c))

  candidates = [
    (a+step, c),
    (a-step,c),
    (a,c+step),
    (a,c-step),
    (a-step,c-step),
    (a+step,c-step),
    (a+step,c+step),
    (a-step,c+step)
  ]
  
  best = min([
    (RSS(points, linear(a,c)),a,c) for a,c in candidates])
  return best

rss = RSS(points, linear(a0,c0))
best = solve(points,a0,c0)

while best[0] < rss: 
  rss = RSS(points, linear(best[1], best[2]))
  best = solve(points, best[1], best[2])

rss,a,c = best

print(
  "y={}x + {}".format(a,c)
)
