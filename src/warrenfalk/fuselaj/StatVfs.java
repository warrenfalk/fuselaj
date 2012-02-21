package warrenfalk.fuselaj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StatVfs {
	final ByteBuffer bb;
	
	public StatVfs(ByteBuffer bb) {
		this.bb = bb;
		this.bb.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Get file system block size
	 */
	public long getBlockSize() {
		return bb.getLong(0x0);
	}
	
	/**
	 * Set file system block size
	 */
	public StatVfs putBlockSize(final long value) {
		bb.putLong(0x0, value);
		return this;
	}
	
	/**
	 * Get fragment size
	 */
	public long getFragmentSize() {
		return bb.getLong(0x8);
	}
	
	/**
	 * Set fragment size
	 */
	public StatVfs putFragmentSize(final long value) {
		bb.putLong(0x8, value);
		return this;
	}
	
	/**
	 * Get size of fs in FragmentSize units
	 */
	public long getBlocks() {
		return bb.getLong(0x10);
	}
	
	/**
	 * Set size of fs in FragmentSize units
	 */
	public StatVfs putBlocks(final long value) {
		bb.putLong(0x10, value);
		return this;
	}
	
	/**
	 * Get # free blocks
	 */
	public long getBlocksFree() {
		return bb.getLong(0x18);
	}
	
	/**
	 * Set # free blocks
	 */
	public StatVfs putBlocksFree(final long value) {
		bb.putLong(0x18, value);
		return this;
	}
	
	/**
	 * Get # free blocks for unprivileged users
	 */
	public long getBlocksAvail() {
		return bb.getLong(0x20);
	}
	
	/**
	 * Set # free blocks for unprivileged users
	 */
	public StatVfs putBlocksAvail(final long value) {
		bb.putLong(0x20, value);
		return this;
	}
	
	/**
	 * Get # inodes
	 */
	public long getFiles() {
		return bb.getLong(0x28);
	}
	
	/**
	 * Set # inodes
	 */
	public StatVfs putFiles(final long value) {
		bb.putLong(0x28, value);
		return this;
	}
	
	/**
	 * Get # free inodes
	 */
	public long getFilesFree() {
		return bb.getLong(0x30);
	}
	
	/**
	 * Set # free inodes
	 */
	public StatVfs putFilesFree(final long value) {
		bb.putLong(0x30, value);
		return this;
	}
	
	/**
	 * Get # free inodes for unprivileged users
	 */
	public long getFilesAvail() {
		return bb.getLong(0x38);
	}
	
	/**
	 * Set # free inodes for unprivileged users
	 */
	public StatVfs putFilesAvail(final long value) {
		bb.putLong(0x38, value);
		return this;
	}
	
	/**
	 * Get file system ID
	 */
	public long getFileSystemId() {
		return bb.getLong(0x40);
	}
	
	/**
	 * Set file system ID
	 */
	public StatVfs putFileSystemId(final long value) {
		bb.putLong(0x40, value);
		return this;
	}
	
	/**
	 * Get mount flags
	 */
	public long getMountFlags() {
		return bb.getLong(0x48);
	}
	
	/**
	 * Set mount flags
	 */
	public StatVfs putMountFlags(final long value) {
		bb.putLong(0x48, value);
		return this;
	}
	
	/**
	 * Get maximum filename length
	 */
	public long getNameMax() {
		return bb.getLong(0x50);
	}
	
	/**
	 * Set maximum filename length
	 */
	public StatVfs putNameMax(final long value) {
		bb.putLong(0x50, value);
		return this;
	}
	
}
