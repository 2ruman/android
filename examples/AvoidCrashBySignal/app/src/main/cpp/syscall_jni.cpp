
#include <android/log.h>
#include <jni.h>
#include <unistd.h>

#include <csignal>
#include <string>

#define LOG_TAG "SYSCALL_JNI"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

static void signal_handler(int signal, siginfo_t*, void*) {
    LOGD("I got a signal! (%d)", signal);
}

static void register_handler_for_signal(int __signal) {
    struct sigaction action;
    memset(&action, 0, sizeof(struct sigaction));
    action.sa_sigaction = signal_handler;
    action.sa_flags = SA_SIGINFO;
    sigaction(__signal, &action, NULL);
}

static void unregister_handler_for_signal(int __signal) {
    signal(__signal, SIG_DFL);
}

extern "C"
JNIEXPORT jint JNICALL
Java_truman_android_example_avoid_1crash_1by_1signal_SystemCall_nativeSetUid(JNIEnv *env,
                                                                             jclass clazz,
                                                                             jint uid) {
    LOGD("setuid(%d)", uid);

    register_handler_for_signal(SIGSYS);

    return setuid(uid);
}

extern "C"
JNIEXPORT jint JNICALL
Java_truman_android_example_avoid_1crash_1by_1signal_SystemCall_nativeSetUidUnhandled(JNIEnv *env,
                                                                                      jclass clazz,
                                                                                      jint uid) {
    LOGD("setuid(%d)", uid);

    unregister_handler_for_signal(SIGSYS);

    return setuid(uid);
}