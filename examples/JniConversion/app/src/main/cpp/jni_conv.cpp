
#include <android/log.h>
#include <jni.h>
#include "native_impl.h"

#include <memory>
#include <mutex>
#include <string>

#define LOG_TAG "JniConversion-JNI" ".2ruman"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

namespace android {
namespace truman {

static std::mutex g_initialized_mutex;
static bool g_initialized;
static std::unique_ptr<MyNative> gMyNative;

static jclass jclass_java_util_ArrayList;
static jmethodID jctor_java_util_ArrayList;
static jmethodID jmethod_java_util_ArrayList_add;
static jmethodID jmethod_java_util_ArrayList_get;
static jmethodID jmethod_java_util_ArrayList_size;

static jclass jclass_BasicData;
static jmethodID jctor_BasicData;

#define RETURN_X_IF_NPTR(c, x) if ((c) == nullptr) {             \
                                   if (env->ExceptionCheck()) {  \
                                       env->ExceptionDescribe(); \
                                       env->ExceptionClear();    \
                                   }                             \
                                   return (x);                   \
                               }
#define RETURN_FALSE_IF_NPTR(c) RETURN_X_IF_NPTR((c), false)

static bool init_globals(JNIEnv *env) {
    std::lock_guard<std::mutex> guard(g_initialized_mutex);
    if (g_initialized) return true;

    RETURN_FALSE_IF_NPTR(jclass_java_util_ArrayList =
            static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/util/ArrayList"))));
    RETURN_FALSE_IF_NPTR(jctor_java_util_ArrayList =
                                 env->GetMethodID(jclass_java_util_ArrayList, "<init>", "(I)V"));
    RETURN_FALSE_IF_NPTR(jmethod_java_util_ArrayList_add =
                                 env->GetMethodID(jclass_java_util_ArrayList, "add", "(Ljava/lang/Object;)Z"));
    RETURN_FALSE_IF_NPTR(jmethod_java_util_ArrayList_get =
                                 env->GetMethodID(jclass_java_util_ArrayList, "get", "(I)Ljava/lang/Object;"));
    RETURN_FALSE_IF_NPTR(jmethod_java_util_ArrayList_size =
                                 env->GetMethodID(jclass_java_util_ArrayList, "size", "()I"));

    RETURN_FALSE_IF_NPTR(jclass_BasicData =
                                 static_cast<jclass>(env->NewGlobalRef(env->FindClass(
                                         "truman/android/example/jniconversion/BasicData"))));
    RETURN_FALSE_IF_NPTR(jctor_BasicData =
                                 env->GetMethodID(jclass_BasicData, "<init>", "(IIJJ)V"));

    gMyNative = std::make_unique<MyNative>();

    g_initialized = true;
    return g_initialized;
}

static std::vector<std::string> convStrListToVec(JNIEnv *env, jobject arrayList) {
    jint size = env->CallIntMethod(arrayList, jmethod_java_util_ArrayList_size);
    std::vector<std::string> result;
    result.reserve(size);

    for (int i = 0; i < size; i++) {
        jstring element = static_cast<jstring>(env->CallObjectMethod(arrayList, jmethod_java_util_ArrayList_get, i));
        const char* chars = env->GetStringUTFChars(element, nullptr);
        result.emplace_back(chars);
        env->ReleaseStringUTFChars(element, chars);
        env->DeleteLocalRef(element);
    }
    return result;
}

static jobject convStrVecToList(JNIEnv *env, std::vector<std::string> vec) {
    jobject result = env->NewObject(jclass_java_util_ArrayList, jctor_java_util_ArrayList, (jint) vec.size());
    for (std::string s: vec) {
        jstring element = env->NewStringUTF(s.c_str());
        env->CallBooleanMethod(result, jmethod_java_util_ArrayList_add, element);
        env->DeleteLocalRef(element);
    }
    return result;
}

static jobject convDataVecToList(JNIEnv *env, std::vector<basic_data_t> vec) {
    jobject result = env->NewObject(jclass_java_util_ArrayList, jctor_java_util_ArrayList, (jint) vec.size());
    for (auto d: vec) {
        jobject element = env->NewObject(jclass_BasicData, jctor_BasicData,
                                         d.i_val_1, d.i_val_2, d.l_val_1, d.l_val_2);
        env->CallBooleanMethod(result, jmethod_java_util_ArrayList_add, element);
        env->DeleteLocalRef(element);
    }
    return result;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_truman_android_example_jniconversion_MyImplNative_nativeGetWelcomeMessage(JNIEnv *env,
                                                                               jclass clazz) {
#ifdef DEBUG
    LOGD("%s", __func__);
#endif
    if (!g_initialized && !init_globals(env)) return nullptr;
    std::string hello = "Welcome to JNI world!";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jobject JNICALL
Java_truman_android_example_jniconversion_MyImplNative_nativeGetStrDataList(JNIEnv *env,
                                                                            jclass clazz) {
#ifdef DEBUG
    LOGD("%s", __func__);
#endif
    if (!g_initialized && !init_globals(env)) return nullptr;
    return convStrVecToList(env, gMyNative->getAlphabetList());
}

extern "C"
JNIEXPORT jobject JNICALL
Java_truman_android_example_jniconversion_MyImplNative_nativeGetDataList(JNIEnv *env,
                                                                         jclass clazz) {
#ifdef DEBUG
    LOGD("%s", __func__);
#endif
    if (!g_initialized && !init_globals(env)) return nullptr;
    return convDataVecToList(env, gMyNative->getDataList());
}

extern "C"
JNIEXPORT jint JNICALL
Java_truman_android_example_jniconversion_MyImplNative_nativePrintStrList(JNIEnv *env, jclass clazz,
                                                                       jobject list) {
#ifdef DEBUG
    LOGD("%s", __func__);
#endif
    if (!g_initialized && !init_globals(env)) return JNI_ERR;
    if (list == nullptr) return JNI_ERR;
    std::vector<std::string> result = convStrListToVec(env, list);
    for (std::string r : result) {
        LOGD("Conv : %s", r.c_str());
    }
    return JNI_OK;
}

} // namespace truman
} // namespace android