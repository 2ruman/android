#ifndef SIGNAL_AVOIDER_H
#define SIGNAL_AVOIDER_H

#include <android/log.h>

#include <csignal>
#include <cstring>
#include <functional>
#include <memory>

#ifndef LOG_TAG
#define LOG_TAG "SIGNAL_AVOIDER"
#endif

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

typedef int (* func_I_I)(int);
typedef int (* func_I_II)(int, int);
typedef int (* func_I_III)(int, int, int);

static std::function<void(int)> signal_callback = [](int signal) {
    LOGD("Signal(%d) ignored...", signal);
};

static void signal_handler(int signal, siginfo_t*, void*) {
    signal_callback(signal);
}

static void register_signal_handler(int __signal) {
    struct sigaction action;
    memset(&action, 0, sizeof(struct sigaction));
    action.sa_sigaction = signal_handler;
    action.sa_flags = SA_SIGINFO;
    sigaction(__signal, &action, NULL);
}

static void unregister_signal_handler(int __signal) {
    signal(__signal, SIG_DFL);
}

static int avoid_sigsys_func(func_I_I func, int i) {
    register_signal_handler(SIGSYS);
    int result = func(i);
    unregister_signal_handler(SIGSYS);
    return result;
}

static int avoid_sigsys_func(func_I_II func, int i, int ii) {
    register_signal_handler(SIGSYS);
    int result = func(i, ii);
    unregister_signal_handler(SIGSYS);
    return result;
}

static int avoid_sigsys_func(func_I_III func, int i, int ii, int iii) {
    register_signal_handler(SIGSYS);
    int result = func(i, ii, iii);
    unregister_signal_handler(SIGSYS);
    return result;
}

#endif // SIGNAL_AVOIDER_H
