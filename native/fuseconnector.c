#define FUSE_USE_VERSION 26

#include <stdlib.h>
#include <fuse.h> /* contains definitions for basic functions needed to implement a filesystem */
#include <stdio.h>
#include <string.h>
#include <errno.h> /* contains definitions of error numbers */
#include <fcntl.h> /* contains definitions of file options (the ones used with fcntl() and open()) */
#include "jni/warrenfalk_fuselaj_Fuselaj.h"

static const char *hello_str = "Hello World!\n";
static const char *hello_path = "/hello";

static int fuselaj_getattr(const char *path, struct stat *stbuf) {
	int res = 0; /* temporary result */
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

JNIEXPORT jint JNICALL Java_warrenfalk_fuselaj_Fuselaj_initialize (JNIEnv *env, jobject obj, jobjectArray args) {
	int argc;
	char **argv;
	int i;
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
	int res = fuse_main(argc, argv, &fuselaj_operations, NULL);
	for (i = 1; i < argc; i++) {
		jstring str = (jstring)(*env)->GetObjectArrayElement(env, args, i - 1);
		(*env)->ReleaseStringUTFChars(env, str, argv[i]);
	}
	return res;
}

