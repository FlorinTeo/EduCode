/*
ID: florin.5
PROG: gift1
LANG: C++                 
*/
/* https://usaco.training/usacoprob2?a=Ocbyjnk1Aqx&S=gift1 */
#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <list>

using namespace std;

class Giver {
    private:
        string _name;
        int _initialBudget;
        int _currentBudget;
        list<Giver*> _recepients;
    public:
        Giver(string name) {
            _name = name;
            _initialBudget = 0;
            _currentBudget = 0;
        }

        void setInitialBudget(int initialBudget) {
            _initialBudget = initialBudget;
            _currentBudget = initialBudget;
        }

        void addRecepient(Giver* recepient) {
            _recepients.push_back(recepient);
        }

        void give() {
            int amount = _recepients.size() > 0
                        ?_initialBudget / _recepients.size()
                        : 0;
            for(Giver* recepient : _recepients ) {
                recepient->receive(amount);
                _currentBudget -= amount;
            }
        }

        void receive(int amount) {
            _currentBudget += amount;
        }

        string toString() {
            return _name + " " + to_string(_currentBudget - _initialBudget);
        }
};

list<Giver *> _giversList;
map<string, Giver*> _giversMap;

int main() {
    #pragma region load data
    ifstream fin("gift1.in");
    int np;
    fin >> np;
    // load giver names
    for(int i = 0; i < np; i++) {
        string gName;
        fin >> gName;
        Giver* giver = new Giver(gName);
        _giversList.push_back(giver);
        _giversMap[gName] = giver;
    }
    // load giver data
    for (int iGiver = 0; iGiver < np; iGiver++) {
        string gName;
        fin >> gName;
        Giver* g = _giversMap[gName];
        int initialBudget, nReceivers;
        fin >> initialBudget >> nReceivers;
        g->setInitialBudget(initialBudget);
        for (int iReceiver = 0; iReceiver < nReceivers; iReceiver++) {
            string rName;
            fin >> rName;
            Giver* r = _giversMap[rName];
            g->addRecepient(r);
        }
    }
    #pragma endregion load data

    #pragma region run algorithm
    for(Giver* giver : _giversList) {
        giver->give();
    }
    #pragma endregion run algorithm

    #pragma region write output
    ofstream fout("gift1.out");
    for(Giver* giver : _giversList) {
        fout << giver->toString() << endl;
    }
    fout.close();
    #pragma endregion write output

    #pragma region cleanup
    _giversMap.clear();
    while(!_giversList.empty()) {
        Giver* giver = _giversList.front();
        _giversList.pop_front();
        delete(giver);
    }
    #pragma endregion cleanup

    return 0;
}