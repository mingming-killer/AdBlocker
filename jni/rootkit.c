/**
 * The native root kit jni interface.
 * @author humm <humm@dw.gdbbk.com>
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <unistd.h>

#include <jni.h>
#include <assert.h>

#include "log.h"

static void com_eebbk_rootkit_execlCommandWithRoot(JNIEnv* env, jobject thiz, jstring command) {
    int ret = -1;
	const char* utf_command = (*env)->GetStringUTFChars(env, command, NULL);
    LOG_D("execel command is %s \n", utf_command);

    // this jni just a make lib dir in /data/data/xx/, real execl in java
    //ret = execl("/data/local/tmp/run_root_shell", "run_root_shell", "-c", utf_command, NULL);
    //ret = execl("/system/bin/sh", "sh", "ls", "-l", NULL);
    //LOG_D("execel result is %n \n", ret);

	(*env)->ReleaseStringUTFChars(env, command, utf_command);
}


//=====================================================
//=====================================================

static JNINativeMethod g_methods[] = {
    {"execlCommandWithRoot",  "(Ljava/lang/String;)V",    (void *)com_eebbk_rootkit_execlCommandWithRoot},
};

static const char* const k_class_name =  "com/eebbk/rootkit/jni/RootKit";
static const int n_methods = (sizeof(g_methods) / sizeof(g_methods[0]));

// This function only registers the native methods
static int register_com_eebbk_rootkit(JNIEnv *env)
{
    jclass clazz;

    // look up the class 
    clazz = (*env)->FindClass(env, k_class_name);
    if (NULL == clazz) {
        LOG_E("Can't find class %s\n", k_class_name);
        return -1;
    }

    // register all the methods 
    int ret = (*env)->RegisterNatives(env, clazz, g_methods, n_methods); 
    if (JNI_OK != ret) {
        LOG_E("Failed registering methods for %s\n", k_class_name);
        return -1;
    }
    
    return ret;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    // the c code must use (*vm)->, if is cpp use vm-> directly
    //if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
    if ((*vm)->GetEnv(vm,(void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOG_E("ERROR: GetEnv failed\n");
        goto bail;
    }    
    assert(env != NULL);

    if (register_com_eebbk_rootkit(env) < 0) {
        LOG_E("ERROR: rootkit native registration failed\n");
        goto bail;
    }

    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

bail:
    return result;
}
