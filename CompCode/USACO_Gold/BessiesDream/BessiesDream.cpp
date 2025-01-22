/*
ID: florinteo
PROG: dream
LANG: C++                 
*/
/* USACO 2015 December Contest, Gold (ref: https://usaco.org/index.php?page=dec15results)
DEF: https://usaco.org/index.php?page=viewproblem2&cpid=575
*/
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <climits>
#include <queue>

using namespace std;

#define OFFSET_ROW(dir) ((dir == Up) ? -1 : (dir == Down) ? 1 : 0)
#define OFFSET_COL(dir) ((dir == Left) ? -1 : (dir == Right) ? 1 : 0)
#define OPPOSITE(dir) (Any - 1 - dir)

enum Color {
    RED = 0,
    PINK = 1,
    ORANGE = 2,
    BLUE = 3,
    PURPLE = 4
};

enum Direction {
    Up = 0,
    Right = 1,
    Left = 2,
    Down = 3,
    Any = 4,
};

#pragma region - Globals
// Class representing a node in the maze graph
class Node;
// Class representing a state in the graph traversal algorithm
class State;
// The 2D matrix of Nodes in the graph
vector<vector<Node>> _maze;
// The starting & ending nodes in the maze
Node* pStartNode;
Node* pEndNode;
// The minimum distance of the travel from Start to End
int minDistance;
// The queue to be used in the iterative graph traversal
queue<Node*> _queue;
#pragma endregion - Globals

class Node {
private:
    int _row, _col;
    Color _color;
    // the neighbors of this node in the order Up, Right, Left, Down
    Node* _neighbors[2][4] = {
        { nullptr, nullptr, nullptr, nullptr }, // links if state is non-smelly in this node
        { nullptr, nullptr, nullptr, nullptr }  // links if state is smelly in this node
    };
    // the distance of this node from the start of the route, for each of the
    // two possible states: when the state is not-smelly (distanceFromStart[0])
    // and when the state is smelly (distanceFromStart[1]).
    int _distanceFromStart[2] = { INT_MAX, INT_MAX };
    // the current "smelly" state of this node
    bool _smelly = false;
public:
    void initialize(int row, int col, Color color) {
        _row = row;
        _col = col;
        _color = color;
        for(Direction dir : {Up, Right, Left, Down}) {
            int r = _row + OFFSET_ROW(dir);
            int c = _col + OFFSET_COL(dir);
            if (r >= 0 && r < _maze.size() && c >= 0 && c < _maze[r].size()) {
                _neighbors[false][dir] = &_maze[r][c];
                _neighbors[true][dir] = &_maze[r][c];
            }
        }
    }

    void optimize() {
        for (Direction dir : {Up, Left}) {
            for(bool smelly : {false, true}) {
                Node* pNode1 = _neighbors[smelly][dir];
                Node* pNode2 = _neighbors[smelly][OPPOSITE(dir)];
                if (_color == RED || (!smelly && _color == BLUE)) {
                    // red tiles are removed for both smelly & non-smelly graphs
                    // blue tiles are removed for the non-smelly graph
                    if (pNode1 != nullptr) {
                        pNode1->_neighbors[smelly][OPPOSITE(dir)] = nullptr;
                        _neighbors[smelly][dir] = nullptr;
                    }
                    if (pNode2 != nullptr) {
                        pNode2->_neighbors[smelly][dir] = nullptr;
                        _neighbors[smelly][OPPOSITE(dir)] = nullptr;
                    }
                } else if (_color == PURPLE 
                            && pNode1 != nullptr && pNode2 != nullptr 
                            && (pNode1->_color == PINK || pNode1->_color == PURPLE) 
                            && (pNode2->_color == PINK || pNode2->_color == PURPLE)) {
                    pNode1->_neighbors[smelly][OPPOSITE(dir)] = pNode2;
                    pNode2->_neighbors[smelly][dir] = pNode1;
                    _neighbors[smelly][dir] = nullptr;
                    _neighbors[smelly][OPPOSITE(dir)] = nullptr;
                }
            }
        }
    }

    int getDistanceFromStart() {
        return _distanceFromStart[_smelly];
    }

    void setDistanceFromStart(int distanceFromStart) {
        _distanceFromStart[_smelly] = distanceFromStart;
    }

    int getMinDistanceFromStart() {
        int minDistanceFromStart = min(
            _distanceFromStart[false],
            _distanceFromStart[true]
        );

        return minDistanceFromStart == INT_MAX ? -1 : minDistanceFromStart;
    }

    int improved(Node *pPreviousNode) {
        int prevDistanceFromStart = pPreviousNode->getDistanceFromStart();
        int distanceFromNode = abs(_row - pPreviousNode->_row) + abs(_col - pPreviousNode->_col);
        int newDistanceFromStart = prevDistanceFromStart + distanceFromNode;
        bool newSmelly = (_color == PURPLE || distanceFromNode > 1) ? false
                        : (_color == ORANGE) ? true
                        : pPreviousNode->_smelly;

        if (newDistanceFromStart < _distanceFromStart[newSmelly]) {
            _smelly = newSmelly;
            _distanceFromStart[_smelly] = newDistanceFromStart;
            return true;
        }
        return false;
    }

    void oneStep() {
        // if this is the end node..
        if (this == pEndNode) {
            // ..update the minDistance as needed..
            minDistance = min(minDistance, getDistanceFromStart());
            // ..and return because target was reached
            return;
        }

        // go through each possible direction
        for(Direction dir : {Up, Down, Left, Right}) {
            Node* pNeighbor = _neighbors[_smelly][dir];
            if (pNeighbor != nullptr && pNeighbor->improved(this)) {
                // otherwise go to that neighbor.
                _queue.push(pNeighbor);
            }
        }
    }

    string toString(bool smelly) {
        string result = "     ";
        bool isOrphan = _neighbors[smelly][Left] == nullptr
            && _neighbors[smelly][Up] == nullptr
            && _neighbors[smelly][Down] == nullptr
            && _neighbors[smelly][Right] == nullptr;
        if (!isOrphan) {
            result = to_string(_color);
            result += (_neighbors[smelly][Left] != nullptr) ? "<" : "-";
            result += (_neighbors[smelly][Up] != nullptr) ? "^" : "-";
            result += (_neighbors[smelly][Down] != nullptr) ? "v" : "-";
            result += (_neighbors[smelly][Right] != nullptr) ? ">" : "-";
        }
        return result;
    }
};

// iterative implementation of Dijkstra
void travel() {
    // empty the queue and enqueue the initial state
    _queue = queue<Node*>();
    // start from the start node, with no smell, with no direction constraints and 0 distance from start.
    Node *pNode = pStartNode;
    pNode->setDistanceFromStart(0);
    _queue.push(pStartNode);
    // loop through the queue until it gets empty
    while(!_queue.empty()) {
        pNode = _queue.front();
        _queue.pop();
        pNode->oneStep();
    }
}

void dbgDump() {
    ofstream cout("dream.dbg");
    cout << "non-smelly ------" << endl;
    for(int r = 0; r < _maze.size(); r++) {
        for (int c = 0; c < _maze[r].size(); c++) {
            Node& node = _maze[r][c];
            cout << node.toString(false);
            cout << " ";
        }
        cout << endl;
    }
    cout << endl << "smelly ------" << endl;
    for(int r = 0; r < _maze.size(); r++) {
        for (int c = 0; c < _maze[r].size(); c++) {
            Node& node = _maze[r][c];
            cout << node.toString(true);
            cout << " ";
        }
        cout << endl;
    }
}

int main() {
    #pragma region load data
    ifstream cin("dream.in");
    int nRows, nCols;
    cin >> nRows >> nCols;
    _maze = vector<vector<Node>>(nRows, vector<Node>(nCols));
    // initialize and link all nodes: O(n^2)
    for (int r = 0; r < nRows; r++) {
        for (int c = 0; c < nCols; c++) {
            int color;
            cin >> color;
            _maze[r][c].initialize(r, c, (Color)color);
        }
    }
    // optimize graph by removing or cross-linking nodes: O(n^2)
    for (int r = 0; r < nRows; r++) {
        for (int c = 0; c < nCols; c++) {
            _maze[r][c].optimize();
        }
    }
    pStartNode = &_maze[0][0];
    pEndNode = &_maze[nRows-1][nCols-1];
    minDistance = INT_MAX;
    #pragma endregion load data

    #pragma region run algorithm
    travel();
    #pragma endregion run algorithm

    #pragma region write output
    ofstream cout("dream.out");
    cout << pEndNode->getMinDistanceFromStart() << endl;
    cout.close();
    dbgDump();
    #pragma endregion write output
    return 0;
}
