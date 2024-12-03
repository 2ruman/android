
#include <android/log.h>
#include <jni.h>
#include <unistd.h>

#include <csignal>
#include <string>

#define LOG_TAG "SYSCALL_JNI"
#include "signal_avoider.h"

extern "C"
JNIEXPORT jint JNICALL
Java_truman_android_example_avoid_1crash_1by_1signal_SystemCall_nativeSetUid(JNIEnv *env,
                                                                             jclass clazz,
                                                                             jint uid) {
    LOGD("setuid(%d)", uid);
    return avoid_sigsys_func(reinterpret_cast<func_I_I>(setuid), uid);
}

extern "C"
JNIEXPORT jint JNICALL
Java_truman_android_example_avoid_1crash_1by_1signal_SystemCall_nativeSetUidUnhandled(JNIEnv *env,
                                                                                      jclass clazz,
                                                                                      jint uid) {
    LOGD("setuid(%d)", uid);
    return setuid(uid);
}

extern "C"
JNIEXPORT jint JNICALL
Java_truman_android_example_avoid_1crash_1by_1signal_SystemCall_nativeSetReuid(JNIEnv *env,
                                                                               jclass clazz,
                                                                               jint ruid,
                                                                               jint euid) {
    LOGD("setreuid(%d, %d)", ruid, euid);
    return avoid_sigsys_func(reinterpret_cast<func_I_II>(setreuid), ruid, euid);
}

extern "C"
JNIEXPORT jint JNICALL
Java_truman_android_example_avoid_1crash_1by_1signal_SystemCall_nativeSetResuid(JNIEnv *env,
                                                                                jclass clazz,
                                                                                jint ruid,
                                                                                jint euid,
                                                                                jint suid) {
    LOGD("setresuid(%d, %d, %d)", ruid, euid, suid);
    return avoid_sigsys_func(reinterpret_cast<func_I_III>(setresuid), ruid, euid, suid);
}

extern "C"
JNIEXPORT jint JNICALL
Java_truman_android_example_avoid_1crash_1by_1signal_SystemCall_nativeSetResgid(JNIEnv *env,
                                                                                jclass clazz,
                                                                                jint rgid,
                                                                                jint egid,
                                                                                jint sgid) {
    LOGD("setresgid(%d, %d, %d)", rgid, egid, sgid);
    return avoid_sigsys_func(reinterpret_cast<func_I_III>(setresgid), rgid, egid, sgid);
}