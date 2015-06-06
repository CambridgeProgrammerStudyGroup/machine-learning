#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <math.h>
#include <time.h>

#include <boost/algorithm/string.hpp>

// K-means clustering using Lloyd's algorithm

using namespace std;

// number of clusters
size_t K=5;

struct Point {
    Point(double x1,double x2, double x3, double x4) 
        : x1_(x1),
          x2_(x2),
          x3_(x3),
          x4_(x4) 
    { cluster_ = -1; }

    Point& operator+(const Point &p) {
        x1_ += p.x1_;
        x2_ += p.x2_;
        x3_ += p.x3_;
        x4_ += p.x4_;
        cluster_ += p.cluster_;
        return *this;
    }

    double x1_,x2_,x3_,x4_;
    int cluster_;
};

typedef std::vector<Point> DataVec;

std::ostream& operator << (std::ostream &o, const Point& p){
      return o << p.x1_ << " " << p.x2_ << " " << p.x3_ << " " << p.x4_ << " " << p.cluster_;
}

double dist(const Point& p1, const Point& p2) {
    return sqrt(pow((p1.x1_-p2.x1_),2.0) + 
                pow((p1.x2_-p2.x2_),2.0) + 
                pow((p1.x3_-p2.x3_),2.0) + 
                pow((p1.x4_-p2.x4_),2.0));
}

// Fisher-Yates shuffle
template<class fwditer>
fwditer random_unique(fwditer begin, fwditer end, size_t num_random) {
    size_t left = std::distance(begin, end);
    while (num_random--) {
        fwditer r = begin;
        std::advance(r, rand()%left);
        std::swap(*begin, *r);
        ++begin;
        --left;
    }
    return begin;
}


bool getCentroid(const DataVec& data, const int cluster, Point& centroid) {
    size_t num(0);
    Point new_centroid(0.0,0.0,0.0,0.0);
    for (DataVec::const_iterator ct=data.begin();ct!=data.end();++ct) {
        if (ct->cluster_ == cluster) {
            new_centroid.x1_ += ct->x1_;
            new_centroid.x2_ += ct->x2_;
            new_centroid.x3_ += ct->x3_;
            new_centroid.x4_ += ct->x4_;
            ++new_centroid.cluster_;
            ++num;
        }
    }

    if (num==0) {
        std::cout << "Cluster unchanged \n";
        return true;
    }


    new_centroid.x1_ /= num;
    new_centroid.x2_ /= num;
    new_centroid.x3_ /= num;
    new_centroid.x4_ /= num;

    double d = dist(centroid,new_centroid);
    std::cout << "getCentroid: d=" << d << "\n";
    std::cout << "num=" << num << "\n";
    bool changed = d>0.05 ? true : false;
    centroid = new_centroid;
    return changed;
}

bool fit(DataVec& data, DataVec& centroids) {
    bool converged(true);

    // assign points to closest centroid
    for (DataVec::iterator it1 = data.begin(); it1!=data.end(); ++it1) {
        double min_dist = std::numeric_limits<double>::max();
        int min_clust   = -1;
        for (DataVec::iterator it2 = centroids.begin(); it2!=centroids.end(); ++it2) {
            double d = dist(*it1,*it2);
            if (d < min_dist) {
                min_dist = d;
                min_clust = it2-centroids.begin();
            }
        }
        //std::cout << "Point " << *it1 << "\n";
        //std::cout << "min_dist=" << min_dist << " min_clust=" << min_clust << "\n";
        it1->cluster_ = min_clust;
    }
    
    // re-estimate centroids
    for (size_t i=0;i<K;++i) {
        std::cout << "Centroid at " << i << " was " << centroids[i] << "\n";
        bool centroidUpdated = getCentroid(data,i,centroids[i]);
        if (centroidUpdated) converged = false;
        std::cout << "Centroid at " << i << " is now " << centroids[i] << "\n";
    }

    return converged;
}

double dunnIndex(DataVec& data, DataVec& centroids) {
    double res = 0.0;

    // compute max cluster diameter
    double max_clust_diam = 0.0;
    for (size_t i=0;i<K;++i) {
        double clust_diam = 0.0;
        double n = 0.0;
        for (DataVec::const_iterator ct=data.begin();ct!=data.end();++ct) {
            if (ct->cluster_ == i) {
                double d = dist(*ct,centroids[K]);
                clust_diam += d;
                ++n;
            }
        }
        std::cout << "clust_diam = " << clust_diam << "\n";
        if (n>0) clust_diam /= n;
        std::cout << "clust_diam = " << clust_diam << "\n";
        if (clust_diam > max_clust_diam) max_clust_diam = clust_diam;
    }

    // compute min intercluster distance
    double min_clust_dist = std::numeric_limits<double>::max();
    for (size_t i=0;i<K;++i) {
        double d = 0.0;
        for (size_t j=(i+1);j<K;++j) {
            d = dist(centroids[i],centroids[j]);
            //std::cout << "distance btw " << i << " " << j << " d= " << d << "\n";
        }
        if (d>0 && d<min_clust_dist) min_clust_dist = d;
    }
    std::cout << "min_clust_dist = " << min_clust_dist << "\n";
    std::cout << "max_clust_diam = " << max_clust_diam << "\n";
    if (max_clust_diam > 0) res = min_clust_dist / max_clust_diam;
    return res;
}

int main(int argc, char** argv) {
    
    // seed random generator
    srand(time(NULL));

    ifstream infile("../iris.data");
    string line;
    DataVec data;
    double fac = 4.0;
    while (std::getline(infile, line))
    {
        std::vector<std::string> fields;
        boost::split(fields,line, boost::is_any_of(","));
        assert(fields.size() == 5);

        double x1 = atof(fields[0].c_str())*fac;
        double x2 = atof(fields[1].c_str())*fac;
        double x3 = atof(fields[2].c_str())*fac;
        double x4 = atof(fields[3].c_str())*fac;

        Point p(x1,x2,x3,x4);
        //std::cout << p << std::endl;
        data.push_back(p);
    }
    std::cout << "Collected " << data.size() << " points. " << std::endl;
    assert(data.size()>K);
    
    // init centroids to random points
    DataVec centroids;
    //centroids.reserve(K);
    DataVec::iterator dataBegin = random_unique(data.begin(),data.end(),K);
    std::cout << K << " random points " << std::endl;
    for (size_t i=0;i<K;++i) {
        std::cout << data[i] << "\n";
        centroids.push_back(data[i]);
    }
    std::cout << centroids.size() << " centroids " << std::endl;

    // Lloyd's algorithm to iteratively fit the cluster centroids.
    bool done = fit(data,centroids);
    while(!done) {
        done = fit(data,centroids);
        std::cout << done << "\n";
    }

    double idx = dunnIndex(data,centroids);
    cout << "Dunn Index for this clustering " << idx << "\n";

    // write clustering to file
    ofstream of("clusters.dat");
    for (DataVec::iterator it = data.begin(); it!=data.end(); ++it) {
        of << *it << std::endl;
    }
    of.close();

    return EXIT_SUCCESS;
}

