/*
ID: florinteo
PROG: gifts
LANG: C++                 
*/
/*
DEF: https://usaco.org/index.php?page=viewproblem2&cpid=1206 
IDEEA: Each Cow is a node in a DAG. Each can hope to exchange a gift only with
the other Cows that may form a loop in DAG. In a loop gifts might possibly be
shifted until the most preferred one gets to the Cow interested in it.
REF: https://wiki.usaco.fun/index.php/2022_Feb_Gold_Problem_1_Redistributing_Gifts
*/
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <queue>

using namespace std;

#pragma region - Globals
// Class representing a Cow
class Cow;
// Total number of cows
 int _nCows;
// Vector of all cows in the farm
vector<Cow*> _cows;
#pragma endregion - Globals

class Cow {
    private:
        /// @brief id of this cow
        int _id;
        /// @brief a list of references to the Cows owning the "wish list" gifts, in priority order
        /// (in DAG representation: _wishList is the list of its immediate neighbors)
        vector<Cow*> _wishList;
        /// @brief a vector of _nCows size, pointing to all other cows that may be interesting for an exchange
        /// (in DAG representation: _wishedBy contains indexed references to the other nodes that that have a path to this one)
        vector<Cow*> _wishedBy;

    public:
        Cow(int id) {
            _id = id;
            _wishedBy = vector<Cow*>(_nCows + 1);
            // all cows are indexed from 1
            _wishedBy[0] = nullptr;
        }

        /// @brief Sets the links from this Cow to the other Cows holding the gifs on its wish list.
        /// @param wishList String representation of the wish list.
        void setWishList(string wishList) {
            stringstream parser(wishList);
            int wishGift;
            while(parser >> wishGift && wishGift != _id) {
                _wishList.push_back(_cows[wishGift]);
            }
            _wishList.push_back(_cows[_id]);
        }

        /// @brief sets this Cow in the "wished by" list of all other Cows that might be interested
        /// in the gift owned by it.
        /// (in DAG representation: sets a reference to this node in all other nodes that have a path to it)
        void fillWishedBy() {
            queue<Cow*> queue;
            queue.push(this);
            while(!queue.empty()) {
                Cow* pCow = queue.front();
                queue.pop();
                pCow->_wishedBy[_id] = this;
                for(auto pNextCow : pCow->_wishList) {
                    if (pNextCow->_wishedBy[_id] == nullptr) {
                        queue.push(pNextCow);
                    }
                }
            }
        }

        /// @brief Determines the gift most preferred by this Cow: this is the gift in the "wish list"
        /// which the Cow currently owning it might be interested in exchanging.
        /// (in DAG representation: the first node in the list of neighbors which has a path back to this node)
        /// @return The most preferred gift number.
        int getMostPreferred() {
            for(auto pCow : _wishList) {
                if (_wishedBy[pCow->_id] != nullptr) {
                    return pCow->_id;
                }
            }
            // won't reach here, since each cow is "wishedBy" itself at a minimum.
            return -1;
        }
};

int main() {
    #pragma region load data
    ifstream cin("gifts.in");
    cin >> _nCows;
    cin.ignore();

    // first create the Cow objects and index them in the _cows array from index 1.
    _cows.push_back(nullptr);
    for (int i = 1; i <= _nCows; i++) {
        _cows.push_back(new Cow(i));
    }

    // then read and update the wishlist of each cow
    for(int i = 1; i <= _nCows; i++) {
        string line;
        getline(cin, line);
        _cows[i]->setWishList(line);
    }
    #pragma endregion load data

    #pragma region run algorithm
    // determine the reverse "wished by" links for each cow
    for (int i = 1; i <= _nCows; i++) {
        _cows[i]->fillWishedBy();
    }
    #pragma endregion run algorithm

    #pragma region write output
    ofstream cout("gifts.out");
    for(int i = 1; i <= _nCows; i++) {
        cout << _cows[i]->getMostPreferred() << endl;
    }
    cout << flush;
    #pragma endregion write output

    #pragma region cleanup
    for(int i = 1; i < _cows.size(); i++) {
        delete _cows[i];
    }
    #pragma endregion cleanup

    return 0;
}
