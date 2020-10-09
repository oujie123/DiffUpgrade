#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" {
extern int executePatch(int argc, char *argv[]);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_gacrnd_gcs_diffupgrade_DiffUpgradeUtils_patch(JNIEnv *env, jclass type, jstring oldApk_,
                                                       jstring newApk_, jstring patch_) {
    const char *oldApk = env->GetStringUTFChars(oldApk_, 0);
    const char *newApk = env->GetStringUTFChars(newApk_, 0);
    const char *patch = env->GetStringUTFChars(patch_, 0);

    int args = 4;
    char *argv[args];
    argv[0] = (char *)"bspatch";
    argv[1] = (char *)oldApk;
    argv[2] = (char *)newApk;
    argv[3] = (char *)patch;

    int result = executePatch(args,argv);

    env->ReleaseStringUTFChars(oldApk_, oldApk);
    env->ReleaseStringUTFChars(newApk_, newApk);
    env->ReleaseStringUTFChars(patch_, patch);

    __android_log_print(ANDROID_LOG_ERROR,"Jack_Diff","oldApk:%s,newApk:%s,patch:%s,result:%d",argv[1],argv[2],argv[3],result);
    return result;
}