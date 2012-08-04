package warrenfalk.fuselaj;

import java.io.IOException;

public class FilesystemException extends Exception {
	final Errno errno;

	private static final long serialVersionUID = 1L;
	
	public FilesystemException(Errno errno) {
		super(errno.msg);
		this.errno = errno;
	}
	
	public FilesystemException(IOException e) {
		super(Errno.IOError.msg + "; " + e.getMessage());
		this.errno = Errno.IOError;
	}
	
	public FilesystemException(InterruptedException e) {
		super(Errno.InterruptedSystemCall.msg + "; " + e.getMessage());
		this.errno = Errno.InterruptedSystemCall;
	}
}
