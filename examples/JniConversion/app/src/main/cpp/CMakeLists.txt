
cmake_minimum_required(VERSION 3.18.1)

project("jni_conv")

set( TAG "JNI_CONV :: " )

if( ${CMAKE_BUILD_TYPE} STREQUAL "Debug" )
    add_definitions( -DDEBUG )
    message( ${TAG} "Debug Build")
else()
    message( ${TAG} "Not Debug Build")
endif()

include_directories( ${CMAKE_CURRENT_SOURCE_DIR} )
message( ${TAG} "Current source directory = " ${CMAKE_CURRENT_SOURCE_DIR})

add_library(
        jni_conv
        SHARED
        jni_conv.cpp )

find_library(
        log-lib
        log )

target_link_libraries(
        jni_conv

        native_impl
        ${log-lib} )

add_library(
        native_impl
        SHARED
        native_impl.cpp)
