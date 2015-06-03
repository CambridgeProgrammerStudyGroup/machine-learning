from sklearn.datasets import load_iris
from sklearn.cluster import KMeans

iris = load_iris()
X = iris.data
Y = iris.target


clf = KMeans(n_clusters=3)
clf.fit(X)
labels = clf.labels_

labels = clf.predict(X)

for label,target in zip(labels,Y):
    print label,target
