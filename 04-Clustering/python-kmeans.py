import csv 
import sys
import pprint
import math
import operator
import random

def choose_k(data):
	return 3

def pick_centroids(k, data):
	return data[:k]

def distance(a,b):
	return math.sqrt(sum([(a-b)*(a-b) for a,b in zip(a,b)]))

def assign_points(centroids,data):
	# assign every point to its closest centroid
	for point in data:
		distances = [(distance(point, centroid), centroid) for centroid in centroids.keys()]
		d, c = min(distances, key=lambda x: x[0])
		centroids[c].append(point)
	
			
def new_centroids(old_centroids):
	"""
	returns a dictionary of the form:

	    {
	    	<point>: [],
	    	<poiny>: []
	    }

	Where <point> is a data point with the same dimension 
	as the data in old_centroids.
	"""
	# start from scratch
	centroids = []
	for centroid, points in old_centroids.items():
		acc = points[0]
		N = len(points)
		for point in points[1:]:
			acc = [a+b for a,b in zip(acc, point)]
		mean = [i/N for i in acc]
		centroids.append(mean)
	news = dict([(tuple(centroid), []) for centroid in centroids])	
	pprint.pprint(news)
	return news

def centroids_not_stable(old, new, threshold):
	#pprint.pprint(old, new)
	old_n_new = zip(old.keys(),new.keys())
	pprint.pprint(old_n_new)
	max_distance = max([distance(a,b) for a,b in old_n_new])
	print("max: {}, threshold: {}".format(max_distance, threshold))
	return max_distance > threshold
		


# sanity testing
assert( 1 == distance((0,0), (0,1)))
assert(math.sqrt(3) == distance([0,0,0], [1,1,1]))
assert(math.sqrt(3) == distance([1,1,1], [2,2,2]))


with open(sys.argv[1]) as data:
	reader = csv.reader(data, delimiter=",")
	data = [map(float,row[:-1]) for row in reader]
	
	threshold = 0.01
	k = choose_k(data)

	old_centroids = [(tuple(centroid), []) for centroid in pick_centroids(k, data)]
	assign_points(old_centroids, data)
	fresh_centroids = new_centroids(old_centroids)

	counter = 1

	while centroids_not_stable(old_centroids, fresh_centroids, threshold):
		assign_points(fresh_centroids, data)
		old_centroids = fresh_centroids
		fresh_centroids = new_centroids(old_centroids)
		counter += 1

	pprint.pprint(centroids.keys())
	print("after {} iterations".format(counter))

