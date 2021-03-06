package warrenfalk.fuselaj;

public final class DirBuffer {
	private final long buffer;
	private final long filler;
	private long position;
	
	public DirBuffer(long buffer, long filler, long position) {
		this.buffer = buffer;
		this.filler = filler;
		this.position = position;
	}
	
	public long getPosition() {
		return position;
	}

	public native boolean putDir(String name, long inode, int mode, long position);
	
	public native boolean putDir(String name, long position);

}
