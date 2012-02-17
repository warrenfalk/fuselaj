package warrenfalk.fuselaj;

import java.nio.ByteBuffer;



public abstract class Filesystem {
	public int run(String[] args) {
		System.loadLibrary("fuselaj");
		Fuselaj fuselaj = new Fuselaj();
		return fuselaj.initialize(this, args);
	}
	

	@SuppressWarnings("unused")
	private final int _getattr(String path, Stat stat) {
		System.err.println("getattr(" + path + ")");
		if ("/".equals(path)) {
			stat.putMode(Mode.IFDIR | 0755);
			stat.putLinkCount(2);
		}
		else if ("/hello".equals(path)) {
			stat.putMode(Mode.IFREG | 0444);
			stat.putLinkCount(1);
			stat.putSize("Hello World!".length());
		}
		else {
			return -Status.ENOENT.code;
		}
		return -Status.OK.code;
	}

	@SuppressWarnings("unused")
	private final int _readdir(String path, DirBuffer dirBuffer, FileInfo fileInfo) {
		if (!"/".equals(path))
			return -Status.ENOENT.code;
		dirBuffer.putDir(".", 0, 0, 0);
		dirBuffer.putDir("..", 0, 0, 0);
		dirBuffer.putDir("hello", 0, 0, 0);
		return -Status.OK.code;
	}
	
	@SuppressWarnings("unused")
	private final int _open(String path, FileInfo fileInfo) {
		if (!"/hello".equals(path))
			return -Status.ENOENT.code;
		if ((fileInfo.getOpenFlags() & FileInfo.O_ACCMODE) != FileInfo.O_RDONLY)
			return -Status.EACCES.code;
		return -Status.OK.code;
	}
	
	@SuppressWarnings("unused")
	private final int _read(String path, FileInfo fileInfo, ByteBuffer buffer, long position) {
		int len;
		if (!"/hello".equals(path))
			return -Status.ENOENT.code;
		len = "Hello World!".length();
		if (position < len) {
			if (position + buffer.limit() > len)
				len -= position;
			byte[] bytes = "Hello World!".getBytes();
			buffer.put(bytes, (int)position, len);
		}
		else {
			len = 0;
		}
		return len;
	}
}
