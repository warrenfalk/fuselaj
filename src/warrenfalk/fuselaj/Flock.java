package warrenfalk.fuselaj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Flock {
	final ByteBuffer bb;
	
	public Flock(ByteBuffer bb) {
		this.bb = bb;
		this.bb.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Get Type of lock: F_RDLCK, F_WRLCK, or F_UNLCK.
	 */
	public short getType() {
		return bb.getShort(0x0);
	}
	
	/**
	 * Set Type of lock: F_RDLCK, F_WRLCK, or F_UNLCK.
	 */
	public Flock putType(final short value) {
		bb.putShort(0x0, value);
		return this;
	}
	
	/**
	 * Get Where `l_start' is relative to (like `lseek'). 
	 */
	public short getWhence() {
		return bb.getShort(0x2);
	}
	
	/**
	 * Set Where `l_start' is relative to (like `lseek'). 
	 */
	public Flock putWhence(final short value) {
		bb.putShort(0x2, value);
		return this;
	}
	
	/**
	 * Get Offset where the lock begins. 
	 */
	public long getStart() {
		return bb.getLong(0x8);
	}
	
	/**
	 * Set Offset where the lock begins. 
	 */
	public Flock putStart(final long value) {
		bb.putLong(0x8, value);
		return this;
	}
	
	/**
	 * Get Size of the locked area; zero means until EOF. 
	 */
	public long getLength() {
		return bb.getLong(0x10);
	}
	
	/**
	 * Set Size of the locked area; zero means until EOF. 
	 */
	public Flock putLength(final long value) {
		bb.putLong(0x10, value);
		return this;
	}
	
	/**
	 * Get Process holding the lock. 
	 */
	public int getPid() {
		return bb.getInt(0x18);
	}
	
	/**
	 * Set Process holding the lock. 
	 */
	public Flock putPid(final int value) {
		bb.putInt(0x18, value);
		return this;
	}
}
