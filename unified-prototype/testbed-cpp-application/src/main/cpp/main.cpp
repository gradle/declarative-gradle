#include <iostream>
#include <cpp-util.h>

using namespace std;

string cppVersion() {
    if (__cplusplus == 202002L) {
        return "C++20";
    } else if (__cplusplus == 201703L) {
        return "C++17";
    } else {
        return "unknown C++ version";
    }
}

string os() {
#ifdef __MACH__
    return "macOS";
#else
    return "unknown OS"
#endif
}

int main() {
    cout << "Hello from " << cppVersion() << " on " << os() << endl;
    cout << Util().message() << endl;
    return 0;
}
