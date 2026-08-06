//task13
//Melissa Zierer

#include <iostream>
#include <array>
#include <cstdlib>
#include <ctime>
using namespace std;

const int NCOL = 5;
const int NROW = 5;
typedef array <array<int,NCOL>,NROW> TMat;

TMat saddlePoint(TMat m);
int checkPoints(TMat m, int i, int j);
void printMat(TMat m);

int main(){

    TMat m = {{
        {{1, 2, 3, 4, 5}},
        {{5, 6, 7, 8, 6}},
        {{4, 9, 8, 7, 5}},
        {{6, 8, 9, 10, 4}},
        {{7, 6, 5, 4, 3}}
    }};

    printMat(saddlePoint(m));


    return 0;
}

TMat saddlePoint(TMat m){
    TMat result = {};
    for(int i = 0; i < NROW; i++){
        for(int j = 0; j < NCOL; j++){
            result[i][j] = checkPoints(m, i, j);
        }
    }
    return result;
}

int checkPoints(TMat m, int i, int j){
    int current = m[i][j];
    bool maxRow = true;
    bool minCol = true;

    for(int k = 0; k < NROW; k++){
        if(m[i][k] > current) maxRow = false;
    }

    for(int k = 0; k < NCOL; k++){
        if(m[k][j] < current) minCol = false;
        }

    if(maxRow and minCol){
        return +1;
    }

    bool maxCol = true;
    bool minRow = true;

    for(int k = 0; k < NROW; k++){
        if(m[i][k] < current) minRow = false;
    }

    for(int k = 0; k < NCOL; k++){
        if(m[k][j] > current) maxCol = false;
    }

    if(maxCol and minRow){
        return -1;
        }

    return 0;

}

void printMat(TMat m){

    for(int i = 0; i < NROW; i++){
        for(int j = 0; j < NCOL; j++){
            cout << m[i][j] << " ";
        }
        cout << endl;
    }
}


//Lösung
#include <iostream>
#include <iomanip>
#include <array>
using namespace std;

const int NROWS = 3;
const int NCOLS = 4;
typedef array<array<int,NCOLS>,NROWS> TMat;

int main(int argc, char *argv[])
{
    TMat saddlePoints(TMat m);
    void print(TMat m);

    TMat m = {{
        {{1,  3, 7,  1}},
        {{0,  2, 1, -1}},
        {{1,  3, 2,  3}}
    }};
    print(saddlePoints(m));
    return 0;
}

TMat saddlePoints(TMat m)
{
    bool maxVert(TMat m, int row, int col);
    bool minVert(TMat m, int row, int col);
    bool maxHor(TMat m, int row, int col);
    bool minHor(TMat m, int row, int col);

    TMat r;
    for (int row = 0; row < NROWS; ++row) {
        for (int col = 0; col < NCOLS; ++col) {
            if ((maxHor(m, row, col) and minVert(m, row, col)))
                r[row][col] = 1;
            else if (minHor(m, row, col) and maxVert(m, row, col))
                r[row][col] = -1;
            else
                r[row][col] = 0;
        }
    }
    return r;
}

bool maxVert(TMat m, int row, int col)
{
    return (row == 0 or m[row-1][col] < m[row][col]) and
           (row == NROWS-1 or m[row+1][col] < m[row][col]);
}
bool minVert(TMat m, int row, int col)
{
    return (row == 0 or m[row-1][col] > m[row][col]) and
           (row == NROWS-1 or m[row+1][col] > m[row][col]);
}
bool maxHor(TMat m, int row, int col)
{
    return (col == 0 or m[row][col-1] < m[row][col]) and
           (col == NCOLS-1 or m[row][col+1] < m[row][col]);
}
bool minHor(TMat m, int row, int col)
{
    return (col == 0 or m[row][col-1] > m[row][col]) and
           (col == NCOLS-1 or m[row][col+1] > m[row][col]);
}

void print(TMat m)
{
    for (int i = 0; i < NROWS; ++i) {
        for (int j = 0; j < NCOLS; ++j) {
            cout << setw(3) << m[i][j];
        }
        cout << endl;
    }
}