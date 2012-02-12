#define FUSE_USE_VERSION 26

#include <stdlib.h>
#include <fuse.h> /* contains definitions for basic functions needed to implement a filesystem */
#include <stdio.h>
#include <string.h>
#include <errno.h> /* contains definitions of error numbers */
#include <fcntl.h> /* contains definitions of file options (the ones used with fcntl() and open()) */
//#include "fuseconnector.h"
#include "../src-gen/warrenfalk_fuselaj_Fuselaj.h"

static const char *hello_str = "Hello World!\n";
static const char *hello_path = "/hello";

// Use the following structure for global variables
struct fuselaj_struct {
	JavaVM* jvm;
	jobject jvmfsimpl;
	jclass fsclass;
	jmethodID fs_readdir;
};
struct fuselaj_struct fuselaj = {
	.jvm = NULL,
	.jvmfsimpl = NULL,
	.fsclass = NULL,
	.fs_readdir = NULL,
};

// use the following structure for thread local storage
struct threadlocal_struct {
	const char *current_path;
	struct fuse_file_info *current_file_info;
};
__thread struct threadlocal_struct *threadlocal = NULL;

// This is used when attaching the native threads to the VM (see get_env())
static struct JavaVMAttachArgs jvm_args = {
	.version = JNI_VERSION_1_2,
	.name = NULL,
	.group = NULL,
};

// Get the JNI environment
static JNIEnv* get_env() {
	// Attach the native thread to the VM
	JNIEnv *env;
	(*fuselaj.jvm)->AttachCurrentThreadAsDaemon(fuselaj.jvm, (void**)&env, &jvm_args);
	return env;
}

static struct threadlocal_struct *get_threadlocal() {
	// if thread local has not been initialized yet, initialize it
	if (threadlocal == NULL) {
		threadlocal = (struct threadlocal_struct*)malloc(sizeof(struct threadlocal_struct));
		memset(threadlocal, 0, sizeof(struct threadlocal_struct));
	}
	return threadlocal;
}

static int fuselaj_getattr(const char *path, struct stat *stbuf) {
	int res = 0; /* temporary result */
	//current_path = path;
	memset(stbuf, 0, sizeof(struct stat));
	if (strcmp(path, "/") == 0) {
		stbuf->st_mode = S_IFDIR | 0755;
		stbuf->st_nlink = 2;
	}
	else if (strcmp(path, hello_path) == 0) {
		stbuf->st_mode = S_IFREG | 0444;
		stbuf->st_nlink = 1;
		stbuf->st_size = strlen(hello_str);
	}
	else {
		res = -ENOENT;
	}
	return res;
}

static int fuselaj_readdir(const char *path, void *buf, fuse_fill_dir_t filler, off_t offset, struct fuse_file_info *fi) {
	(void) offset;
	(void) fi;
	JNIEnv* env;
	env = get_env();
	struct threadlocal_struct *threadlocal = get_threadlocal();
	threadlocal->current_path = path;
	threadlocal->current_file_info = fi;

	(*env)->CallVoidMethod(env, fuselaj.jvmfsimpl, fuselaj.fs_readdir);

	if (strcmp(path, "/") != 0)
		return -ENOENT;
	filler(buf, ".", NULL, 0);
	filler(buf, "..", NULL, 0);
	filler(buf, hello_path + 1, NULL, 0);
	return 0;
}

static int fuselaj_open(const char *path, struct fuse_file_info *fi) {
	if (strcmp(path, hello_path) != 0)
		return -ENOENT;
	if ((fi->flags & 3) != O_RDONLY)
		return -EACCES;
	return 0;
}

static int fuselaj_read(const char* path, char *buf, size_t size, off_t offset, struct fuse_file_info *fi) {
	(void) fi;
	size_t len;
	//current_path = path;
	//current_fi = fi;
	if (strcmp(path, hello_path) != 0)
		return -ENOENT;
	len = strlen(hello_str);
	if (offset < len) {
		if (offset + size > len)
			size = len - offset;
		memcpy(buf, hello_str + offset, size);
	}
	else {
		size = 0;
	}
	return size;
}

static struct fuse_operations fuselaj_operations = {
	.getattr = fuselaj_getattr,
	.readdir = fuselaj_readdir,
	.open = fuselaj_open,
	.read = fuselaj_read,
};

jobject to_global(JNIEnv *env, jobject obj) {
	jobject global = (*env)->NewGlobalRef(env, obj);
	(*env)->DeleteLocalRef(env, obj);
	return global;
}

JNIEXPORT jstring JNICALL Java_warrenfalk_fuselaj_Fuselaj_getCurrentPath (JNIEnv *env, jclass clss) {
	struct threadlocal_struct *threadlocal = get_threadlocal();
	const char *path = threadlocal->current_path;
	if (path == NULL)
		return NULL;
	return to_global(env, (*env)->NewStringUTF(env, path));
}

JNIEXPORT jint JNICALL Java_warrenfalk_fuselaj_Fuselaj_initialize (JNIEnv *env, jobject obj, jobject fsimpl, jobjectArray args) {
	int argc;
	char **argv;
	int i;

	// remember the reference to the filesystem instance
	fuselaj.jvmfsimpl = to_global(env, fsimpl);

	// get a reference to the JVM
	(*env)->GetJavaVM(env, &fuselaj.jvm);

	// get the methods of the Filesystem class
	fuselaj.fsclass = to_global(env, (*env)->FindClass(env, "warrenfalk/fuselaj/Filesystem"));
	fuselaj.fs_readdir = (*env)->GetMethodID(env, fuselaj.fsclass, "readdir", "()V");

	// copy the args list from JVM to a native args list
	argc = (*env)->GetArrayLength(env, args) + 1;
	argv = (char**)malloc(sizeof(char*) * argc);
	argv[0] = "";
	for (i = 1; i < argc; i++) {
		jstring str = (jstring)(*env)->GetObjectArrayElement(env, args, i - 1);
		argv[i] = (char*)(*env)->GetStringUTFChars(env, str, 0);
	}
	printf("argc = %d\n", argc);
	for (i = 0; i < argc; i++) {
		printf("  arg %d: %s\n", i, argv[i]);
	}
	fflush(stdout);
	int res = fuse_main(argc, argv, &fuselaj_operations, NULL);
	for (i = 1; i < argc; i++) {
		jstring str = (jstring)(*env)->GetObjectArrayElement(env, args, i - 1);
		(*env)->ReleaseStringUTFChars(env, str, argv[i]);
	}
	return res;
}

