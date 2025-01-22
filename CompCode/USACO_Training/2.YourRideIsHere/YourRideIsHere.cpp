/*
ID: florin.5
PROG: ride
LANG: C++                 
*/
/* https://usaco.training/usacoprob2?a=Ocbyjnk1Aqx&S=ride */
#include <iostream>
#include <fstream>
#include <string>

using namespace std;

int code(string name) {
    int code = 1;
    for(char ch : name) {
        code = (code * (ch - 'A' + 1)) % 47;
    }
    return code;
}

int main() {
    ifstream fin ("ride.in");
    string cName;
    fin >> cName;
    string gName;
    fin >> gName;
    int cCode = code(cName);
    int gCode = code(gName);
    ofstream fout("ride.out");
    fout << (cCode == gCode ? "GO" : "STAY") << endl;
    return 0;
}