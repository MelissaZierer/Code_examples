//Exam questions
//Melissa Zierer

#include <iostream>
#include <array>
using namespace std;

const int N = 8;
typedef array<array<char,N>,N> TSqMat;

void wordPuzzle(TSqMat m, string str);
void fillMat(TSqMat& m, string s, int row);
void showMat(TSqMat m);

bool checkNorth(TSqMat m, string str, int i, int j);
bool checkSouth(TSqMat m, string str, int i, int j);
bool checkEast(TSqMat m, string str, int i, int j);
bool checkWest(TSqMat m, string str, int i, int j);

bool startSearch(TSqMat m, string str, int i, int j, string& direction);

int main(){

    TSqMat m = {{}};
    cout << "Enter a string of 8 chars: " << endl;
    int i = 0;
    while(i < N){
        string s = "";
        cin >> s;
        if(s.length() == 8){
        fillMat(m,s,i);
        }else{
            i--;
        }
        i++;
    }
    wordPuzzle(m,"programming");

    return 0;
}

void wordPuzzle(TSqMat m, string str){
    bool found = false;
    bool start = false;
    int si = -1;
    int sj = -1;
    string direction = "";
    int i = 0;
    while(i < N and !found){
        int j = 0;
        while(j < N and !found){
            if(m[i][j] == str[0]){
                si = i;
                sj = j;
                if(startSearch(m, str, si, sj, direction)){
                    found = true;
                }
                
            }
            j++;
        }
        i++;
    }
    if(found){
        cout << "The word " << str << " is in the position (" << si << ", " << sj << ") " << direction << endl;
    }
}

bool startSearch(TSqMat m, string str, int i, int j, string& direction){


        if(checkNorth(m, str, i, j)){
            direction = "North";
            return true;
        }
        if(checkSouth(m, str, i, j)){
            direction = "South";
            return true;
        }
        if(checkEast(m, str, i, j)){
            direction = "East";
            return true;
        }
        if(checkWest(m, str, i, j)){
            direction = "West";
            return true;
        }
    
    return false;
}




bool checkNorth(TSqMat m, string str, int i, int j){
    bool check = true;
    int y = i;
    int x = 0;
    while(check and y >= 0 and x < str.length()){
        if(m[y][j] != str[x]) check = false;
        y--;
        x++;
    }
    return check;
}

bool checkSouth(TSqMat m, string str, int i, int j) {
    int x = 0;
    while (i + x < N && x < str.length()) {
        if (m[i + x][j] != str[x]) return false;
        x++;
    }
    return x == str.length();
}

bool checkEast(TSqMat m, string str, int i, int j){
    bool check = true;
    int y = j;
    int x = 0;
    while(check and y < N and x < str.length()){
        if(m[j][y] != str[x]) check = false;
        y++;
        x++;
    }
    return check;
}

bool checkWest(TSqMat m, string str, int i, int j){
    bool check = true;
    int y = j;
    int x = 0;
    while(check and y > 0 and x < str.length()){
        if(m[i][y] != str[x]) check = false;
        y--;
        x++;
    }
    return check;
}



void fillMat(TSqMat& m, string s, int row){
    for(int i = 0; i < N; i++){
        m[row][i] = s[i];
    }
}

void showMat(TSqMat m){
    for(int i = 0; i < N; i++){
        for(int j = 0; j < N; j++){
            cout << m[i][j];
        }
        cout << endl;
    }
}

bool inborder(int i, int j){
    return(i > 0 and i < N and j > 0 and j < N);
}