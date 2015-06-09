import csv 
import sys
import pprint
import math
import operator
import random

import plotly.plotly as py
from plotly.graph_objs import *

SPYON = True

def spy(fn):
	def newfn(*args, **kwargs):
		returned = fn(*args, **kwargs)
		pprint.pprint(returned)
		return returned
	if SPYPON:
		return newfn
	else:
		return fn

def choose_k(data):
	return 3

def pick_centroids(k, data):
	return [random.choice(data) for i in range(k)]

def distance(a,b):
	return math.sqrt(sum([(a-b)*(a-b) for a,b in zip(a,b)]))

def assign_points(centroids,data):
	# assign every point to its closest centroid
	for point in data:
		distances = [ (distance(point, centroid), index) for index, (centroid, points) in enumerate(centroids)]
		d, i = min(distances, key=lambda x: x[0])
		centroids[i][1].append(point)
	return centroids
			
def new_centroids(old_centroids):
	"""
	returns a dictionary of the form:

	    [
	    	(<point>, []),
	    	(<point>, []),
	    	...
	    ]

	Where <point> is a data point with the same dimension 
	as the data in old_centroids.
	"""
	# start from scratch
	centroids = []
	for centroid, points in old_centroids:
		if points:
			acc = points[0]
			N = len(points)
			for point in points[1:]:
				acc = [a+b for a,b in zip(acc, point)]
			mean = [i/N for i in acc]
			centroids.append(mean)
		else:
			centroids.append(centroid)
	return [(tuple(centroid), []) for centroid in centroids]


def centroids_stable(old, new):
	return all(c1 == c2 for (c1, _), (c2, _) in zip(old,new))	



def make_plot(centroids):
	# points_colors = ['rgb(255,0,0)', 'rgb(0,255,0)', 'rgb(0,0,255)']
	# centroid_colors = ['rgba(255,0,0,125)', 'rgba(0,255,0,125)', 'rgba(0,0,255,125)']
	traces = []
	for index, (centroid, points) in enumerate(centroids):
		traces.append(Scatter(
			x=list(map(lambda x: x[0], points)),
			y=list(map(lambda x: x[1], points)),
			name="Cluster {}".format(index+1),
			mode='markers',
			#marker=Marker(color=points_colors[index])
    	))
		traces.append(Scatter(
    		x=[centroid[0]],
    		y=[centroid[1]],
    		mode="markers",
    		showlegend=False,
    		marker=Marker(
				size=20,
				#color=centroid_colors[index]
			)
    	))
	data = Data(traces)

	layout = Layout(
	    title='Iris data (length vs width)',
	    xaxis=XAxis(
	        title='Sepal Length',
	        showgrid=False,
	        zeroline=False
	    ),
	    yaxis=YAxis(
	        title='Sepal Width',
	        showline=False
	    )
	)

	fig = Figure(data=data, layout=layout)
	plot_url = py.plot(fig, filename='iris-data')
	print(plot_url)

# sanity testing
assert( 1 == distance((0,0), (0,1)))
assert(math.sqrt(3) == distance([0,0,0], [1,1,1]))
assert(math.sqrt(3) == distance([1,1,1], [2,2,2]))

def average_distance_to(point, points):
	return sum( distance(point, p) for p in points ) / len(points)

assert(1 == average_distance_to( (0,0), [(1,0), (0,1), (-1,0), (0,-1)]))


def si(point, cluster, clustered_points):
	distances_in_my_cluster = []
	distances_in_rest = []

	for p,c in clustered_points:
		if c == cluster:
			distances_in_my_cluster.append( distance(p, point) )
		else:
			distances_in_rest.append( distance(p,point) )

	# average distance to other points in teh same cluster 
	a_i = sum(distances_in_my_cluster) / len(distances_in_my_cluster)
	# average distance to points in other clustered_points
	b_i = sum(distances_in_rest)/len(distances_in_rest)

	return (b_i - a_i)/max([a_i, b_i])

def silhouette(clustered_points):
	return sum( si(point, cluster, clustered_points) for point, cluster in clustered_points )/len(clustered_points)

def centroids_to_clusters(centroids):
	clustered_points = []

	for centroid, points in centroids:
		for point in points:
			clustered_points.append( (point, centroid) )

	return clustered_points

def dunn_index(centroids):
	diameters = []
	for centroid, points in centroids:
		diameters.append(average_distance_to(centroid, points))

	only_centroids = [c for c,ps in centroids]
	
	intercluster_distances = []
	for c1 in only_centroids:
		for c2 in only_centroids:
			if not c1 == c2:
				intercluster_distances.append(distance(c1,c2))
	
	return min(intercluster_distances) / max(diameters)

def tick(centroids, data):
	
	return new_centroids(centroids)

def main():

	datafile = sys.argv[1]
	k = int(sys.argv[2]) # lets us pick the k value on the CLI

	with open(sys.argv[1]) as data:
		reader = csv.reader(data, delimiter=",")
		data = [tuple(map(float,row[:-1])) for row in reader]

		#Choose initial conditions
		old_centroids = [(tuple(centroid), []) for centroid in pick_centroids(k, data)]
		assign_points(old_centroids, data)
		fresh_centroids = new_centroids(old_centroids)

		iterations = 1
		while not centroids_stable(old_centroids, fresh_centroids):
			old_centroids = fresh_centroids
			assign_points(old_centroids, data)
			fresh_centroids = new_centroids(old_centroids)
			iterations += 1
		
		assign_points(fresh_centroids, data)

		# Printing out
		# pprint.pprint(list(map(lambda x: x[0], fresh_centroids)))
		print("after {} iterations".format(iterations))

		print("Dunn Index (k={}): {:.3}".format(k, dunn_index(fresh_centroids)))
		print("Silhouettte metric (k={}): {:.3}".format(k,silhouette(centroids_to_clusters(fresh_centroids))))

		# make_plot(fresh_centroids)

if __name__ == "__main__":
	main()