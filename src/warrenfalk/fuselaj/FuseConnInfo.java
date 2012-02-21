package warrenfalk.fuselaj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FuseConnInfo {
	final ByteBuffer bb;
	
	public FuseConnInfo(ByteBuffer bb) {
		this.bb = bb;
		this.bb.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Get Major version of the protocol (read-only)
	 */
	public int getProtoMajor() {
		return bb.getInt(0x0);
	}
	
	/**
	 * Set Major version of the protocol (read-only)
	 */
	public FuseConnInfo putProtoMajor(final int value) {
		bb.putInt(0x0, value);
		return this;
	}
	
	/**
	 * Get Minor version of the protocol (read-only)
	 */
	public int getProtoMinor() {
		return bb.getInt(0x4);
	}
	
	/**
	 * Set Minor version of the protocol (read-only)
	 */
	public FuseConnInfo putProtoMinor(final int value) {
		bb.putInt(0x4, value);
		return this;
	}
	
	/**
	 * Get Is asynchronous read supported (read-write)
	 */
	public int getAsyncRead() {
		return bb.getInt(0x8);
	}
	
	/**
	 * Set Is asynchronous read supported (read-write)
	 */
	public FuseConnInfo putAsyncRead(final int value) {
		bb.putInt(0x8, value);
		return this;
	}
	
	/**
	 * Get Maximum size of the write buffer
	 */
	public int getMaxWrite() {
		return bb.getInt(0xc);
	}
	
	/**
	 * Set Maximum size of the write buffer
	 */
	public FuseConnInfo putMaxWrite(final int value) {
		bb.putInt(0xc, value);
		return this;
	}
	
	/**
	 * Get Maximum readahead
	 */
	public int getMaxReadahead() {
		return bb.getInt(0x10);
	}
	
	/**
	 * Set Maximum readahead
	 */
	public FuseConnInfo putMaxReadahead(final int value) {
		bb.putInt(0x10, value);
		return this;
	}
	
	/**
	 * Get Capability flags, that the kernel supports
	 */
	public int getCapable() {
		return bb.getInt(0x14);
	}
	
	/**
	 * Set Capability flags, that the kernel supports
	 */
	public FuseConnInfo putCapable(final int value) {
		bb.putInt(0x14, value);
		return this;
	}
	
	/**
	 * Get Capability flags, that the filesystem wants to enable
	 */
	public int getWant() {
		return bb.getInt(0x18);
	}
	
	/**
	 * Set Capability flags, that the filesystem wants to enable
	 */
	public FuseConnInfo putWant(final int value) {
		bb.putInt(0x18, value);
		return this;
	}
	
}

