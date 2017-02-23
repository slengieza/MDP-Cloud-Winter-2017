#include <string>
#include <iostream>
#include <fstream>
#include <vector>
using namespace std;

int main (int argc, char ** argv) {
	fstream myfile;
	myfile.open (argv[1]);
	vector<string> v;
	string s;
	while(getline(myfile,s)){
		v.push_back(s);
	}
	myfile.close();
	s = argv[1];
	s = s.substr(0, s.length()-4);
	s += "_output.txt";
	myfile.open(s, ofstream::out | ofstream::trunc);
	for (auto line:v){
		string res;
		int year, month, day, hour, minute, sec;
		for (int i = 0; i < line.length(); ++i){
			if (line[i] == '['){
				res = line.substr(i+1,4) + " ";
				res += line.substr(i+6,2)+ " ";
				res += line.substr(i+9, 2)+ " ";
			}else if (line[i] == 'T'){
				res += line.substr(i+1,2) + " ";
				res += line.substr(i+4,2) + " ";
				res += line.substr(i+7,2) + " ";
			}else if(line[i] == ','){
				string data = line.substr(i+1);
				data.pop_back();
				res += data;
			}
		}
		myfile << res << endl;
	}
	myfile.close();
	return 0;
}