package warrenfalk.fuselaj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FuseContext {
	final ByteBuffer bb;
	
	public FuseContext(ByteBuffer bb) {
		this.bb = bb;
		this.bb.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public static FuseContext getCurrent() {
		try {
			return new FuseContext(Filesystem.getCurrentContext());
		}
		catch (UnsatisfiedLinkError e) {
			return null;
		}
	}
	
	/**
	 * Get Handle to fuse environment
	 */
	public long getFuseHandle() {
		return bb.getLong(0x0);
	}
	
	/**
	 * Get User ID of the calling process
	 */
	public int getUserId() {
		return bb.getInt(0x8);
	}
	
	/**
	 * Get Group ID of the calling process
	 */
	public int getGroupId() {
		return bb.getInt(0xc);
	}
	
	/**
	 * Get Thread ID of the calling process
	 */
	public int getProcessId() {
		return bb.getInt(0x10);
	}
	
	/**
	 * Get Private data returned from init()
	 */
	public Object getPrivateData() {
		return Filesystem.toObject(bb.getLong(0x18));
	}
	
	/**
	 * Get Umask of the calling process (introduced in version 2.8)
	 */
	public int getUMask() {
		return bb.getInt(0x20);
	}

}
