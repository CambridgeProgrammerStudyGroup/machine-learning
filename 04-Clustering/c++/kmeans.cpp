#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <math.h>

#include <boost/algorithm/string.hpp>

// K-means clustering using Lloyd's algorithm

using namespace std;

// number of clusters
unsigned int K=4;

struct Point {
    Point(double x1,double x2, double x3, double x4) 
        : x1_(x1),
          x2_(x2),
          x3_(x3),
          x4_(x4) 
    { cluster_ = -1; }

    double x1_,x2_,x3_,x4_;
    int cluster_;
};

std::ostream& operator << (std::ostream &o, const Point& p){
      return o << p.x1_ << " " << p.x2_ << " " << p.x3_ << " " << p.x4_ << " " << p.cluster_;
}

double distance(const Point& p1, const Point& p2) {
    return sqrt(pow((p1.x1_-p2.x1_),2.0) + 
                pow((p1.x2_-p2.x2_),2.0) + 
                pow((p1.x3_-p2.x3_),2.0) + 
                pow((p1.x4_-p2.x4_),2.0));
}


int main(int argc, char** argv) {

    ifstream infile("../iris.data");
    string line;
    std::vector<Point> data;
    while (std::getline(infile, line))
    {
        std::vector<std::string> fields;
        boost::split(fields,line, boost::is_any_of(","));
        assert(fields.size() == 5);

        double x1 = atof(fields[0].c_str());
        double x2 = atof(fields[1].c_str());
        double x3 = atof(fields[2].c_str());
        double x4 = atof(fields[3].c_str());

        Point p(x1,x2,x3,x4);
        std::cout << p << std::endl;
        
    }
    return EXIT_SUCCESS;
}

