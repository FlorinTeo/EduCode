/*
ID: florin.5
PROG: friday
LANG: C++                 
*/
/* https://usaco.training/usacoprob2?a=3AWD3WW5dPR&S=friday */
#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <list>
#include <vector>

using namespace std;

typedef enum {
    Saturday = 0,
    Sunday,
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday
} WEEK_DAY;

typedef enum {
    January = 0,
    February,
    March,
    April,
    May,
    June,
    July,
    August,
    September,
    October,
    November,
    December
} MONTH;

bool isLeapYear(int year) {
    return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
}

int daysInMonth(int month, int year) {
    return month == February
        ?  28 + (isLeapYear(year) ? 1 : 0)
        :  month <= July
            ? 31 - (month % 2)
            : 30 + (month % 2);
}

map<WEEK_DAY, int> _mapThirteenthCount;

int main() {
    #pragma region load data
     ifstream fin("friday.in");
     int nYears;
     fin >> nYears;
    #pragma endregion load data

    #pragma region run algorithm
    int daysSinceOrigin = 0;
    for (int y = 1900; y < 1900 + nYears; y++) {
        for(int m = January; m <= December; m++) {
            WEEK_DAY wDayThirteens = (WEEK_DAY)((Monday + daysSinceOrigin + 13 - 1) % 7);
            if (_mapThirteenthCount.find(wDayThirteens) != _mapThirteenthCount.end()) {
                _mapThirteenthCount[wDayThirteens]++;
            } else {
                _mapThirteenthCount[wDayThirteens] = 1;
            }
            daysSinceOrigin += daysInMonth((MONTH)m, y); 
        }
    }
    #pragma endregion run algorithm

    #pragma region write output
    ofstream fout("friday.out");
    for(int w = Saturday; w < Friday; w++) {
        fout << _mapThirteenthCount[(WEEK_DAY)w] << " ";
    }
    fout << _mapThirteenthCount[Friday] << endl;
    #pragma endregion write output

    return 0;
}