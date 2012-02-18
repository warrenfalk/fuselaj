#define FUSE_USE_VERSION 26

#include <stdlib.h>
#include <fuse.h> /* contains definitions for basic functions needed to implement a filesystem */
#include <stdio.h>
#include <string.h>
#include <errno.h> /* contains definitions of error numbers */
#include <fcntl.h> /* contains definitions of file options (the ones used with fcntl() and open()) */

typedef struct stat st;

#define fieldoffset(type, field) ((unsigned long) &(((type *) 0)->field))
#define fieldsize(type, field) sizeof(((type *) 0)->field)
#define dumpField(typename, name, type, field, caption) dumpfield(typename, name, fieldsize(type, field), fieldoffset(type, field), caption)

void dumpfield(const char *typename, const char *name, int size, long offset, const char *caption) {
	const char *ftype = "??";
	const char *type = "??";
	if (size == 4) {
		ftype = "Int";
		type = "int";
	}
	else if (size == 8) {
		ftype = "Long";
		type = "long";
	}
	printf("\t/**\n");
	printf("\t * Get %s\n", caption);
	printf("\t */\n");
	printf("\tpublic %s get%s() {\n", type, name);
	printf("\t\treturn bb.get%s(0x%lx);\n", ftype, offset);
	printf("\t}\n");
	printf("\t\n");
	printf("\t/**\n");
	printf("\t * Set %s\n", caption);
	printf("\t */\n");
	printf("\tpublic %s put%s(final %s value) {\n", typename, name, type);
	printf("\t\tbb.put%s(0x%lx, value);\n", ftype, offset);
	printf("\t\treturn this;\n");
	printf("\t}\n");
	printf("\t\n");
}

int main(int argc, char *argv[]) {

	printf("public class Stat {\n");
    dumpField("Stat", "Dev", struct stat, st_dev, "ID of device containing file");
    dumpField("Stat", "Inode", struct stat, st_ino, "inode number");
    dumpField("Stat", "Mode", struct stat, st_mode, "protection");
    dumpField("Stat", "LinkCount", struct stat, st_nlink, "number of hard links");
    dumpField("Stat", "UserId", struct stat, st_uid, "user ID of owner");
    dumpField("Stat", "GroupId", struct stat, st_gid, "group ID of owner");
    dumpField("Stat", "RDev", struct stat, st_rdev, "device ID (if special file)");
    dumpField("Stat", "Size", struct stat, st_size, "total size, in bytes");
    dumpField("Stat", "BlkSize", struct stat, st_blksize, "blocksize for file system I/O");
    dumpField("Stat", "Blocks", struct stat, st_blocks, "number of 512B blocks allocated");
    dumpField("Stat", "AccessTime", struct stat, st_atime, "time of last access");
    dumpField("Stat", "ModTime", struct stat, st_mtime, "time of last modification");
    dumpField("Stat", "CTime", struct stat, st_ctime, "time of last status change");
    printf("}\n\n");
    printf("// Warning: because of the use of bit fields, it is not possible to determine with accuracy through code where several of the flags are\n");
    printf("public class FileInfo {\n");
    dumpField("FileInfo", "OpenFlags", struct fuse_file_info, flags, "Open flags.	 Available in open() and release()");
    //dumpField("FileInfo", "", struct fuse_file_info, fh_old, "Old file handle, don't use");
    dumpField("FileInfo", "WritePage", struct fuse_file_info, writepage, "In case of a write operation indicates if this was caused by a writepage");
    //dumpField("FileInfo", "DirectIo", struct fuse_file_info, direct_io, "Can be filled in by open, to use direct I/O on this file. Introduced in version 2.4");
    //dumpField("FileInfo", "KeepCache", struct fuse_file_info, keep_cache, "Can be filled in by open, to indicate, that cached file data need not be invalidated. Introduced in version 2.4");
    //dumpField("FileInfo", "Flush", struct fuse_file_info, flush, "Indicates a flush operation.  Set in flush operation, also maybe set in highlevel lock operation and lowlevel release operation. Introduced in version 2.6");
    //dumpField("FileInfo", "NonSeekable", struct fuse_file_info, nonseekable, "Can be filled in by open, to indicate that the file is not seekable.  Introduced in version 2.8");
    //dumpField("FileInfo", "", struct fuse_file_info, padding, "Padding.  Do not use");
    dumpField("FileInfo", "FileHandle", struct fuse_file_info, fh, "File handle.  May be filled in by filesystem in open(). Available in all other file operations");
    dumpField("FileInfo", "LockOwner", struct fuse_file_info, lock_owner, "Lock owner id.  Available in locking operations and flush");
    printf("}\n\n");

	return 0;
}
