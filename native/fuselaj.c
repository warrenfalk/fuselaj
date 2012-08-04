#define FUSE_USE_VERSION 28

#include <stdlib.h>
#include <time.h>
#include <fuse.h> /* contains definitions for basic functions needed to implement a filesystem */
#include <stdio.h>
#include <string.h>
#include <errno.h> /* contains definitions of error numbers */
#include <fcntl.h> /* contains definitions of file options (the ones used with fcntl() and open()) */
#include <unistd.h>
//#include "fuseconnector.h"
#include "../src-gen/warrenfalk_fuselaj_FuselajFs.h"
#include "../src-gen/warrenfalk_fuselaj_DirBuffer.h"
#include "../src-gen/warrenfalk_fuselaj_DirBufferClass.h"
#include "../src-gen/warrenfalk_fuselaj_FuselajFsClass.h"

#define WRAPBUFFER(buf,size) (*env)->NewDirectByteBuffer(env, (void*)buf, size)
#define WRAPSTRUCT(buf,type) WRAPBUFFER(buf,sizeof(type))
#define JSTRING(str) (str ? (*env)->NewStringUTF(env, str) : NULL)
#define DELETELOCAL(obj) if (obj) (*env)->DeleteLocalRef(env, obj)

// Use the following structure for global variables
struct fuselaj_struct {
	JavaVM* jvm;
	jobject jvmfsimpl;
};
struct fuselaj_struct fuselaj = {
	.jvm = NULL,
	.jvmfsimpl = NULL,
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

jobject to_global(JNIEnv *env, jobject obj) {
	jobject global = (*env)->NewGlobalRef(env, obj);
	DELETELOCAL(obj);
	return global;
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
	JNIEnv* env;
	env = get_env();

	memset(stbuf, 0, sizeof(struct stat)); // zeroing is almost always done, so do it on native side for speed
	jstring jpath = JSTRING(path);
	jobject stat = WRAPSTRUCT(stbuf, struct stat);
	jint jret = FuselajFs_call__getattr(env, fuselaj.jvmfsimpl, jpath, stat);
	DELETELOCAL(jpath);
	DELETELOCAL(stat);

	return jret;
}

static int fuselaj_readdir(const char *path, void *buf, fuse_fill_dir_t filler, off_t offset, struct fuse_file_info *fi) {
	(void) offset;
	(void) fi;

	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject db = DirBuffer_Create(env, (jlong)buf, (jlong)filler, offset);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__readdir(env, fuselaj.jvmfsimpl, jpath, db, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(db);
	DELETELOCAL(jfi);

	return jret;
}

static int fuselaj_open(const char *path, struct fuse_file_info *fi) {
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__open(env, fuselaj.jvmfsimpl, jpath, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}

static int fuselaj_read(const char* path, char *buf, size_t size, off_t offset, struct fuse_file_info *fi) {
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject fb = WRAPBUFFER(buf, size);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__read(env, fuselaj.jvmfsimpl, jpath, jfi, fb, offset);
	DELETELOCAL(jpath);
	DELETELOCAL(fb);
	DELETELOCAL(jfi);

	return jret;
}

static int fuselaj_mkdir(const char* path, mode_t mode) {
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__mkdir(env, fuselaj.jvmfsimpl, jpath, mode);
	DELETELOCAL(jpath);

	return jret;
}

void* fuselaj_init(struct fuse_conn_info *conn){
	JNIEnv* env;
	env = get_env();

	jobject jconn = WRAPSTRUCT(conn, struct fuse_conn_info);
	jobject jprivdata = FuselajFs_call__init(env, fuselaj.jvmfsimpl, jconn);
	DELETELOCAL(jconn);

	if (jprivdata != NULL)
		return (void*)to_global(env, jprivdata);
	return NULL;
}

void fuselaj_destroy(void* private_data){
	JNIEnv* env;
	env = get_env();

	FuselajFs_call__destroy(env, fuselaj.jvmfsimpl, (jobject)private_data);
}

int fuselaj_fgetattr(const char *path, struct stat *stbuf, struct fuse_file_info *fi){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jstat = WRAPSTRUCT(stbuf, struct stat);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__fgetattr(env, fuselaj.jvmfsimpl, jpath, jstat, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jstat);
	DELETELOCAL(jfi);

	return jret;
}

int fuselaj_access(const char* path, int mask){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__access(env, fuselaj.jvmfsimpl, jpath, mask);
	DELETELOCAL(jpath);

	return jret;
}

int fuselaj_readlink(const char* path, char* buf, size_t size){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject bb = WRAPBUFFER(buf, size * sizeof(char));
	jint jret = FuselajFs_call__readlink(env, fuselaj.jvmfsimpl, jpath, bb);
	DELETELOCAL(jpath);
	DELETELOCAL(bb);

	return jret;
}

int fuselaj_opendir(const char* path, struct fuse_file_info* fi){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__opendir(env, fuselaj.jvmfsimpl, jpath, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}

int fuselaj_mknod(const char* path, mode_t mode, dev_t rdev){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__mknod(env, fuselaj.jvmfsimpl, jpath, mode, rdev);
	DELETELOCAL(jpath);

	return jret;
}

int fuselaj_unlink(const char* path){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__unlink(env, fuselaj.jvmfsimpl, jpath);
	DELETELOCAL(jpath);

	return jret;
}

int fuselaj_rmdir(const char* path){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__rmdir(env, fuselaj.jvmfsimpl, jpath);
	DELETELOCAL(jpath);

	return jret;
}

int fuselaj_symlink(const char* oldpath, const char* newpath){
	JNIEnv* env;
	env = get_env();

	jstring joldpath = JSTRING(oldpath);
	jstring jnewpath = JSTRING(newpath);
	jint jret = FuselajFs_call__symlink(env, fuselaj.jvmfsimpl, joldpath, jnewpath);
	DELETELOCAL(joldpath);
	DELETELOCAL(jnewpath);

	return jret;
}

int fuselaj_rename(const char* from, const char* to){
	JNIEnv* env;
	env = get_env();

	jstring jfrom = JSTRING(from);
	jstring jto = JSTRING(to);
	jint jret = FuselajFs_call__rename(env, fuselaj.jvmfsimpl, jfrom, jto);
	DELETELOCAL(jfrom);
	DELETELOCAL(jto);

	return jret;
}

int fuselaj_link(const char* from, const char* to){
	JNIEnv* env;
	env = get_env();

	jstring jfrom = JSTRING(from);
	jstring jto = JSTRING(to);
	jint jret = FuselajFs_call__link(env, fuselaj.jvmfsimpl, jfrom, jto);
	DELETELOCAL(jfrom);
	DELETELOCAL(jto);

	return jret;
}

int fuselaj_chmod(const char* path, mode_t mode){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__chmod(env, fuselaj.jvmfsimpl, jpath, mode);
	DELETELOCAL(jpath);

	return jret;
}

int fuselaj_chown(const char* path, uid_t uid, gid_t gid){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__chown(env, fuselaj.jvmfsimpl, jpath, uid, gid);
	DELETELOCAL(jpath);

	return jret;
}

int fuselaj_truncate(const char* path, off_t size){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__truncate(env, fuselaj.jvmfsimpl, jpath, size);
	DELETELOCAL(jpath);

	return jret;
}

int fuselaj_ftruncate(const char* path, off_t size, struct fuse_file_info *fi){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__ftruncate(env, fuselaj.jvmfsimpl, jpath, size, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}

int fuselaj_utimens(const char* path, const struct timespec ts[2]){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__utimens(env, fuselaj.jvmfsimpl, jpath, ts[0].tv_sec, ts[0].tv_nsec, ts[1].tv_sec, ts[1].tv_nsec);
	DELETELOCAL(jpath);

	return jret;
}

int fuselaj_write(const char* path, const char *buf, size_t size, off_t offset, struct fuse_file_info *fi){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject fb = WRAPBUFFER(buf, size);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__write(env, fuselaj.jvmfsimpl, jpath, jfi, fb, offset);
	DELETELOCAL(jpath);
	DELETELOCAL(fb);
	DELETELOCAL(jfi);

	return jret;
}

int fuselaj_statfs(const char* path, struct statvfs* stbuf){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject statvfs = WRAPSTRUCT(stbuf, struct statvfs);
	jint jret = FuselajFs_call__statfs(env, fuselaj.jvmfsimpl, jpath, statvfs);
	DELETELOCAL(jpath);
	DELETELOCAL(statvfs);

	return jret;
}

int fuselaj_release(const char* path, struct fuse_file_info *fi){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__release(env, fuselaj.jvmfsimpl, jpath, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}

int fuselaj_releasedir(const char* path, struct fuse_file_info *fi){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__releasedir(env, fuselaj.jvmfsimpl, jpath, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}

int fuselaj_fsync(const char* path, int isdatasync, struct fuse_file_info* fi){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__fsync(env, fuselaj.jvmfsimpl, jpath, isdatasync, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}

int fuselaj_fsyncdir(const char* path, int isdatasync, struct fuse_file_info* fi){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__fsyncdir(env, fuselaj.jvmfsimpl, jpath, isdatasync, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}

int fuselaj_flush(const char* path, struct fuse_file_info* fi){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__flush(env, fuselaj.jvmfsimpl, jpath, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}

int fuselaj_lock(const char* path, struct fuse_file_info* fi, int cmd, struct flock* locks){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jobject jlocks = WRAPSTRUCT(locks, struct flock*);
	jint jret = FuselajFs_call__lock(env, fuselaj.jvmfsimpl, jpath, jfi, cmd, jlocks);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);
	DELETELOCAL(jlocks);

	return jret;
}

int fuselaj_bmap(const char* path, size_t blocksize, uint64_t* blockno){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jblockno = WRAPSTRUCT(blockno, uint64_t);
	jint jret = FuselajFs_call__bmap(env, fuselaj.jvmfsimpl, jpath, blocksize, jblockno);
	DELETELOCAL(jpath);
	DELETELOCAL(jblockno);

	return jret;
}

int fuselaj_setxattr(const char* path, const char* name, const char* value, size_t size, int flags){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jstring jname = JSTRING(name);
	jobject jvalue = WRAPSTRUCT(value, size);
	jint jret = FuselajFs_call__setxattr(env, fuselaj.jvmfsimpl, jpath, jname, jvalue, size, flags);
	DELETELOCAL(jpath);
	DELETELOCAL(jname);
	DELETELOCAL(jvalue);

	return jret;
}

int fuselaj_getxattr(const char* path, const char* name, char* value, size_t size){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jstring jname = JSTRING(name);
	jobject jvalue = WRAPBUFFER(value, size);
	jint jret = FuselajFs_call__getxattr(env, fuselaj.jvmfsimpl, jpath, jname, jvalue);
	DELETELOCAL(jpath);
	DELETELOCAL(jname);
	DELETELOCAL(jvalue);

	return jret;
}

int fuselaj_listxattr(const char* path, char* list, size_t size){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jlist = WRAPBUFFER(list, size * sizeof(char));
	jint jret = FuselajFs_call__listxattr(env, fuselaj.jvmfsimpl, jpath, jlist);
	DELETELOCAL(jpath);
	DELETELOCAL(jlist);

	return jret;
}

int fuselaj_removexattr(const char* path, const char* list){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jlist = JSTRING(list);
	jint jret = FuselajFs_call__removexattr(env, fuselaj.jvmfsimpl, jpath, jlist);
	DELETELOCAL(jpath);
	DELETELOCAL(jlist);

	return jret;
}

int fuselaj_create(const char * path, mode_t mode, struct fuse_file_info *fi) {
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__create(env, fuselaj.jvmfsimpl, jpath, mode, jfi);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}

/*
int fuselaj_ioctl(const char* path, int cmd, void* arg, struct fuse_file_info* fi, unsigned int flags, void* data){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jint jret = FuselajFs_call__ioctl(env, fuselaj.jvmfsimpl, jpath, cmd, ...);
	DELETELOCAL(jpath);

	return jret;
}
*/

/*
int fuselaj_poll(const char* path, struct fuse_file_info* fi, struct fuse_pollhandle* ph, unsigned* reventsp){
	JNIEnv* env;
	env = get_env();

	jstring jpath = JSTRING(path);
	jobject jfi = WRAPSTRUCT(fi, struct fuse_file_info);
	jint jret = FuselajFs_call__poll(env, fuselaj.jvmfsimpl, jpath, jfi, ...);
	DELETELOCAL(jpath);
	DELETELOCAL(jfi);

	return jret;
}
*/


static struct fuse_operations fuselaj_operations = {
};

static int is_implemented(JNIEnv *env, jobject obj, const char* name) {
	jstring jname = JSTRING(name);
	jboolean rval = FuselajFs_call_isImplemented(env, obj, jname);
	if (rval)
		printf("  -> %s\n", name);
	DELETELOCAL(jname);
	return rval;
}

JNIEXPORT void JNICALL Java_warrenfalk_fuselaj_FuselajFs_initialize (JNIEnv *env, jobject obj) {

	// get a reference to the JVM
	(*env)->GetJavaVM(env, &fuselaj.jvm);

	// remember the reference to the filesystem instance
	fuselaj.jvmfsimpl = to_global(env, obj);

	// initialize the native wrappers for the FuselajFs class
	FuselajFs_ClassInitialize(env);

	// initialize the native wrappers for the DirBuffer class
	DirBuffer_ClassInitialize(env);

	fuselaj_operations.getattr = is_implemented(env, fuselaj.jvmfsimpl, "getattr") ? fuselaj_getattr : 0;
	fuselaj_operations.readdir = is_implemented(env, fuselaj.jvmfsimpl, "readdir") ? fuselaj_readdir : 0;
	fuselaj_operations.open = is_implemented(env, fuselaj.jvmfsimpl, "open") ? fuselaj_open : 0;
	fuselaj_operations.read = is_implemented(env, fuselaj.jvmfsimpl, "read") ? fuselaj_read : 0;
	fuselaj_operations.mkdir = is_implemented(env, fuselaj.jvmfsimpl, "mkdir") ? fuselaj_mkdir : 0;
	fuselaj_operations.init = is_implemented(env, fuselaj.jvmfsimpl, "init") ? fuselaj_init : 0;
	fuselaj_operations.destroy = is_implemented(env, fuselaj.jvmfsimpl, "destroy") ? fuselaj_destroy : 0;
	fuselaj_operations.fgetattr = is_implemented(env, fuselaj.jvmfsimpl, "fgetattr") ? fuselaj_fgetattr : 0;
	fuselaj_operations.access = is_implemented(env, fuselaj.jvmfsimpl, "access") ? fuselaj_access : 0;
	fuselaj_operations.readlink = is_implemented(env, fuselaj.jvmfsimpl, "readlink") ? fuselaj_readlink : 0;
	fuselaj_operations.opendir = is_implemented(env, fuselaj.jvmfsimpl, "opendir") ? fuselaj_opendir : 0;
	fuselaj_operations.mknod = is_implemented(env, fuselaj.jvmfsimpl, "mknod") ? fuselaj_mknod : 0;
	fuselaj_operations.unlink = is_implemented(env, fuselaj.jvmfsimpl, "unlink") ? fuselaj_unlink : 0;
	fuselaj_operations.rmdir = is_implemented(env, fuselaj.jvmfsimpl, "rmdir") ? fuselaj_rmdir : 0;
	fuselaj_operations.symlink = is_implemented(env, fuselaj.jvmfsimpl, "symlink") ? fuselaj_symlink : 0;
	fuselaj_operations.rename = is_implemented(env, fuselaj.jvmfsimpl, "rename") ? fuselaj_rename : 0;
	fuselaj_operations.link = is_implemented(env, fuselaj.jvmfsimpl, "link") ? fuselaj_link : 0;
	fuselaj_operations.chmod = is_implemented(env, fuselaj.jvmfsimpl, "chmod") ? fuselaj_chmod : 0;
	fuselaj_operations.chown = is_implemented(env, fuselaj.jvmfsimpl, "chown") ? fuselaj_chown : 0;
	fuselaj_operations.truncate = is_implemented(env, fuselaj.jvmfsimpl, "truncate") ? fuselaj_truncate : 0;
	fuselaj_operations.ftruncate = is_implemented(env, fuselaj.jvmfsimpl, "ftruncate") ? fuselaj_ftruncate : 0;
	fuselaj_operations.utimens = is_implemented(env, fuselaj.jvmfsimpl, "utimens") ? fuselaj_utimens : 0;
	fuselaj_operations.write = is_implemented(env, fuselaj.jvmfsimpl, "write") ? fuselaj_write : 0;
	fuselaj_operations.statfs = is_implemented(env, fuselaj.jvmfsimpl, "statfs") ? fuselaj_statfs : 0;
	fuselaj_operations.release = is_implemented(env, fuselaj.jvmfsimpl, "release") ? fuselaj_release : 0;
	fuselaj_operations.releasedir = is_implemented(env, fuselaj.jvmfsimpl, "releasedir") ? fuselaj_releasedir : 0;
	fuselaj_operations.fsync = is_implemented(env, fuselaj.jvmfsimpl, "fsync") ? fuselaj_fsync : 0;
	fuselaj_operations.fsyncdir = is_implemented(env, fuselaj.jvmfsimpl, "fsyncdir") ? fuselaj_fsyncdir : 0;
	fuselaj_operations.flush = is_implemented(env, fuselaj.jvmfsimpl, "flush") ? fuselaj_flush : 0;
	fuselaj_operations.lock = is_implemented(env, fuselaj.jvmfsimpl, "lock") ? fuselaj_lock : 0;
	fuselaj_operations.bmap = is_implemented(env, fuselaj.jvmfsimpl, "bmap") ? fuselaj_bmap : 0;
	fuselaj_operations.setxattr = is_implemented(env, fuselaj.jvmfsimpl, "setxattr") ? fuselaj_setxattr : 0;
	fuselaj_operations.getxattr = is_implemented(env, fuselaj.jvmfsimpl, "getxattr") ? fuselaj_getxattr : 0;
	fuselaj_operations.listxattr = is_implemented(env, fuselaj.jvmfsimpl, "listxattr") ? fuselaj_listxattr : 0;
	fuselaj_operations.removexattr = is_implemented(env, fuselaj.jvmfsimpl, "removexattr") ? fuselaj_removexattr : 0;
	fuselaj_operations.create = is_implemented(env, fuselaj.jvmfsimpl, "create") ? fuselaj_create : 0;
	//fuselaj_operations.ioctl = is_implemented(env, fuselaj.jvmfsimpl, "ioctl") ? fuselaj_ioctl : 0;
	//fuselaj_operations.poll = is_implemented(env, fuselaj.jvmfsimpl, "poll") ? fuselaj_poll : 0;

	fuselaj_operations.flag_nullpath_ok = FuselajFs_get_nullPathsOk(env, fuselaj.jvmfsimpl);

}

JNIEXPORT jint JNICALL Java_warrenfalk_fuselaj_FuselajFs_fuse_1main (JNIEnv *env, jobject obj, jobjectArray args) {
	int argc;
	char **argv;
	int i;

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

JNIEXPORT jboolean JNICALL Java_warrenfalk_fuselaj_DirBuffer_putDir__Ljava_lang_String_2JIJ (JNIEnv *env, jobject obj, jstring name, jlong inode, jint mode, jlong position) {
	void *buf = (void*)DirBuffer_get_buffer(env, obj);
	fuse_fill_dir_t filler = (fuse_fill_dir_t)DirBuffer_get_filler(env, obj);

	const char *sname = (*env)->GetStringUTFChars(env, name, NULL);
	struct stat st;
	memset(&st, 0, sizeof(struct stat));
	st.st_ino = inode;
	st.st_mode = mode;
	int res = filler(buf, sname, &st, position);
	(*env)->ReleaseStringUTFChars(env, name, sname);

	DirBuffer_set_position(env, obj, position);

	return res;
}


JNIEXPORT jboolean JNICALL Java_warrenfalk_fuselaj_DirBuffer_putDir__Ljava_lang_String_2J (JNIEnv *env, jobject obj, jstring name, jlong position) {
	void *buf = (void*)DirBuffer_get_buffer(env, obj);
	fuse_fill_dir_t filler = (fuse_fill_dir_t)DirBuffer_get_filler(env, obj);

	const char *sname = (*env)->GetStringUTFChars(env, name, NULL);
	int res = filler(buf, sname, NULL, position);
	(*env)->ReleaseStringUTFChars(env, name, sname);

	DirBuffer_set_position(env, obj, position);

	return res;
}


JNIEXPORT jobject JNICALL Java_warrenfalk_fuselaj_FuselajFs_getCurrentContext (JNIEnv *env, jclass fsclass) {
	struct fuse_context *context = fuse_get_context();
	jobject jcontext = to_global(env, WRAPSTRUCT(context, struct fuse_context));
	return jcontext;
}


JNIEXPORT jobject JNICALL Java_warrenfalk_fuselaj_FuselajFs_toObject (JNIEnv *env, jclass fsclass, jlong pointer) {
	return (jobject)pointer;
}

JNIEXPORT jint JNICALL Java_warrenfalk_fuselaj_FuselajFs__1os_1stat (JNIEnv *env, jclass fsclass, jstring path, jobject buf) {
	const char *spath = (*env)->GetStringUTFChars(env, path, NULL);
	void* statbuf = (*env)->GetDirectBufferAddress(env, buf);
	int rval = stat(spath, (struct stat*)statbuf);
	(*env)->ReleaseStringUTFChars(env, path, spath);

	return rval;
}

JNIEXPORT jint JNICALL Java_warrenfalk_fuselaj_FuselajFs__1os_1mkdir (JNIEnv *env, jclass fsclass, jstring path, jint mode) {
	const char *spath = (*env)->GetStringUTFChars(env, path, NULL);
	int rval = mkdir(spath, mode);
	(*env)->ReleaseStringUTFChars(env, path, spath);

	return rval;
}
