#define FUSE_USE_VERSION 26

#include <stdlib.h>
#include <fuse.h> /* contains definitions for basic functions needed to implement a filesystem */
#include <stdio.h>
#include <string.h>
#include <errno.h> /* contains definitions of error numbers */
#include <fcntl.h> /* contains definitions of file options (the ones used with fcntl() and open()) */
#include "jni/warrenfalk_fuselaj_Fuselaj.h"

JNIEXPORT jint JNICALL Java_warrenfalk_fuselaj_Fuselaj_initialize (JNIEnv *env, jobject obj, jobjectArray args) {
	int argc;
	const char **argv;
	int i;
	argc = (*env)->GetArrayLength(env, args);
	argv = (const char**)malloc(sizeof(char*) * argc);
	for (i = 0; i < argc; i++) {
		jstring str = (jstring)(*env)->GetObjectArrayElement(env, args, i);
		argv[i] = (*env)->GetStringUTFChars(env, str, 0);
	}
	printf("argc = %d\n", argc);
	for (i = 0; i < argc; i++) {
		printf("  arg %d: %s\n", i, argv[i]);
	}
	for (i = 0; i < argc; i++) {
		jstring str = (jstring)(*env)->GetObjectArrayElement(env, args, i);
		(*env)->ReleaseStringUTFChars(env, str, argv[i]);
	}
}
