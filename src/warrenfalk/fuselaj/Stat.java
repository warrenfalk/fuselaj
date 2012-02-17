package warrenfalk.fuselaj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Stat {
	final ByteBuffer bb;
	
	public Stat(ByteBuffer bb) {
		this.bb = bb;
		this.bb.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Get ID of device containing file
	 */
	public long getDev() {
		return bb.getLong(0x0);
	}
	
	/**
	 * Set ID of device containing file
	 */
	public Stat putDev(final long value) {
		bb.putLong(0x0, value);
		return this;
	}
	
	/**
	 * Get inode number
	 */
	public long getInode() {
		return bb.getLong(0x8);
	}
	
	/**
	 * Set inode number
	 */
	public Stat putInode(final long value) {
		bb.putLong(0x8, value);
		return this;
	}
	
	/**
	 * Get protection
	 */
	public int getMode() {
		return bb.getInt(0x18);
	}
	
	/**
	 * Set protection
	 */
	public Stat putMode(final int value) {
		bb.putInt(0x18, value);
		return this;
	}
	
	/**
	 * Get number of hard links
	 */
	public long getLinkCount() {
		return bb.getLong(0x10);
	}
	
	/**
	 * Set number of hard links
	 */
	public Stat putLinkCount(final long value) {
		bb.putLong(0x10, value);
		return this;
	}
	
	/**
	 * Get user ID of owner
	 */
	public int getUserId() {
		return bb.getInt(0x1c);
	}
	
	/**
	 * Set user ID of owner
	 */
	public Stat putUserId(final int value) {
		bb.putInt(0x1c, value);
		return this;
	}
	
	/**
	 * Get group ID of owner
	 */
	public int getGroupId() {
		return bb.getInt(0x20);
	}
	
	/**
	 * Set group ID of owner
	 */
	public Stat putGroupId(final int value) {
		bb.putInt(0x20, value);
		return this;
	}
	
	/**
	 * Get device ID (if special file)
	 */
	public long getRDev() {
		return bb.getLong(0x28);
	}
	
	/**
	 * Set device ID (if special file)
	 */
	public Stat putRDev(final long value) {
		bb.putLong(0x28, value);
		return this;
	}
	
	/**
	 * Get total size, in bytes
	 */
	public long getSize() {
		return bb.getLong(0x30);
	}
	
	/**
	 * Set total size, in bytes
	 */
	public Stat putSize(final long value) {
		bb.putLong(0x30, value);
		return this;
	}
	
	/**
	 * Get blocksize for file system I/O
	 */
	public long getBlkSize() {
		return bb.getLong(0x38);
	}
	
	/**
	 * Set blocksize for file system I/O
	 */
	public Stat putBlkSize(final long value) {
		bb.putLong(0x38, value);
		return this;
	}
	
	/**
	 * Get number of 512B blocks allocated
	 */
	public long getBlocks() {
		return bb.getLong(0x40);
	}
	
	/**
	 * Set number of 512B blocks allocated
	 */
	public Stat putBlocks(final long value) {
		bb.putLong(0x40, value);
		return this;
	}
	
	/**
	 * Get time of last access
	 */
	public long getAccessTime() {
		return bb.getLong(0x48);
	}
	
	/**
	 * Set time of last access
	 */
	public Stat putAccessTime(final long value) {
		bb.putLong(0x48, value);
		return this;
	}
	
	/**
	 * Get time of last modification
	 */
	public long getModTime() {
		return bb.getLong(0x58);
	}
	
	/**
	 * Set time of last modification
	 */
	public Stat putModTime(final long value) {
		bb.putLong(0x58, value);
		return this;
	}
	
	/**
	 * Get time of last status change
	 */
	public long getCTime() {
		return bb.getLong(0x68);
	}
	
	/**
	 * Set time of last status change
	 */
	public Stat putCTime(final long value) {
		bb.putLong(0x68, value);
		return this;
	}
	
}
