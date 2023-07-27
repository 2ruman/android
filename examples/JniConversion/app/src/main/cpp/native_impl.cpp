
#include "native_impl.h"

MyNative::MyNative() {

}

std::vector<std::string> MyNative::getAlphabetList() {
    std::vector<std::string> result;
    result.emplace_back("ABC");
    result.emplace_back("DEF");
    result.emplace_back("GHI");
    result.emplace_back("JKL");
    result.emplace_back("MNO");
    result.emplace_back("PQR");
    result.emplace_back("STU");
    result.emplace_back("VWX");
    result.emplace_back("YZ");
    return result;
}

std::vector<basic_data_t> MyNative::getDataList() {
    std::vector<basic_data_t> result {
        { 1, 2, 1000L, 2000L },
        { 3, 4, 3000L, 4000L },
        { 5, 6, 5000L, 6000L },
        { 7, 8, 7000L, 8000L },
        { 9, -1, 9000L, -1000L },
    };
    return result;
}