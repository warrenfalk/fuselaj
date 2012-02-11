#define FUSE_USE_VERSION 26

#include <stdlib.h>
#include <fuse.h> /* contains definitions for basic functions needed to implement a filesystem */
#include <stdio.h>
#include <string.h>
#include <errno.h> /* contains definitions of error numbers */
#include <fcntl.h> /* contains definitions of file options (the ones used with fcntl() and open()) */
#include "jni/warrenfalk_fuselaj_Fuselaj.h"

JNIEXPORT void JNICALL Java_warrenfalk_fuselaj_Fuselaj_initialize (JNIEnv *env, jobject obj) {
	printf("Hello World\n");
}
