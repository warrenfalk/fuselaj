#define FUSE_USE_VERSION 26

#include <stdlib.h>
#include <fuse.h> /* contains definitions for basic functions needed to implement a filesystem */
#include <stdio.h>
#include <string.h>
#include <errno.h> /* contains definitions of error numbers */
#include <fcntl.h> /* contains definitions of file options (the ones used with fcntl() and open()) */
//#include "fuseconnector.h"
#include "../src-gen/warrenfalk_fuselaj_Fuselaj.h"
#include "../src-gen/warrenfalk_fuselaj_DirBuffer.h"

static const char *hello_str = "Hello World!\n";
static const char *hello_path = "/hello";

// Use the following structure for global variables
struct fuselaj_struct {
	JavaVM* jvm;
	jobject jvmfsimpl;
	jclass fsclass;
	jmethodID fs_getattr;
	jmethodID fs_readdir;
	jmethodID fs_open;
	jmethodID fs_read;
	jclass dirbufferclass;
	jmethodID dirbuffer_ctor;
	jfieldID dirbuffer_buffer;
	jfieldID dirbuffer_filler;
	jfieldID dirbuffer_position;
	jclass statclass;
	jmethodID stat_ctor;
	jclass fileinfoclass;
	jmethodID fileinfo_ctor;
};
struct fuselaj_struct fuselaj = {
	.jvm = NULL,
	.jvmfsimpl = NULL,
	.fsclass = NULL,
	.fs_getattr = NULL,
	.fs_readdir = NULL,
	.statclass = NULL,
	.stat_ctor = NULL,
};

// use the following structure for thread local storage
struct threadlocal_struct {
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

	JNIEnv* env;
	env = get_env();

	memset(stbuf, 0, sizeof(struct stat)); // zeroing is almost always done, so do it on native side for speed
	jstring jpath = (*env)->NewStringUTF(env, path);
	jobject bb = (*env)->NewDirectByteBuffer(env, (void*)stbuf, sizeof(struct stat));
	jobject stat = (*env)->NewObject(env, fuselaj.statclass, fuselaj.stat_ctor, bb);
	jint jret = (*env)->CallIntMethod(env, fuselaj.jvmfsimpl, fuselaj.fs_getattr, jpath, stat);
	(*env)->DeleteLocalRef(env, jpath);
	(*env)->DeleteLocalRef(env, stat);
	(*env)->DeleteLocalRef(env, bb);

	return jret;
}

static int fuselaj_readdir(const char *path, void *buf, fuse_fill_dir_t filler, off_t offset, struct fuse_file_info *fi) {
	(void) offset;
	(void) fi;

	JNIEnv* env;
	env = get_env();

	jstring jpath = (*env)->NewStringUTF(env, path);
	jobject db = (*env)->NewObject(env, fuselaj.dirbufferclass, fuselaj.dirbuffer_ctor, buf, filler, offset);
	jobject bb = (*env)->NewDirectByteBuffer(env, (void*)fi, sizeof(struct fuse_file_info));
	jobject jfi = (*env)->NewObject(env, fuselaj.fileinfoclass, fuselaj.fileinfo_ctor, bb);
	jint jret = (*env)->CallIntMethod(env, fuselaj.jvmfsimpl, fuselaj.fs_readdir, jpath, db, jfi);
	(*env)->DeleteLocalRef(env, jpath);
	(*env)->DeleteLocalRef(env, db);
	(*env)->DeleteLocalRef(env, jfi);
	(*env)->DeleteLocalRef(env, bb);

	return jret;
}

static int fuselaj_open(const char *path, struct fuse_file_info *fi) {
	JNIEnv* env;
	env = get_env();

	jstring jpath = (*env)->NewStringUTF(env, path);
	jobject bb = (*env)->NewDirectByteBuffer(env, (void*)fi, sizeof(struct fuse_file_info));
	jobject jfi = (*env)->NewObject(env, fuselaj.fileinfoclass, fuselaj.fileinfo_ctor, bb);
	jint jret = (*env)->CallIntMethod(env, fuselaj.jvmfsimpl, fuselaj.fs_open, jpath, jfi);
	(*env)->DeleteLocalRef(env, jpath);
	(*env)->DeleteLocalRef(env, jfi);
	(*env)->DeleteLocalRef(env, bb);

	return jret;
}

static int fuselaj_read(const char* path, char *buf, size_t size, off_t offset, struct fuse_file_info *fi) {
	JNIEnv* env;
	env = get_env();

	jstring jpath = (*env)->NewStringUTF(env, path);
	jobject fb = (*env)->NewDirectByteBuffer(env, (void*)buf, size);
	jobject bb = (*env)->NewDirectByteBuffer(env, (void*)fi, sizeof(struct fuse_file_info));
	jobject jfi = (*env)->NewObject(env, fuselaj.fileinfoclass, fuselaj.fileinfo_ctor, bb);
	jint jret = (*env)->CallIntMethod(env, fuselaj.jvmfsimpl, fuselaj.fs_read, jpath, jfi, fb, offset);
	(*env)->DeleteLocalRef(env, jpath);
	(*env)->DeleteLocalRef(env, fb);
	(*env)->DeleteLocalRef(env, jfi);
	(*env)->DeleteLocalRef(env, bb);

	return jret;
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
	fuselaj.fs_getattr = (*env)->GetMethodID(env, fuselaj.fsclass, "_getattr", "(Ljava/lang/String;Lwarrenfalk/fuselaj/Stat;)I");
	fuselaj.fs_readdir = (*env)->GetMethodID(env, fuselaj.fsclass, "_readdir", "(Ljava/lang/String;Lwarrenfalk/fuselaj/DirBuffer;Lwarrenfalk/fuselaj/FileInfo;)I");
	fuselaj.fs_open = (*env)->GetMethodID(env, fuselaj.fsclass, "_open", "(Ljava/lang/String;Lwarrenfalk/fuselaj/FileInfo;)I");
	fuselaj.fs_read = (*env)->GetMethodID(env, fuselaj.fsclass, "_read", "(Ljava/lang/String;Lwarrenfalk/fuselaj/FileInfo;Ljava/nio/ByteBuffer;J)I");

	// get the methods of the DirBuffer class
	fuselaj.dirbufferclass = to_global(env, (*env)->FindClass(env, "warrenfalk/fuselaj/DirBuffer"));
	fuselaj.dirbuffer_buffer = (*env)->GetFieldID(env, fuselaj.dirbufferclass, "buffer", "J");
	fuselaj.dirbuffer_filler = (*env)->GetFieldID(env, fuselaj.dirbufferclass, "filler", "J");
	fuselaj.dirbuffer_position = (*env)->GetFieldID(env, fuselaj.dirbufferclass, "position", "J");
	fuselaj.dirbuffer_ctor = (*env)->GetMethodID(env, fuselaj.dirbufferclass, "<init>", "(JJJ)V");

	// get the class and constructor for the structure classes
	// Stat
	fuselaj.statclass = to_global(env, (*env)->FindClass(env, "warrenfalk/fuselaj/Stat"));
	fuselaj.stat_ctor = (*env)->GetMethodID(env, fuselaj.statclass, "<init>", "(Ljava/nio/ByteBuffer;)V");
	// FileInfo
	fuselaj.fileinfoclass = to_global(env, (*env)->FindClass(env, "warrenfalk/fuselaj/FileInfo"));
	fuselaj.fileinfo_ctor = (*env)->GetMethodID(env, fuselaj.fileinfoclass, "<init>", "(Ljava/nio/ByteBuffer;)V");


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

JNIEXPORT jboolean JNICALL Java_warrenfalk_fuselaj_DirBuffer_putDir (JNIEnv *env, jobject obj, jstring name, jlong inode, jint mode, jlong position) {
	void *buf = (void*)(*env)->GetLongField(env, obj, fuselaj.dirbuffer_buffer);
	fuse_fill_dir_t filler = (fuse_fill_dir_t)(*env)->GetLongField(env, obj, fuselaj.dirbuffer_filler);

	// TODO: use inode and mode
	const char *sname = (*env)->GetStringUTFChars(env, name, NULL);
	int res = filler(buf, sname, NULL, position);
	(*env)->ReleaseStringUTFChars(env, name, sname);

	(*env)->SetLongField(env, obj, fuselaj.dirbuffer_position, position);

	return res;
}

