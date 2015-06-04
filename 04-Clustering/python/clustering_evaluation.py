from sklearn.datasets import load_iris
from sklearn.cluster import KMeans
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

iris = load_iris()
X = iris.data
Y = iris.target

clf = KMeans(n_clusters=3)
clf.fit(X)
labels = clf.labels_

intersection = np.ndarray((3,3))
for i in [0,1,2]:
    for j in [0,1,2]:
        where_y = list(np.where(Y==i)[0])
        where_l = list(np.where(labels==j)[0])
        intersection[i, j] = len(set(where_y) & set(where_l))
mapping_predict_true = dict(zip(np.argmax(intersection,1),[0,1,2]))

labels = map(lambda x: mapping_predict_true[x], labels)

color_map = {False : 'r', True: 'g'}

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')
ax.scatter(X[:,0],X[:,1],X[:,2], marker = 'o', color = map(lambda x: color_map[x], labels==Y))
plt.show()


print 'Accuracy: '
print sum(labels==Y)/float(len(Y))
