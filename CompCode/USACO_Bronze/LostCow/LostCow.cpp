/*
ID: florinteo
PROG: LostCow
LANG: Java             
*/
/*
DEF: https://usaco.org/index.php?page=viewproblem2&cpid=735
*/
#include <iostream>
#include <fstream>
#include <sstream>

using namespace std;

int main() {
    #pragma region load data
    ifstream cin("lostcow.in");
    int x, y;
    cin >> x >> y;
    #pragma endregion load data

    #pragma region run algorithm
    int totalDistance = 0;
    int step = 1;
    int crtX = x;
    int newX = x + step;
    while((y - crtX) * (y - newX) > 0) {
        totalDistance += abs(newX - crtX);
        crtX = newX;
        step *= -2;
        newX = x + step;
    }
    totalDistance += abs(y - crtX);
    #pragma endregion run algorithm

    #pragma region write output
    ofstream cout("lostcow.out");
    cout << totalDistance << endl;
    #pragma endregion write output

    #pragma region cleanup
    #pragma endregion cleanup

    return 0;
}
