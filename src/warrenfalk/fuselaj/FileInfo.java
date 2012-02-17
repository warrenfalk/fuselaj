package warrenfalk.fuselaj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class FileInfo {
	final ByteBuffer bb;
	
	public final static int O_ACCMODE = 0003;
	public final static int O_RDONLY = 00;
	public final static int O_WRONLY = 01;
	public final static int O_RDWR = 02;	
	public final static int O_CREAT = 0100;
	public final static int O_EXCL = 0200;
	public final static int O_NOCTTY = 0400;
	public final static int O_TRUNC = 01000;
	public final static int O_APPEND = 02000;
	public final static int O_NONBLOCK = 04000;
	public final static int O_NDELAY = O_NONBLOCK;
	public final static int O_SYNC = 04010000;
	public final static int O_FSYNC = O_SYNC;
	public final static int O_ASYNC = 020000;
	
	/* Must be a directory. */
	public final static int O_DIRECTORY = 0200000;
	/* Do not follow links. */
	public final static int O_NOFOLLOW = 0400000;
	/* Set close_on_exec. */
	public final static int O_CLOEXEC = 02000000;
	/* Direct disk access. */
	public final static int O_DIRECT = 040000;
	/* Do not set atime. */
	public final static int O_NOATIME = 01000000;
	
	/* Synchronize data. */
	public final static int O_DSYNC = 010000;
	/* Synchronize read operations. */
	public final static int O_RSYNC = O_SYNC;
	
	public final static int O_LARGEFILE = 0100000;

	
	public FileInfo(ByteBuffer bb) {
		this.bb = bb;
		this.bb.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Get Open flags.	 Available in open() and release()
	 */
	public int getOpenFlags() {
		return bb.getInt(0x0);
	}
	
	/**
	 * Set Open flags.	 Available in open() and release()
	 */
	public FileInfo putOpenFlags(final int value) {
		bb.putInt(0x0, value);
		return this;
	}
	
	/**
	 * Get In case of a write operation indicates if this was caused by a writepage
	 */
	public int getWritePage() {
		return bb.getInt(0x10);
	}
	
	/**
	 * Set In case of a write operation indicates if this was caused by a writepage
	 */
	public FileInfo putWritePage(final int value) {
		bb.putInt(0x10, value);
		return this;
	}
	
	/**
	 * Get File handle.  May be filled in by filesystem in open(). Available in all other file operations
	 */
	public long getFileHandle() {
		return bb.getLong(0x18);
	}
	
	/**
	 * Set File handle.  May be filled in by filesystem in open(). Available in all other file operations
	 */
	public FileInfo putFileHandle(final long value) {
		bb.putLong(0x18, value);
		return this;
	}
	
	/**
	 * Get Lock owner id.  Available in locking operations and flush
	 */
	public long getLockOwner() {
		return bb.getLong(0x20);
	}
	
	/**
	 * Set Lock owner id.  Available in locking operations and flush
	 */
	public FileInfo putLockOwner(final long value) {
		bb.putLong(0x20, value);
		return this;
	}

}
