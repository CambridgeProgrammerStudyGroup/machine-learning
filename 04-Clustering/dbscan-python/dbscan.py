import csv
import math
from collections import defaultdict

# simple implementation of DB-scan algorithm
# https://en.wikipedia.org/wiki/DBSCAN

def distance(p1,p2):
    return math.sqrt(sum([(x-y)*(x-y) for x, y in zip(p1,p2)]))

def regionQuery(point,eps,data):
    neighbours = []
    for d in data:
        dst = distance(d,point)
        if dst<eps:
            neighbours.append(d)
    return neighbours

def expandCluster(point,NeighborPts,C,eps,minPts,visited,data):
    #print "Expand cluster=",point
    C.append(point)
    for p in NeighborPts:
        if p not in visited:
            visited[p] = 1
            NeighborPtsPrime = regionQuery(p,eps,data)
            if len(NeighborPtsPrime) >= minPts:
                NeighborPts.extend(NeighborPtsPrime)
            C.append(p)
    #print "Cluster C is ",C

def dbscan(data,eps,minPts,visited):
    clusters = []
    for p in data:
        #print "p=",p
        if p in visited:
            continue
        visited[p] = 1
        NeighborPts = regionQuery(p,eps,data)
        if len(NeighborPts) < minPts:
            # set this point to noise
            print "NOISE: ",p
            visited[p] = -1
        else:
            C = []
            expandCluster(p,NeighborPts,C,eps,minPts,visited,data)
            clusters.append(C)
    return clusters

def main():

    datafile = "../iris.data"

    with open(datafile) as data:
        reader = csv.reader(data, delimiter=",")
        data = [tuple(map(float,row[:-1])) for row in reader]

    minPts = 3
    eps    = 0.5
    visited = defaultdict(int)
    clusters = dbscan(data,eps,minPts,visited)

    # write clusters to file
    with file('dbscan.clusters','w') as outfile:
        csv_out=csv.writer(outfile)
        for i in range(0,len(clusters)):
            for p in clusters[i]:
                csv_out.writerow(p+(i,))



if __name__ == "__main__":
	main()
