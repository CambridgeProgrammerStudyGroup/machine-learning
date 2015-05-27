import csv 
import sys
import pprint
import math
import operator
import random

import plotly.plotly as py
from plotly.graph_objs import *

SPYON=False

def spy(fn):
	def newfn(*args, **kwargs):
		rv = fn(*args, **kwargs)
		print("@spy:", end="")
		pprint.pprint(rv)
		return rv
	if SPYON:
		return newfn
	else:
		return fn

def choose_k(data):
	return 3

def pick_centroids(k, data):
	return data[:k]

def distance(a,b):
	return math.sqrt(sum([(a-b)*(a-b) for a,b in zip(a,b)]))

@spy
def assign_points(centroids,data):
	# assign every point to its closest centroid
	for point in data:
		distances = [ (distance(point, centroid), index) for index, (centroid, points) in enumerate(centroids)]
		d, i = min(distances, key=lambda x: x[0])
		centroids[i][1].append(point)
	return centroids
	
@spy			
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
		acc = points[0]
		N = len(points)
		for point in points[1:]:
			acc = [a+b for a,b in zip(acc, point)]
		mean = [i/N for i in acc]
		centroids.append(mean)
	return [(tuple(centroid), []) for centroid in centroids]

@spy
def centroids_stable(old, new):
	return all(c1 == c2 for (c1, _), (c2, _) in zip(old,new))	

def tick(centroids):
	assign_points(centroids, data)
	return new_centroids(centroids)

# sanity testing
assert( 1 == distance((0,0), (0,1)))
assert(math.sqrt(3) == distance([0,0,0], [1,1,1]))
assert(math.sqrt(3) == distance([1,1,1], [2,2,2]))


with open(sys.argv[1]) as data:
	reader = csv.reader(data, delimiter=",")
	data = [tuple(map(float,row[:-1])) for row in reader]
	
	threshold = 0.01
	k = choose_k(data)

	#Choose initial conditions

	old_centroids = [(tuple(centroid), []) for centroid in pick_centroids(k, data)]
	assign_points(old_centroids, data)
	fresh_centroids = new_centroids(old_centroids)

	counter = 1

	while not centroids_stable(old_centroids, fresh_centroids):
		old_centroids = fresh_centroids
		fresh_centroids = tick(old_centroids)
		counter += 1
	assign_points(fresh_centroids, data)

	pprint.pprint(list(map(lambda x: x[0], fresh_centroids)))
	print("after {} iterations".format(counter))

	points_colors = ['rgb(255,0,0)', 'rgb(0,255,0)', 'rgb(0,0,255)']
	centroid_colors = ['rgba(255,0,0,125)', 'rgba(0,255,0,125)', 'rgba(0,0,255,125)']
	traces = []
	for index, (centroid, points) in enumerate(fresh_centroids):
		traces.append(Scatter(
			x=list(map(lambda x: x[0], points)),
			y=list(map(lambda x: x[1], points)),
			name="Cluster {}".format(index+1),
			mode='markers',
			marker=Marker(color=points_colors[index])
    	))
		traces.append(Scatter(
    		x=[centroid[0]],
    		y=[centroid[1]],
    		mode="markers",
    		showlegend=False,
    		marker=Marker(
				size=20,
				color=centroid_colors[index]
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