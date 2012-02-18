package warrenfalk.fuselaj;

public class FilesystemException extends Exception {
	final Errno errno;

	private static final long serialVersionUID = 1L;
	
	public FilesystemException(Errno errno) {
		super(errno.msg);
		this.errno = errno;
	}
	
}
