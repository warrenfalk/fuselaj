package warrenfalk.fuselaj;

public class Mode {
	/* Directory. */
	public static final int IFDIR = 0040000;
	/* Character device. */
	public static final int IFCHR = 0020000;
	/* Block device. */
	public static final int IFBLK = 0060000;
	/* Regular file. */
	public static final int IFREG = 0100000;
	/* FIFO. */
	public static final int IFIFO = 0010000;
	/* Symbolic link. */
	public static final int IFLNK = 0120000;
	/* Socket. */
	public static final int IFSOCK = 0140000;

}
