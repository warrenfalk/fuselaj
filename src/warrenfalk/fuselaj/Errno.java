package warrenfalk.fuselaj;

public enum Errno {
	/** Operation not permitted */
	EPERM (1, "Operation not permitted"),
	/** No such file or directory */
	ENOENT (2, "No such file or directory"),
	/** No such process */
	ESRCH (3, "No such process"),
	/** Interrupted system call */
	EINTR (4, "Interrupted system call"),
	/** I/O error */
	EIO (5, "I/O error"),
	/** No such device or address */
	ENXIO (6, "No such device or address"),
	/** Argument list too long */
	E2BIG (7, "Argument list too long"),
	/** Exec format error */
	ENOEXEC (8, "Exec format error"),
	/** Bad file number */
	EBADF (9, "Bad file number"),
	/** No child processes */
	ECHILD (10, "No child processes"),
	/** Try again */
	EAGAIN (11, "Try again"),
	/** Out of memory */
	ENOMEM (12, "Out of memory"),
	/** Permission denied */
	EACCES (13, "Permission denied"),
	/** Bad address */
	EFAULT (14, "Bad address"),
	/** Block device required */
	ENOTBLK (15, "Block device required"),
	/** Device or resource busy */
	EBUSY (16, "Device or resource busy"),
	/** File exists */
	EEXIST (17, "File exists"),
	/** Cross-device link */
	EXDEV (18, "Cross-device link"),
	/** No such device */
	ENODEV (19, "No such device"),
	/** Not a directory */
	ENOTDIR (20, "Not a directory"),
	/** Is a directory */
	EISDIR (21, "Is a directory"),
	/** Invalid argument */
	EINVAL (22, "Invalid argument"),
	/** File table overflow */
	ENFILE (23, "File table overflow"),
	/** Too many open files */
	EMFILE (24, "Too many open files"),
	/** Not a typewriter */
	ENOTTY (25, "Not a typewriter"),
	/** Text file busy */
	ETXTBSY (26, "Text file busy"),
	/** File too large */
	EFBIG (27, "File too large"),
	/** No space left on device */
	ENOSPC (28, "No space left on device"),
	/** Illegal seek */
	ESPIPE (29, "Illegal seek"),
	/** Read-only file system */
	EROFS (30, "Read-only file system"),
	/** Too many links */
	EMLINK (31, "Too many links"),
	/** Broken pipe */
	EPIPE (32, "Broken pipe"),
	/** Math argument out of domain of func */
	EDOM (33, "Math argument out of domain of func"),
	/** Math result not representable */
	ERANGE (34, "Math result not representable");
	
	final int code;
	final String msg;
	
	Errno(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
