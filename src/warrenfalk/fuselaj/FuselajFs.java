package warrenfalk.fuselaj;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;



public abstract class FuselajFs {
	/** Flag indicating, that the filesystem can accept a NULL path
	 * as the first argument for the following operations:
	 *
	 * read, write, flush, release, fsync, readdir, releasedir,
	 * fsyncdir, ftruncate, fgetattr and lock
	 */
	final boolean nullPathsOk;
	protected final FileSystem nfs;
	
	/** Construct a Filesystem object
	 * 
	 * @param nullPathsOk flag indicating that the filesystem can accept a NULL path as the first argument for operations receiving FileInfo structure
	 */
	public FuselajFs(final boolean nullPathsOk) {
		this.nullPathsOk = nullPathsOk;
		this.nfs = FileSystems.getDefault();
	}
	
	final static ThreadLocal<CharsetEncoder> _utf8encoder = new ThreadLocal<CharsetEncoder>() {
		protected CharsetEncoder initialValue() {
			return Charset.forName("utf-8").newEncoder();
		}
	};
	
	protected static long getUnixTime(long timeInMillis) {
		return timeInMillis / 1000;
	}
	
	private native void initialize();
	
	private native int fuse_main(String[] args);
	
	native static ByteBuffer getCurrentContext();
	
	native static Object toObject(long jobject);
	
	/**
	 * Does an operating system stat on the file, filling the Stat structure
	 * @param path the path of the file to stat
	 * @param stat the structure to populate
	 * @return error code
	 */
	public static int os_stat(Path path, Stat stat) {
		return _os_stat(path.toString(), stat.bb);
	}
	
	native static int _os_stat(String path, ByteBuffer bb);
	
	boolean isImplemented(String name) {
		Method base = getBaseMethod(name);
		if (base == null)
			return false;
		return isMethodImplemented(base);
	}
	
	private Method getBaseMethod(String name) {
		for (Method method : FuselajFs.class.getDeclaredMethods()) {
			if (name.equals(method.getName()))
				return method;
		}
		return null;
	}
	
	private boolean isMethodImplemented(Method base) {
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
			return !method.getDeclaringClass().equals(FuselajFs.class); 
		}
		return false; 
	}

	public int run(String[] args) {
		try {
			System.loadLibrary("fuselaj");
		}
		catch (UnsatisfiedLinkError ule) {
			System.err.println("Unable to locate fuselaj library");
			System.err.println("java.library.path = " + System.getProperty("java.library.path"));
			System.err.println("  Tip: Use -Djava.library.path=/path/to/dir/containing/fuselaj/library");
			System.err.println("  Tip: make sure fuselaj is built (make -C fuselaj/native)");
			ule.printStackTrace();
			System.exit(-1);
		}
		initialize();
		return fuse_main(args);
	}
	
	private Path path(String path) {
		if (null == path)
			return null;
		int index = 0;
		int length = path.length();
		while (index < length && path.charAt(index) == '/')
			index++;
		return nfs.getPath(index == 0 ? path : path.substring(index));
	}

	/** Get file attributes.
	 *
	 * <p>Similar to stat().  The <code>Dev</code> and <code>BlkSize</code> fields are
	 * ignored.	 The <code>Inode</code> field is ignored except if the 'use_ino'
	 * mount option is given.</p>
	 * <p>Notes:
	 * <ul>
	 * <li>The passed-in Stat structure is already zeroed.</li>
	 * <li>Fields that are meaningless should be left zero.</li>
	 * <li>Mode should contain one of the IF modes (e.g. <code>IFDIR</code> or <code>IFREG</code>)
	 * </ul></p>
	 * @param path the path of the file relative to the file system
	 * @param stat the Stat structure to fill in
	 * @throws FilesystemException
	 */
	protected void getattr(Path path, Stat stat) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}
	
	private final int _getattr(String path, ByteBuffer stat) {
		try {
			getattr(path(path), new Stat(stat));
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
	
	/** Read directory
	 *
	 * <p>This supersedes the old getdir() interface.  New applications
	 * should use this.</p>
	 *
	 * <p>The filesystem may choose between two modes of operation:
	 * <ol>
	 * <li>The readdir implementation ignores the buffer position, and
	 * passes zero to the buffer's putDir()'s position parameter.  The putDir()
	 * function will return <code>false</code> (unless an error happens), so the
	 * whole directory is read in a single readdir operation.  This
	 * works just like the old getdir() method.</li>
	 *
	 * <li>The readdir implementation keeps track of the positions of the
	 * directory entries.  It uses the position value and always
	 * passes a non-zero position to the putDir() function.  When the buffer
	 * is full (or an error happens) the putDir() function will return
	 * true.</li>
	 * </ol></p>
	 *
	 * <p>Introduced in version 2.3</p>
	 * 
	 * @param path path of the directory relative to the filesystem
	 * @param dirBuffer the entry buffer to fill
	 * @param fileInfo a structure containing detail on the directory
	 * @throws FilesystemException
	 */
	protected void readdir(Path path, DirBuffer dirBuffer, FileInfo fileInfo) throws FilesystemException {
		throw new FilesystemException(Errno.NoSuchFileOrDirectory);
	}

	private final int _readdir(String path, DirBuffer dirBuffer, ByteBuffer fileInfo) {
		try {
			readdir(path(path), dirBuffer, new FileInfo(fileInfo));
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
	
	/** File open operation
	 *
	 * <p>No creation (O_CREAT, O_EXCL) and by default also no
	 * truncation (O_TRUNC) flags will be passed to <code>open()</code>. If an
	 * application specifies O_TRUNC, fuse first calls <code>truncate()</code>
	 * and then <code>open()</code>. Only if 'atomic_o_trunc' has been
	 * specified and kernel version is 2.6.24 or later, O_TRUNC is
	 * passed on to open.</p>
	 *
	 * <p>Unless the 'default_permissions' mount option is given,
	 * open should check if the operation is permitted for the
	 * given flags.  (This should be done by implementing, then 
	 * calling <code>access()</code> as this will yield consistent
	 * results).  Optionally open may also return an arbitrary
	 * filehandle in the <code>FileInfo</code> structure, which will be
	 * passed to all file operations.</p>
	 *
	 * <p>Changed in version 2.2</p>
	 *  
	 * @param path
	 * @param fileInfo
	 * @throws FilesystemException
	 */
	protected void open(Path path, FileInfo fileInfo) throws FilesystemException {
		throw new FilesystemException(Errno.NoSuchFileOrDirectory);
	}
	
	private final int _open(String path, ByteBuffer fileInfo) {
		try {
			open(path(path), new FileInfo(fileInfo));
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
	
	/** Read data from an open file
	 *
	 * <p>Read should completely fill the buffer except
	 * on EOF or error, otherwise the rest of the data will be
	 * substituted with zeroes. An exception to this is when the
	 * 'direct_io' mount option is specified, in which case the return
	 * value of the read system call will reflect the number of bytes
	 * written in this operation</p>
	 *
	 * <p>Changed in version 2.2</p>
	 *  
	 * @param path
	 * @param fileInfo
	 * @param buffer
	 * @param position
	 * @throws FilesystemException
	 */
	protected void read(Path path, FileInfo fileInfo, ByteBuffer buffer, long position) throws FilesystemException {
		throw new FilesystemException(Errno.NoSuchFileOrDirectory);
	}
	
	private final int _read(String path, ByteBuffer fileInfo, ByteBuffer buffer, long position) {
		try {
			int start = buffer.position();
			read(path(path), new FileInfo(fileInfo), buffer, position);
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

	/** Create a directory 
	 *
	 * @param path
	 * @param mode
	 * @throws FilesystemException
	 */
	protected void mkdir(Path path, int mode) throws FilesystemException {
		throw new FilesystemException(Errno.NoSuchFileOrDirectory);
	}
	
	private final int _mkdir(String path, int mode) {
		try {
			mkdir(path(path), mode | Mode.IFDIR);
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
	
	private Object _init(ByteBuffer conn){
		return init(new FuseConnInfo(conn));
	}
	
	/** Initialize filesystem
	 *
	 * <p>The return value will passed in the <code>privateData</code> field of
	 * <code>FuseContext</code> to all file operations and as a parameter to the
	 * destroy() method.</p>
	 *
	 * <p>Introduced in version 2.3</p>
	 * <p>Changed in version 2.6</p>
	 * 
	 * @param info
	 * @return
	 */
	private Object init(FuseConnInfo info) {
		return null;
	}

	private void _destroy(Object obj){
		destroy(obj);
	}
	
	/** Clean up filesystem
	 *
	 * <p>Called on filesystem exit.</p>
	 *
	 * <p>Introduced in version 2.3</p>
	 * @param privateData object returned from init (or null)
	 */
	protected void destroy(Object privateData) {
	}

    private final int _fgetattr(String path, ByteBuffer stat, ByteBuffer fi) {
		try {
			fgetattr(path(path), new Stat(stat), new FileInfo(fi));
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

	/** Get attributes from an open file
	 *
	 * <p>This method is called instead of the getattr() method if the
	 * file information is available.</p>
	 *
	 * <p>Currently this is only called after the create() method if that
	 * is implemented (see above).  Later it may be called for
	 * invocations of fstat() too.</p>
	 *
	 * <p>Introduced in version 2.5</p>
	 * 
     * @param path
     * @param stat
     * @param fi
     * @throws FilesystemException
     */
    protected void fgetattr(Path path, Stat stat, FileInfo fi) throws FilesystemException {
    	getattr(path, stat);
	}

    private final int _access(String path, int mask) {
		try {
			access(path(path), mask);
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
	
	/** Check file access permissions
	 *
	 * <p>This will be called for the access() system call.  If the
	 * 'default_permissions' mount option is given, this method is not
	 * called.</p>
	 *
	 * <p>This method is not called under Linux kernel versions 2.4.x</p>
	 *
	 * <p>Introduced in version 2.5</p>

     * <p>This is the same as the access(2) system call.</p>
     * <p>Implementation should throw <code>FilesystemException</code> with 
     * <code>Errno.NoSuchFileOrDirectory</code> if the path doesn't exist, 
     * <code>Errno.PermissionDenied</code> if the requested permission isn't available.</p>
     * <p>Note: it can be called on files, directories, or any other object that appears in the filesystem.
     * This call is not required but is highly recommended.</p>
     * <p>Call <code>FuseContext.getCurrent()</code> to get the current user id and group id.</p>
     * 
     * @param path
     * @param mask
     * @throws FilesystemException
     */
    protected void access(Path path, int mask) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _readlink(String path, ByteBuffer buffer) {
		// fill buffer with zero-terminated string
		try {
			Path target = readlink(path(path));
			CharBuffer cb = CharBuffer.wrap(target.toString());
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

	/** Read the target of a symbolic link
	 *
	 * <p>Return a String representing the target of the symbolic link</p>
	 * <p>Notes:<ul>
	 * <li>If the returned string is too long for the internal buffer, it will just be truncated</li>
	 * </ul></p>
     * @param path the path to the symbolic link
     * @return a String representing the target of the symbolic link
     * @throws FilesystemException
     */
    protected Path readlink(Path path) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _opendir(String path, ByteBuffer fi) {
		try {
			opendir(path(path), new FileInfo(fi));
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

    /** Open directory
	 *
	 * <p>Unless the 'default_permissions' mount option is given,
	 * this method should check if opendir is permitted for this
	 * directory.  (ideally by implementing access() and then calling
	 * from this function). Optionally opendir may also return an arbitrary
	 * filehandle in the FileInfo structure, which will be
	 * passed to readdir, closedir and fsyncdir.</p>
	 *
	 * <p>Introduced in version 2.3</p>
	 * 
     * @param path
     * @param fi
     * @throws FilesystemException
     */
    protected void opendir(Path path, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _mknod(String path, int mode, long rdev) {
		try {
			mknod(path(path), mode, rdev);
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

	/** Create a file node
	 *
	 * <p>This is called for creation of all non-directory, non-symlink
	 * nodes, and additionally all regular files if the filesystem does not 
	 * define a <code>create()</code> method, which can be called instead</p>
     * 
     * @param path
     * @param mode
     * @param rdev
     * @throws FilesystemException
     */
    protected void mknod(Path path, int mode, long rdev) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _unlink(String path) {
		try {
			unlink(path(path));
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

    /** Remove a file
     * 
     * @param path
     * @throws FilesystemException
     */
    protected void unlink(Path path) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _rmdir(String path) {
		try {
			rmdir(path(path));
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

    /** Remove a directory
     * 
     * <p>Removes the directory indicated by <code>path</code></p>
     * <p>Notes: <ul>
     * <li>It is up to this function to check for a non-empty directory and fail accordingly</li>
     * </ul></p>
     * @param path
     * @throws FilesystemException
     */
	protected void rmdir(Path path) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _symlink(String targetOfLink, String pathOfLink) {
		try {
			symlink(path(targetOfLink), path(pathOfLink));
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

    /** Create a symbolic link
     * 
     * @param targetOfLink
     * @param pathOfLink
     * @throws FilesystemException
     */
    protected void symlink(Path targetOfLink, Path pathOfLink) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _rename(String from, String to) {
		try {
			rename(path(from), path(to));
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

    /** Rename a file
     * 
     * @param from
     * @param to
     * @throws FilesystemException
     */
    protected void rename(Path from, Path to) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _link(String from, String to) {
		try {
			link(path(from), path(to));
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

    /** Create a hard link to a file
     * 
     * @param from
     * @param to
     * @throws FilesystemException
     */
    protected void link(Path from, Path to) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _chmod(String path, int mode) {
		try {
			chmod(path(path), mode);
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

    /** Change the permission bits of a file
     * 
     * @param path
     * @param mode
     * @throws FilesystemException
     */
    protected void chmod(Path path, int mode) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _chown(String path, int uid, int gid) {
		try {
			chown(path(path), uid, gid);
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

    /** Change the owner and group of a file
     * 
     * <p>Notes: <ul>
     * <li>The implementer should leave uid and gid unchanged if each is -1.</li>
     * <li>This function will not be called unless the filesystem process has the appropriate permissions (e.g. as root)</li>
     * </ul></p>
     * @param path
     * @param uid new userid or -1 to leave unchanged
     * @param gid new group id or -1 to leave unchanged
     * @throws FilesystemException
     */
    protected void chown(Path path, int uid, int gid) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _truncate(String path, long size) {
		try {
			truncate(path(path), size);
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

    /** Change the size of a file
     * 
     * @param path
     * @param size
     * @throws FilesystemException
     */
    protected void truncate(Path path, long size) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _ftruncate(String path, long size, ByteBuffer fileInfo) {
		try {
			ftruncate(path(path), size, new FileInfo(fileInfo));
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

    /** Change the size of an open file
	 *
	 * <p>This method is called instead of the truncate() method if the
	 * truncation was invoked from an ftruncate() system call.</p>
	 *
	 * <p>If this method is not implemented or under Linux kernel
	 * versions earlier than 2.6.15, the truncate() method will be
	 * called instead.</p>
	 *
	 * <p>Introduced in version 2.5</p>
	 * 
     * @param path
     * @param size
     * @param fi
     * @throws FilesystemException
     */
    protected void ftruncate(Path path, long size, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _utimens(String path, long accessSeconds, long accessNanoseconds, long modSeconds, long modNanoseconds) {
		try {
			utimens(path(path), accessSeconds, accessNanoseconds, modSeconds, modNanoseconds);
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

    /** Change the access and modification times of a file with nanosecond resolution
	 *
	 * <p>Introduced in version 2.6</p>
     * 
     * @param path
     * @param accessSeconds
     * @param accessNanoseconds
     * @param modSeconds
     * @param modNanoseconds
     * @throws FilesystemException
     */
    protected void utimens(Path path, long accessSeconds, long accessNanoseconds, long modSeconds, long modNanoseconds) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _write(String path, ByteBuffer fi, ByteBuffer bb, long offset) {
		try {
			int start = bb.position();
			write(path(path), new FileInfo(fi), bb, offset);
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

    /** Write data to an open file
	 *
	 * <p>write should completely empty the supplied buffer
	 * except on error. An exception to this is when the 'direct_io'
	 * mount option is specified (see read operation).</p>
	 *
	 * <p>Changed in version 2.2</p>
     * 
     * @param path
     * @param fi
     * @param bb
     * @param offset
     * @throws FilesystemException
     */
    protected void write(Path path, FileInfo fi, ByteBuffer bb, long offset) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _statfs(String path, ByteBuffer stat) {
		try {
			statfs(path(path), new StatVfs(stat));
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

    /** Get file system statistics
	 *
	 * <p>The <code>FragmentSize</code>, <code>FilesAvail</code>, <code>FilesystemId</code> and <code>MountFlags</code> fields are ignored</p>
	 *
	 * <p>Replaced 'struct statfs' parameter with 'struct statvfs' in
	 * version 2.5</p>
     * 
     * @param path
     * @param stat
     * @throws FilesystemException
     */
    protected void statfs(Path path, StatVfs stat) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _release(String path, ByteBuffer fi) {
		try {
			release(path(path), new FileInfo(fi));
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

	/** Release an open file
	 *
	 * <p>Release is called when there are no more references to an open
	 * file: all file descriptors are closed and all memory mappings
	 * are unmapped.</p>
	 *
	 * <p>For every open() call there will be exactly one release() call
	 * with the same flags and file descriptor.	 It is possible to
	 * have a file opened more than once, in which case only the last
	 * release will mean, that no more reads/writes will happen on the
	 * file.  The return value of release is ignored.</p>
	 *
	 * <p>Changed in version 2.2</p>
     * 
     * @param path
     * @param fi
     * @throws FilesystemException
     */
    protected void release(Path path, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _releasedir(String path, ByteBuffer fi) {
		try {
			releasedir(path(path), new FileInfo(fi));
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

    /** Release directory
	 *
	 * <p>Introduced in version 2.3</p>
	 * 
     * @param path
     * @param fi
     * @throws FilesystemException
     */
    protected void releasedir(Path path, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _fsync(String path, int isdatasync, ByteBuffer fi) {
		try {
			fsync(path(path), isdatasync != 0, new FileInfo(fi));
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

	/** Synchronize file contents
	 *
	 * <p>If the datasync parameter is <code>true</code>, then only the user data
	 * should be flushed, not the meta data.</p>
	 *
	 * <p>Changed in version 2.2</p>
	 * 
     * @param path
     * @param isdatasync
     * @param fi
     * @throws FilesystemException
     */
    protected void fsync(Path path, boolean isdatasync, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _fsyncdir(String path, int isdatasync, ByteBuffer fi) {
		try {
			fsyncdir(path(path), isdatasync != 0, new FileInfo(fi));
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

    /** Synchronize directory contents
	 *
	 * <p>If the <code>isdatasync</code> parameter is true, then only the user data
	 * should be flushed, not the meta data</p>
	 *
	 * <p>Introduced in version 2.3</p>
	 * 
     * @param path
     * @param isdatasync
     * @param fi
     * @throws FilesystemException
     */
    protected void fsyncdir(Path path, boolean isdatasync, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _flush(String path, ByteBuffer fi) {
		try {
			flush(path(path), new FileInfo(fi));
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

	/** Possibly flush cached data
	 *
	 * <p>BIG NOTE: This is not equivalent to fsync().  It's not a
	 * request to sync dirty data.</p>
	 *
	 * <p>Flush is called on each close() of a file descriptor.  So if a
	 * filesystem wants to return write errors in close() and the file
	 * has cached dirty data, this is a good place to write back data
	 * and return any errors.  Since many applications ignore close()
	 * errors this is not always useful.</p>
	 *
	 * <p>NOTE: The flush() method may be called more than once for each
	 * open().	This happens if more than one file descriptor refers
	 * to an opened file due to dup(), dup2() or fork() calls.	It is
	 * not possible to determine if a flush is final, so each flush
	 * should be treated equally.  Multiple write-flush sequences are
	 * relatively rare, so this shouldn't be a problem.</p>
	 *
	 * <p>Filesystems shouldn't assume that flush will always be called
	 * after some writes, or that if will be called at all.</p>
	 *
	 * <p>Changed in version 2.2</p>
	 * 
     * @param path
     * @param fi
     * @throws FilesystemException
     */
    protected void flush(Path path, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _lock(String path, ByteBuffer fi, int cmd, ByteBuffer locks) {
		try {
			lock(path(path), new FileInfo(fi), cmd, new Flock(locks));
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

	/** Perform POSIX file locking operation
	 *
	 * <p>The cmd argument will be either F_GETLK, F_SETLK or F_SETLKW.</p>
	 *
	 * <p>For the meaning of fields in <code>Flock</code> see the man page
	 * for fcntl(2).  The <code>Whence</code> field will always be set to
	 * SEEK_SET.</p>
	 *
	 * <p>For checking lock ownership, the <code>fi.getLockOwner()</code>
	 * argument must be used.</p>
	 *
	 * <p>For F_GETLK operation, the library will first check currently
	 * held locks, and if a conflicting lock is found it will return
	 * information without calling this method.	 This ensures, that
	 * for local locks the <code>Pid</code> field is correctly filled in. The
	 * results may not be accurate in case of race conditions and in
	 * the presence of hard links, but it's unlikely that an
	 * application would rely on accurate GETLK results in these
	 * cases.  If a conflicting lock is not found, this method will be
	 * called, and the filesystem may fill out <code>Pid</code> by a meaningful
	 * value, or it may leave this field zero.</p>
	 *
	 * <p>For F_SETLK and F_SETLKW the <code>Pid</code> field will be set to the pid
	 * of the process performing the locking operation.</p>
	 *
	 * <p>Note: if this method is not implemented, the kernel will still
	 * allow file locking to work locally.  Hence it is only
	 * interesting for network filesystems and similar.</p>
	 *
	 * <p>Introduced in version 2.6</p>
	 * 
     * @param path
     * @param fi
     * @param cmd
     * @param locks
     * @throws FilesystemException
     */
    protected void lock(Path path, FileInfo fi, int cmd, Flock locks) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _bmap(String path, long blocksize, ByteBuffer blockno) {
		try {
			bmap(path(path), blocksize, blockno);
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

    /** Map block index within file to block index within device
	 *
	 * <p>Note: This makes sense only for block device backed filesystems
	 * mounted with the 'blkdev' option</p>
	 *
	 * <p>Introduced in version 2.6</p>
     * 
     * @param path
     * @param blocksize
     * @param blockno
     * @throws FilesystemException
     */
    protected void bmap(Path path, long blocksize, ByteBuffer blockno) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _setxattr(String path, String name, String value, long size, int flags) {
		try {
			setxattr(path(path), name, value, size, flags);
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

    /** Set extended attributes
     * 
     * @param path
     * @param name
     * @param value
     * @param size
     * @param flags
     * @throws FilesystemException
     */
    protected void setxattr(Path path, String name, String value, long size, int flags) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _getxattr(String path, String name, ByteBuffer value) {
		try {
			getxattr(path(path), name, value);
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

    /** Get extended attributes
     * 
     * @param path
     * @param name
     * @param value
     * @throws FilesystemException
     */
    protected void getxattr(Path path, String name, ByteBuffer value) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

    private final int _listxattr(String path, ByteBuffer list) {
		try {
			listxattr(path(path), list);
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
	
    /** List extended attributes
     * 
     * @param path
     * @param list
     * @throws FilesystemException
     */
    protected void listxattr(Path path, ByteBuffer list) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

	private final int _removexattr(String path, String name) {
		try {
			removexattr(path(path), name);
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

	/** Remove extended attributes
	 * 
	 * @param path
	 * @param name
	 * @throws FilesystemException
	 */
	protected void removexattr(Path path, String name) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

	private final int _create(String path, int mode, ByteBuffer fi) {
		try {
			create(path(path), mode, new FileInfo(fi));
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
	
	/** Create and open a file
	 *
	 * <p>If the file does not exist, first create it with the specified
	 * mode, and then open it.</p>
	 *
	 * <p>If this method is not implemented or under Linux kernel
	 * versions earlier than 2.6.15, the mknod() and open() methods
	 * will be called instead.</p>
	 *
	 * <p>Introduced in version 2.5</p>
	 * 
	 * @param path
	 * @param mode
	 * @param fi
	 * @throws FilesystemException
	 */
	protected void create(Path path, int mode, FileInfo fi) throws FilesystemException {
		throw new FilesystemException(Errno.FunctionNotImplemented);
	}

	/*
	@SuppressWarnings("unused")
    private final int _ioctl(Path path, int cmd, void* arg, ByteBuffer fi, unsigned int flags, void* data) {
	}

	@SuppressWarnings("unused")
    private final int _poll(Path path, ByteBuffer fi, struct fuse_pollhandle* ph, unsigned* reventsp) {
	}
	*/
}
