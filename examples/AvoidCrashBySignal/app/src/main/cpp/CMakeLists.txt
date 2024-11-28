cmake_minimum_required(VERSION 3.22.1)

project("syscall_jni")

add_library(${CMAKE_PROJECT_NAME} SHARED
        syscall_jni.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)