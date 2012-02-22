package warrenfalk.fuselaj;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;



public abstract class Filesystem {
	
	public Filesystem() {
	}
	
	final static ThreadLocal<CharsetEncoder> _utf8encoder = new ThreadLocal<CharsetEncoder>() {
		protected CharsetEncoder initialValue() {
			return Charset.forName("utf-8").newEncoder();
		}
	};

	private native void initialize();
	
	private native int fuse_main(String[] args);
	
	native static ByteBuffer getCurrentContext();
	
	boolean isImplemented(String name) {
		Method base = getBaseMethod(name);
		if (base == null)
			return false;
		return isImplemented(base);
	}
	
	private Method getBaseMethod(String name) {
		for (Method method : Filesystem.class.getDeclaredMethods()) {
			if (name.equals(method.getName()))
				return method;
		}
		return null;
	}
	
	boolean isImplemented(Method base) {
		for (Method method : getClass().getDeclaredMethods()) {
			if (!base.getName().equals(method.getName()))
				continue;
			Class<?>[] baseptypes = base.getParameterTypes();
			Class<?>[] methodptypes = method.getParameterTypes();
			if (baseptypes.length != methodptypes.length)
				continue;
			for (int i = 0; i < baseptypes.length; i++)
				if (!baseptypes[i].equals(methodptypes[i]))
					continue;
			return !method.getDeclaringClass().equals(Filesystem.class); 
		}
		return false; 
	}

	public int run(String[] args) {
		System.loadLibrary("fuselaj");
		initialize();
		return fuse_main(args);
	}

	/**
	 * Fill in a Stat structure with metadata for the given path
	 * The Stat structure passed in is already zeroed.
	 * If a Stat field is meaningless or semi-meaningless, it should be left at zero.
	 * The function should fill in the mode field and the links field
	 * The mode field should contain one of the IF modes (e.g. IFDIR or IFREG)
	 * @param path the path of the file relative to the file system
	 * @param stat the Stat structure to fill in
	 * @throws FilesystemException
	 */
	protected void getattr(String path, Stat stat) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}
	
	private final int _getattr(String path, ByteBuffer stat) {
		try {
			getattr(path, new Stat(stat));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}
	
	/**
	 * Fill a buffer with directory entry information for a given directory
	 * Fill the dirBuffer with the directory child information including the name of each child, and optionally an inode and mode.
	 * @param path path of the directory relative to the filesystem
	 * @param dirBuffer the entry buffer to fill
	 * @param fileInfo a structure containing detail on the directory
	 * @throws FilesystemException
	 */
	protected void readdir(String path, DirBuffer dirBuffer, FileInfo fileInfo) throws FilesystemException {
		throw new FilesystemException(Errno.NoSuchFileOrDirectory);
	}

	private final int _readdir(String path, DirBuffer dirBuffer, ByteBuffer fileInfo) {
		try {
			readdir(path, dirBuffer, new FileInfo(fileInfo));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}
	
	protected void open(String path, FileInfo fileInfo) throws FilesystemException {
		throw new FilesystemException(Errno.NoSuchFileOrDirectory);
	}
	
	private final int _open(String path, ByteBuffer fileInfo) {
		try {
			open(path, new FileInfo(fileInfo));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}
	
	protected void read(String path, FileInfo fileInfo, ByteBuffer buffer, long position) throws FilesystemException {
		throw new FilesystemException(Errno.NoSuchFileOrDirectory);
	}
	
	private final int _read(String path, ByteBuffer fileInfo, ByteBuffer buffer, long position) {
		try {
			int start = buffer.position();
			read(path, new FileInfo(fileInfo), buffer, position);
			return buffer.position() - start;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}
	
	protected void mkdir(String path, int mode) throws FilesystemException {
		throw new FilesystemException(Errno.NoSuchFileOrDirectory);
	}
	
	private final int _mkdir(String path, int mode) {
		try {
			mkdir(path, mode);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}
	
	private void _init(ByteBuffer conn){
		new FuseConnInfo(conn);
	}

	private void _destroy(){
	}

    private final int _fgetattr(String path, ByteBuffer stat, ByteBuffer fi) {
		try {
			fgetattr(path, new Stat(stat), new FileInfo(fi));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    /**
     * Just like <code>getattr</code> except that it is meant to work with an open file handle (hence the <code>FileInfo fi</code> parameter).
     * <p>If you aren't doing file-handle-based operations, you can just pass this call onto <code>getattr()</code>, which is what the default implementation does</p>
     * @param path
     * @param stat
     * @param fi
     * @throws FilesystemException
     */
    protected void fgetattr(String path, Stat stat, FileInfo fi) throws FilesystemException {
    	getattr(path, stat);
	}

    private final int _access(String path, int mask) {
		try {
			access(path, mask);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}
	
    /**
     * This is the same as the access(2) system call.
     * <p>Implementation should throw <code>FilesystemException</code> with 
     * <code>Errno.NoSuchFileOrDirectory</code> if the path doesn't exist, 
     * <code>Errno.PermissionDenied</code> if the requested permission isn't available.</p>
     * <p>Note: it can be called on files, directories, or any other object that appears in the filesystem.
     * This call is not required but is highly recommended.</p>
     * <p>Call <code>FuseContext.getCurrent()</code> to get the current user id and group id.</p>
     * @param path
     * @param mask
     * @throws FilesystemException
     */
    protected void access(String path, int mask) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _readlink(String path, ByteBuffer buffer) {
		// fill buffer with zero-terminated string
		try {
			String target = readlink(path);
			CharBuffer cb = CharBuffer.wrap(target);
			CharsetEncoder encoder = _utf8encoder.get();
			encoder.encode(cb, buffer, true);
			buffer.put((byte)0);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected String readlink(String path) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _opendir(String path, ByteBuffer fi) {
		try {
			opendir(path, new FileInfo(fi));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void opendir(String path, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _mknod(String path, int mode, long rdev) {
		try {
			mknod(path, mode, rdev);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void mknod(String path, int mode, long rdev) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _unlink(String path) {
		try {
			unlink(path);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void unlink(String path) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _rmdir(String path) {
		try {
			rmdir(path);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    /**
     * Removes the directory indicated by <code>path</code>
     * <p>It is up to this function to check for a non-empty directory and fail accordingly</p>
     * @param path
     * @throws FilesystemException
     */
	protected void rmdir(String path) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _symlink(String to, String from) {
		try {
			symlink(to, from);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void symlink(String to, String from) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _rename(String from, String to) {
		try {
			rename(from, to);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void rename(String from, String to) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _link(String from, String to) {
		try {
			link(from, to);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void link(String from, String to) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _chmod(String path, int mode) {
		try {
			chmod(path, mode);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void chmod(String path, int mode) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _chown(String path, int uid, int gid) {
		try {
			chown(path, uid, gid);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    /**
     * Change the owner of a file.
     * The implementer should leave uid and gid unchanged if each is -1.
     * This function will not be called unless the filesystem process has the appropriate permissions (e.g. as root)
     * @param path
     * @param uid new userid or -1 to leave unchanged
     * @param gid new group id or -1 to leave unchanged
     * @throws FilesystemException
     */
    protected void chown(String path, int uid, int gid) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _truncate(String path, long size) {
		try {
			truncate(path, size);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void truncate(String path, long size) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _ftruncate(String path, long size) {
		try {
			ftruncate(path, size);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void ftruncate(String path, long size) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _utimens(String path, long accessSeconds, long accessNanoseconds, long modSeconds, long modNanoseconds) {
		try {
			utimens(path, accessSeconds, accessNanoseconds, modSeconds, modNanoseconds);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void utimens(String path, long accessSeconds, long accessNanoseconds, long modSeconds, long modNanoseconds) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _write(String path, ByteBuffer fi, ByteBuffer bb, long offset) {
		try {
			int start = bb.position();
			write(path, new FileInfo(fi), bb, offset);
			return bb.position() - start;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void write(String path, FileInfo fi, ByteBuffer bb, long offset) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _statfs(String path, ByteBuffer stat) {
		try {
			statfs(path, new StatVfs(stat));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void statfs(String path, StatVfs stat) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _release(String path, ByteBuffer fi) {
		try {
			release(path, new FileInfo(fi));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void release(String path, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _releasedir(String path, ByteBuffer fi) {
		try {
			releasedir(path, new FileInfo(fi));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void releasedir(String path, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _fsync(String path, int isdatasync, ByteBuffer fi) {
		try {
			fsync(path, isdatasync, new FileInfo(fi));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void fsync(String path, int isdatasync, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _fsyncdir(String path, int isdatasync, ByteBuffer fi) {
		try {
			fsyncdir(path, isdatasync, new FileInfo(fi));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void fsyncdir(String path, int isdatasync, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _flush(String path, ByteBuffer fi) {
		try {
			flush(path, new FileInfo(fi));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void flush(String path, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _lock(String path, ByteBuffer fi, int cmd, ByteBuffer locks) {
		try {
			lock(path, new FileInfo(fi), cmd, new Flock(locks));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void lock(String path, FileInfo fi, int cmd, Flock locks) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _bmap(String path, long blocksize, ByteBuffer blockno) {
		try {
			bmap(path, blocksize, blockno);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void bmap(String path, long blocksize, ByteBuffer blockno) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _setxattr(String path, String name, String value, long size, int flags) {
		try {
			setxattr(path, name, value, size, flags);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void setxattr(String path, String name, String value, long size, int flags) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _getxattr(String path, String name, ByteBuffer value) {
		try {
			getxattr(path, name, value);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

    protected void getxattr(String path, String name, ByteBuffer value) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _listxattr(String path, ByteBuffer list) {
		try {
			listxattr(path, list);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}
	
    protected void listxattr(String path, ByteBuffer list) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

	private final int _removexattr(String path, String name) {
		try {
			removexattr(path, name);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}

	protected void removexattr(String path, String name) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

	private final int _create(String path, long mode, ByteBuffer fi) {
		try {
			create(path, mode, new FileInfo(fi));
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.IOError.code;
		}
	}
	
	protected void create(String path, long mode, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

	/*
	@SuppressWarnings("unused")
    private final int _ioctl(String path, int cmd, void* arg, ByteBuffer fi, unsigned int flags, void* data) {
	}

	@SuppressWarnings("unused")
    private final int _poll(String path, ByteBuffer fi, struct fuse_pollhandle* ph, unsigned* reventsp) {
	}
	*/
}
