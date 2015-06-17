using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace KMeansClustering
{
    class Program
    {
        static void Main()
        {
            const string fileName = "iris.data";
            const int numCentroids = 3;
            var rawData = new double[150][];
            var labels = new string[150];

            var lines = File.ReadLines(fileName);
            var row = 0;
            foreach (var line in lines.Select(line => line.Split(',')))
            {
                rawData[row] = new[] { Double.Parse(line[0]), Double.Parse(line[1]), Double.Parse(line[2]), Double.Parse(line[3]) };
                labels[row] = line[4];
                row++;
            }

            Console.WriteLine("Raw unclustered data:\n");
            ShowData(rawData, labels);

            int[] clustering = Cluster(rawData, numCentroids);

            Console.WriteLine("\nK-means clustering complete\n");

            Console.WriteLine("Final clustering with indexes:\n");
            ShowVector(clustering);

            Console.WriteLine("Raw data grouped by cluster:\n");
            ShowClustered(rawData, clustering, numCentroids, labels);

            int[] testList = new int[5];
            for (int i = 0; i < testList.Length; i++)
            {
                testList[i] = i;
            }

            var silhouetteEvaluation = CalculateSilhouette(rawData, clustering, numCentroids);
            Console.WriteLine("Silhouette Evaluation: " + silhouetteEvaluation);

            Console.ReadLine();
        }

        public static int[] Cluster(double[][] rawData, int numCentroids)
        {
            double[][] data = rawData;

            var changed = true;
            var success = true;

            int[] clustering = InitClustering(data.Length, numCentroids, 0);
            double[][] means = Allocate(numCentroids, data[0].Length);

            var iterationsCount = 0;
            while (changed && success)
            {
                success = UpdateMeans(data, clustering, means); // compute new centroid means if possible. no effect if fail
                changed = UpdateClustering(data, clustering, means); // (re)assign points to centroids. no effect if fail
                iterationsCount++;
            }
            Console.WriteLine("Iterated " + iterationsCount + " times");

            return clustering;
        }

        private static int[] InitClustering(int numDataPoints, int numCentroids, int randomSeed)
        {
            var random = new Random(randomSeed);
            int[] clustering = new int[numDataPoints];

            // To make sure that each centroid has at least one data point
            for (int i = 0; i < numCentroids; ++i)
            {
                clustering[i] = i;
            }
            // Assign the others randomly
            for (int i = numCentroids; i < clustering.Length; ++i)
            {
                clustering[i] = random.Next(0, numCentroids);
            }
            return clustering;
        }

        private static double[][] Allocate(int numCentroids, int numColumns)
        {
            double[][] result = new double[numCentroids][];
            for (int k = 0; k < numCentroids; ++k)
                result[k] = new double[numColumns];
            return result;
        }

        private static bool UpdateMeans(double[][] data, int[] clustering, double[][] means)
        {
            // Check existing cluster counts
            var numClusters = means.Length;
            int[] clusterCounts = new int[numClusters];
            for (int i = 0; i < data.Length; ++i)
            {
                int cluster = clustering[i];
                ++clusterCounts[cluster];
            }

            for (int k = 0; k < numClusters; ++k)
                if (clusterCounts[k] == 0)
                    return false; // Bad clustering
            
            // Update
            foreach (double[] t in means)
                for (int j = 0; j < t.Length; ++j)
                    t[j] = 0.0;

            for (int i = 0; i < data.Length; ++i)
            {
                int cluster = clustering[i];
                for (int j = 0; j < data[i].Length; ++j)
                    means[cluster][j] += data[i][j]; // Accumulate sum
            }

            for (int k = 0; k < means.Length; ++k)
                for (int j = 0; j < means[k].Length; ++j)
                    means[k][j] /= clusterCounts[k];
            return true;
        }

        private static bool UpdateClustering(double[][] data, int[] clustering, double[][] means)
        {
            var numCentroids = means.Length;
            bool changed = false;

            int[] newClustering = new int[clustering.Length];
            Array.Copy(clustering, newClustering, clustering.Length);

            double[] distances = new double[numCentroids];

            // Go through each point
            for (int i = 0; i < data.Length; i++)
            {
                for (int k = 0; k < numCentroids; k++)
                    distances[k] = CalculateDistance(data[i], means[k]);

                var newCentroidID = GetIndexOfMinDistance(distances); // Find closest mean ID
                if (newCentroidID == newClustering[i]) continue;
                
                changed = true;
                newClustering[i] = newCentroidID;
            }

            if (changed == false)
                return false; // no change so bail and don't update clustering[][]

            // Check proposed clustering[] cluster counts
            int[] clusterCounts = new int[numCentroids];
            for (int i = 0; i < data.Length; ++i)
            {
                int cluster = newClustering[i];
                ++clusterCounts[cluster];
            }

            for (int k = 0; k < numCentroids; ++k)
                if (clusterCounts[k] == 0)
                    return false; // bad clustering. no change to clustering[][]

            // Update
            Array.Copy(newClustering, clustering, newClustering.Length);
            return true;
        }

        private static double CalculateDistance(double[] pointA, double[] pointB)
        {
            var sumSquaredDiffs = pointA.Select((t, j) => Math.Pow((t - pointB[j]), 2)).Sum();
            return Math.Sqrt(sumSquaredDiffs);
        }

        private static int GetIndexOfMinDistance(double[] distances)
        {
            // Get index of smallest value in array
            int indexOfMin = 0;
            double smallDist = distances[0];
            for (int k = 0; k < distances.Length; k++)
            {
                if (distances[k] < smallDist)
                {
                    smallDist = distances[k];
                    indexOfMin = k;
                }
            }

            return indexOfMin;
        }

        // Evaluation
        static double CalculateSilhouette(double[][] data, int[] clustering, int numCentroids)
        {
            var means = GetCentroids(data, clustering, numCentroids);
            var sum = 0.0;
            for (int pointIndex = 0; pointIndex < data.Length; pointIndex++)
            {
                sum += CalculatePointSilhouette(data, clustering, pointIndex, data[pointIndex], means);
            }

            return sum/data.Length;
        }

        private static double CalculatePointSilhouette(double[][] data, int[] clustering, int pointIndex, double[] point, List<double[]> means)
        {
            var a_i = CalculateAverageDistance(data, clustering, point, pointIndex);

            var distancesToOtherCentroids = new List<double>();
            var pointCentroidId = clustering[pointIndex];
            for (int i = 0; i < means.Count; i++)
            {
                if (i != pointCentroidId) distancesToOtherCentroids.Add(CalculateDistance(point, means[i]));
            }
            var b_i = distancesToOtherCentroids.Min();
            return (b_i - a_i)/Math.Max(a_i, b_i);
        }

        private static List<double[]> GetCentroids(double[][] data, int[] clustering, int numCentroids)
        {
            List<double[]> means = new List<double[]>(numCentroids);
            for (int i = 0; i < numCentroids; i++)
            {
                means.Add(CalculateMean(GetPointsForCluster(data, clustering, i)));
            }
            return means;
        }

        static double[] CalculateMean(List<double[]> points)
        {
            var length = points.First().Length;
            double[] total = new double[length];
            foreach (var point in points)
            {
                for (var i = 0; i < length; i++)
                {
                    total[i] += point[i];
                }
            }

            double[] mean = new double[length];
            for (var j = 0; j < length; j++)
            {
                mean[j] = total[j]/points.Count;
            }

            return mean;
        }

        // Average distance from a point to all the other points in cluster
        static double CalculateAverageDistance(double[][] data, int[] clustering, double[] point, int pointIndex)
        {
            int centroidId = clustering[pointIndex];
            List<double[]> pointsForCluster = GetPointsForCluster(data, clustering, centroidId);
            var totalDistance = pointsForCluster.Sum(currentPoint => CalculateDistance(point, currentPoint));

            return totalDistance/pointsForCluster.Count;
        }

        static List<double[]> GetPointsForCluster(double[][] data, int[] clustering, int centroidId)
        {
            return data.Where((t, i) => clustering[i] == centroidId).ToList();
        }

        // Helper Methods

        static void ShowData(double[][] data, string[] labels)
        {
            for (int i = 0; i < data.Length; ++i)
            {
                for (int j = 0; j < data[i].Length; ++j)
                {
                    if (data[i][j] >= 0.0) Console.Write(" ");
                    Console.Write(data[i][j].ToString("F1") + " ");
                }
                Console.WriteLine(labels[i]);
            }
            Console.WriteLine("");
        }

        static void ShowVector(int[] vector)
        {
            for (int i = 0; i < vector.Length; ++i)
                Console.Write(vector[i] + " ");
            Console.WriteLine("\n");
        }

        static void ShowClustered(double[][] data, int[] clustering, int numCentroids, string[] labels)
        {
            for (int k = 0; k < numCentroids; k++)
            {
                Console.WriteLine("==========================================");
                for (int i = 0; i < data.Length; i++)
                {
                    int clusterID = clustering[i];
                    if (clusterID != k) continue;
                    Console.Write(i.ToString().PadLeft(3) + " ");
                    for (int j = 0; j < data[i].Length; j++)
                    {
                        if (data[i][j] >= 0.0) Console.Write(" ");
                        Console.Write(data[i][j].ToString("F1") + " ");
                    }
                    Console.WriteLine(labels[i]);
                }
                Console.WriteLine("==========================================");
            }
        }
    }
}
