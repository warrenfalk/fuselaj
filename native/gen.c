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
#define dumpField(typename, name, type, field, caption) dumpfield(1, typename, name, fieldsize(type, field), fieldoffset(type, field), caption)
#define dumpRoField(typename, name, type, field, caption) dumpfield(0, typename, name, fieldsize(type, field), fieldoffset(type, field), caption)

void dumpfield(int putter, const char *typename, const char *name, int size, long offset, const char *caption) {
	const char *ftype = "??";
	const char *type = "??";
	if (size == 2) {
		ftype = "Short";
		type = "short";
	}
	else if (size == 4) {
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
	if (putter) {
		printf("\t/**\n");
		printf("\t * Set %s\n", caption);
		printf("\t */\n");
		printf("\tpublic %s put%s(final %s value) {\n", typename, name, type);
		printf("\t\tbb.put%s(0x%lx, value);\n", ftype, offset);
		printf("\t\treturn this;\n");
		printf("\t}\n");
		printf("\t\n");
	}
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
    int x = ENOENT;

    printf("public class StatVfs {\n");
    dumpField("StatVfs", "BlockSize", struct statvfs, f_bsize, "file system block size");
    dumpField("StatVfs", "FragmentSize", struct statvfs, f_frsize, "fragment size");
    dumpField("StatVfs", "Blocks", struct statvfs, f_blocks, "size of fs in FragmentSize units");
    dumpField("StatVfs", "BlocksFree", struct statvfs, f_bfree, "# free blocks");
    dumpField("StatVfs", "BlocksAvail", struct statvfs, f_bavail, "# free blocks for unprivileged users");
    dumpField("StatVfs", "Files", struct statvfs, f_files, "# inodes");
    dumpField("StatVfs", "FilesFree", struct statvfs, f_ffree, "# free inodes");
    dumpField("StatVfs", "FilesAvail", struct statvfs, f_favail, "# free inodes for unprivileged users");
    dumpField("StatVfs", "FileSystemId", struct statvfs, f_fsid, "file system ID");
    dumpField("StatVfs", "MountFlags", struct statvfs, f_flag, "mount flags");
    dumpField("StatVfs", "NameMax", struct statvfs, f_namemax, "maximum filename length");
    printf("}\n\n");

    printf("public class Flock {\n");
    dumpField("Flock", "Type", struct flock, l_type, "Type of lock: F_RDLCK, F_WRLCK, or F_UNLCK.");
    dumpField("Flock", "Whence", struct flock, l_whence, "Where `l_start' is relative to (like `lseek'). ");
    dumpField("Flock", "Start", struct flock, l_start, "Offset where the lock begins. ");
    dumpField("Flock", "Length", struct flock, l_len, "Size of the locked area; zero means until EOF. ");
    dumpField("Flock", "Pid", struct flock, l_pid, "Process holding the lock. ");
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

    printf("public class FuseConnInfo {\n");
    dumpField("FuseConnInfo", "ProtoMajor", struct fuse_conn_info, proto_major, "Major version of the protocol (read-only)");
    dumpField("FuseConnInfo", "ProtoMinor", struct fuse_conn_info, proto_minor, "Minor version of the protocol (read-only)");
    dumpField("FuseConnInfo", "AsyncRead", struct fuse_conn_info, async_read, "Is asynchronous read supported (read-write)");
    dumpField("FuseConnInfo", "MaxWrite", struct fuse_conn_info, max_write, "Maximum size of the write buffer");
    dumpField("FuseConnInfo", "MaxReadahead", struct fuse_conn_info, max_readahead, "Maximum readahead");
    dumpField("FuseConnInfo", "Capable", struct fuse_conn_info, capable, "Capability flags, that the kernel supports");
    dumpField("FuseConnInfo", "Want", struct fuse_conn_info, want, "Capability flags, that the filesystem wants to enable");
    printf("}\n\n");

    printf("public class FuseContext {\n");
    dumpRoField("FuseContext", "FuseHandle", struct fuse_context, fuse, "Handle to fuse environment");
    dumpRoField("FuseContext", "UserId", struct fuse_context, uid, "User ID of the calling process");
    dumpRoField("FuseContext", "GroupId", struct fuse_context, gid, "Group ID of the calling process");
    dumpRoField("FuseContext", "ProcessId", struct fuse_context, pid, "Thread ID of the calling process");
    dumpRoField("FuseContext", "PrivateData", struct fuse_context, private_data, "Private data returned from init()");
    dumpRoField("FuseContext", "UMask", struct fuse_context, umask, "Umask of the calling process (introduced in version 2.8)");
    printf("}\n\n");

	return 0;
}
