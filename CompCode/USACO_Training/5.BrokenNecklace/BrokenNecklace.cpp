/*
ID: florin.5
PROG: beads
LANG: C++                 
*/
/* https://usaco.training/usacoprob2?a=3AWD3WW5dPR&S=beads */
#include <iostream>
#include <fstream>

using namespace std;

class Bead {
    private:
        char _color;
        int _count;
        Bead* _next;
        Bead* _prev;
    public:
        Bead(char color) {
            _color = color;
            _count = 1;
            _next = _prev = this;
        }

        Bead* next() {
            return _next;
        }

        Bead* prev() {
            return _prev;
        }

        void addTail(char color) {
            if (color == _prev->_color) {
                _prev->_count++;
            } else {
                Bead* bead = new Bead(color);
                bead->_next = this;
                bead->_prev = this->_prev;
                this->_prev->_next = bead;
                this->_prev = bead;
            }
        }

        Bead* removeTail() {
            Bead* bead = _prev;
            bead->_prev->_next = this;
            this->_prev = bead->_prev;
            return bead;
        }

        int count() {
            int count = 0;
            char adjacentColor = 'w';
            Bead* prevCrt = this->prev();
            while(prevCrt != this) {
                if (prevCrt->_color == 'w' || prevCrt->_color == adjacentColor) {
                    count += prevCrt->_count;
                } else if (adjacentColor == 'w') {
                    adjacentColor = prevCrt->_color;
                    count += prevCrt->_count;
                } else {
                    break;
                }
                prevCrt = prevCrt->prev();
            }
            // point prevCrt to the last bead from the left that was counted
            prevCrt = prevCrt->next();
            count += _count;
            adjacentColor = (adjacentColor == _color) ? 'w' : _color;
            Bead* nextCrt = this->next();
            while(nextCrt != prevCrt) {
                if (nextCrt->_color == 'w' || nextCrt->_color == adjacentColor) {
                    count += nextCrt->_count;
                } else if (adjacentColor == 'w') {
                    adjacentColor = nextCrt->_color;
                    count += nextCrt->_count;
                } else {
                    break;
                }
                nextCrt = nextCrt->next();
            }
            return count;
        }
};

Bead* _head = nullptr;

int main() {
    #pragma region load data
    ifstream cin("beads.in");
    int n;
    string beads;
    cin >> n >> beads;
    int countWhite = 0;
    for (int i = 0; i < n; i++) {
        if (_head == nullptr) {
            _head = new Bead(beads.at(i));
        } else {
            _head->addTail(beads.at(i));
        }
    }
    #pragma endregion load data

    #pragma region run algorithm
    int count = -1;
    Bead* crt = _head;
    do {
        count = max(count, crt->count());
        crt = crt->next();
    } while(crt != _head);
    #pragma endregion run algorithm

    #pragma region write output
    ofstream cout("beads.out");
    cout << count << endl;
    #pragma endregion write output

    #pragma region cleanup
    while(_head->next() != _head) {
        delete _head->removeTail();
    }
    delete _head;
    _head = nullptr;
    #pragma endregion cleanup

    return 0;
}