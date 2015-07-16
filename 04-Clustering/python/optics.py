# OPTICS (SetOfObjects, ε, MinPts, OrderedFile)
# 	FOR i FROM 1 TO SetOfObjects.size DO
# 		Object := SetOfObjects.get(i);
# 		IF NOT Object.Processed THEN
# 			ExpandClusterOrder(SetOfObjects, Object, ε, MinPts, OrderedFile)



def OPTICS(objects, neighbourhood_distance, minPts, output_list):
	""" objects is a sequence of python dicts with the data points as "data"
	and 'processed' set as False:

	[
		{data: [<a>, <b>, ...], processed: False},
		{data: [<a>, <b>, ...], processed: False},
		...
	]
	"""
	for obj in objects:
		if not obj["processed"]:
			ExpandClusterOrder(objects, obj, neighbourhood_distance, minPts, output_list)



# ExpandClusterOrder(SetOfObjects, Object, ε, minPts, OrderedFile);
# 	neighbors := SetOfObjects.neighbors(Object, ε);
# 	Object.Processed := TRUE;
# 	Object.reachability_distance := UNDEFINED;
# 	Object.setCoreDistance(neighbors, ε, minPts);
# 	OrderedFile.write(Object);
# 	IF Object.core_distance <> UNDEFINED THEN
# 		OrderSeeds.update(neighbors, Object);
# 		WHILE NOT OrderSeeds.empty() DO
# 			currentObject := OrderSeeds.next();
# 			neighbors:=SetOfObjects.neighbors(currentObject, ε);
# 			currentObject.Processed := TRUE;
# 			currentObject.setCoreDistance(neighbors, ε, minPts);
# 			OrderedFile.write(currentObject);
# 			IF currentObject.core_distance<>UNDEFINED THEN
# 				OrderSeeds.update(neighbors, currentObject);


def neighbors(obj, objects, neighbourhood_distance):
	return [o for o in objects if distance(obj,o) <= neighbourhood_distance and o != obj]

def coreDistance(obj, neighbors, neighbourhood_distance, minPts):
	# This is WRONG!!!
	# The core distance is the distance so that there 
	# are minPoints within reach exactly.
		if len(neighbors) < minPts:
			return None
		return min([distance(obj,p) for p in neighbors])

def update(neighbors, obj):


def ExpandClusterOrder(objects, obj, neighbourhood_distance, minPts, output_list):
	neighbors = neighbors(obj, objects, neighbourhood_distance)
	obj["processed"] = True
	obj["reachability_distance"] = None
	obj["core_distance"] = coreDistance(obj, neighbors, neighbourhood_distance, minPts)
	output_list.append(obj)
	if obj["core_distance"] is not None:
		cluster_list = [obj]
		update(neighbors, obj)



# OrderSeeds::update(neighbors, CenterObject);
# 	c_dist := CenterObject.core_distance;
# 	FORALL Object FROM neighbors DO
# 		IF NOT Object.Processed THEN
# 			new_r_dist:=max(c_dist,CenterObject.dist(Object));
# 			IF Object.reachability_distance=UNDEFINED THEN
# 				Object.reachability_distance := new_r_dist;
# 				insert(Object, new_r_dist);
# 			ELSE // Object already in OrderSeeds
# 				IF new_r_dist<Object.reachability_distance THEN
# 					Object.reachability_distance := new_r_dist;
# 					decrease(Object, new_r_dist);

# ExtractDBSCAN-Clustering (ClusterOrderedObjs,ε’, MinPts)
# 	// Precondition: ε' ≤ generating dist ε for ClusterOrderedObjs
# 	ClusterId := NOISE;
# 	FOR i FROM 1 TO ClusterOrderedObjs.size DO
# 		Object := ClusterOrderedObjs.get(i);
# 		IF Object.reachability_distance > ε’ THEN
# 			// UNDEFINED > ε
# 			IF Object.core_distance ≤ ε’ THEN
# 				ClusterId := nextId(ClusterId);
# 				Object.clusterId := ClusterId;
# 			ELSE
# 				Object.clusterId := NOISE;
# 		ELSE // Object.reachability_distance ≤ ε’
# 			Object.clusterId := ClusterId;
# 			END; // ExtractDBSCAN-Clustering



# SetOfSteepDownAreas = EmptySet
# SetOfClusters = EmptySet
# index = 0, mib = 0
# WHILE(index < n)
# 	mib = max(mib, r(index))
# 	IF(start of a steep down area D at index)
# 		update mib-values and filter SetOfSteepDownAreas(*)
# 		set D.mib = 0
# 		add D to the SetOfSteepDownAreas
# 		index = end of D + 1; mib = r(index)
# 	ELSE IF(start of steep up area U at index)
# 		update mib-values and filter SetOfSteepDownAreas
# 		index = end of U + 1; mib = r(index)
# 		FOR EACH D in SetOfSteepDownAreas DO
# 			IF(combination of D and U is valid AND(**)
# 				satisfies cluster conditions 1, 2, 3a)
# 				compute [s, e] add cluster to SetOfClusters
# 	ELSE 
# 		index = index + 1
# RETURN(SetOfClusters)

