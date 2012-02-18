package warrenfalk.fuselaj;

import java.nio.ByteBuffer;



public abstract class Filesystem {
	public int run(String[] args) {
		System.loadLibrary("fuselaj");
		Fuselaj fuselaj = new Fuselaj();
		return fuselaj.initialize(this, args);
	}

	/**
	 * Fill in a Stat structure with metadata for the given path
	 * The Stat structure passed in is already zeroed.
	 * If a Stat field is meaningless or semi-meaningless, it should be left at zero.
	 * @param path the path of the file relative to the file system
	 * @param stat the Stat structure to fill in
	 * @throws FilesystemException
	 */
	protected void getattr(String path, Stat stat) throws FilesystemException {
		throw new FilesystemException(Errno.ENOENT);
	}
	
	@SuppressWarnings("unused")
	private final int _getattr(String path, Stat stat) {
		try {
			getattr(path, stat);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.EIO.code;
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
		throw new FilesystemException(Errno.ENOENT);
	}

	@SuppressWarnings("unused")
	private final int _readdir(String path, DirBuffer dirBuffer, FileInfo fileInfo) {
		try {
			readdir(path, dirBuffer, fileInfo);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.EIO.code;
		}
	}
	
	protected void open(String path, FileInfo fileInfo) throws FilesystemException {
		throw new FilesystemException(Errno.ENOENT);
	}
	
	@SuppressWarnings("unused")
	private final int _open(String path, FileInfo fileInfo) {
		try {
			open(path, fileInfo);
			return 0;
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.EIO.code;
		}
	}
	
	protected int read(String path, FileInfo fileInfo, ByteBuffer buffer, long position) throws FilesystemException {
		throw new FilesystemException(Errno.ENOENT);
	}
	
	@SuppressWarnings("unused")
	private final int _read(String path, FileInfo fileInfo, ByteBuffer buffer, long position) {
		try {
			return read(path, fileInfo, buffer, position);
		}
		catch (FilesystemException e) {
			return -e.errno.code;
		}
		catch (Throwable t) {
			t.printStackTrace();
			return -Errno.EIO.code;
		}
	}
}
