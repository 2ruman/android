//
// Created by truman on 23. 7. 28..
//

#ifndef NATIVE_IMPL_H
#define NATIVE_IMPL_H

#include <string>
#include <vector>

typedef struct basic_data {
    int i_val_1;
    int i_val_2;
    long l_val_1;
    long l_val_2;
} basic_data_t;

class MyNative {
public:
    MyNative();
    std::vector<std::string> getAlphabetList();
    std::vector<basic_data_t> getDataList();
};

#endif //NATIVE_IMPL_H
