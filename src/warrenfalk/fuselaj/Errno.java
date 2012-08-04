package warrenfalk.fuselaj;

public enum Errno {
	/** Operation not permitted */
	OperationNotPermitted(1, "EPERM", "Operation not permitted"),
	/** ENOENT: No such file or directory */
	NoSuchFileOrDirectory(2, "ENOENT", "No such file or directory"),
	/** ESRCH: No such process */
	NoSuchProcess(3, "ESRCH", "No such process"),
	/** EINTR: Interrupted system call */
	InterruptedSystemCall(4, "EINTR", "Interrupted system call"),
	/** EIO: I/O error */
	IOError(5, "EIO", "I/O error"),
	/** ENXIO: No such device or address */
	NoSuchDeviceOrAddress(6, "ENXIO", "No such device or address"),
	/** E2BIG: Argument list too long */
	ArgumentListTooLong(7, "E2BIG", "Argument list too long"),
	/** ENOEXEC: Exec format error */
	ExecFormatError(8, "ENOEXEC", "Exec format error"),
	/** EBADF: Bad file number */
	BadFileNumber(9, "EBADF", "Bad file number"),
	/** ECHILD: No child processes */
	NoChildProcesses(10, "ECHILD", "No child processes"),
	/** EAGAIN: Try again */
	TryAgain(11, "EAGAIN", "Try again"),
	/** ENOMEM: Out of memory */
	OutOfMemory(12, "ENOMEM", "Out of memory"),
	/** EACCES: Permission denied */
	PermissionDenied(13, "EACCES", "Permission denied"),
	/** EFAULT: Bad address */
	BadAddress(14, "EFAULT", "Bad address"),
	/** ENOTBLK: Block device required */
	BlockDeviceRequired(15, "ENOTBLK", "Block device required"),
	/** EBUSY: Device or resource busy */
	DeviceOrResourceBusy(16, "EBUSY", "Device or resource busy"),
	/** EEXIST: File exists */
	FileExists(17, "EEXIST", "File exists"),
	/** EXDEV: Cross-device link */
	CrossDeviceLink(18, "EXDEV", "Cross-device link"),
	/** ENODEV: No such device */
	NoSuchDevice(19, "ENODEV", "No such device"),
	/** ENOTDIR: Not a directory */
	NotADirectory(20, "ENOTDIR", "Not a directory"),
	/** EISDIR: Is a directory */
	IsADirectory(21, "EISDIR", "Is a directory"),
	/** EINVAL: Invalid argument */
	InvalidArgument(22, "EINVAL", "Invalid argument"),
	/** ENFILE: File table overflow */
	FileTableOverflow(23, "ENFILE", "File table overflow"),
	/** EMFILE: Too many open files */
	TooManyOpenFiles(24, "EMFILE", "Too many open files"),
	/** ENOTTY: Not a typewriter */
	NotATypewriter(25, "ENOTTY", "Not a typewriter"),
	/** ETXTBSY: Text file busy */
	TextFileBusy(26, "ETXTBSY", "Text file busy"),
	/** EFBIG: File too large */
	FileTooLarge(27, "EFBIG", "File too large"),
	/** ENOSPC: No space left on device */
	NoSpaceLeftOnDevice(28, "ENOSPC", "No space left on device"),
	/** ESPIPE: Illegal seek */
	IllegalSeek(29, "ESPIPE", "Illegal seek"),
	/** EROFS: Read-only file system */
	ReadOnlyFileSystem(30, "EROFS", "Read-only file system"),
	/** EMLINK: Too many links */
	TooManyLinks(31, "EMLINK", "Too many links"),
	/** EPIPE: Broken pipe */
	BrokenPipe(32, "EPIPE", "Broken pipe"),
	/** EDOM: Math argument out of domain of func */
	MathArgumentOutOfDomainOfFunc(33, "EDOM", "Math argument out of domain of func"),
	/** ERANGE: Math result not representable */
	MathResultNotRepresentable(34, "ERANGE", "Math result not representable"),
	/** EDEADLK: Resource deadlock would occur */
	ResourceDeadlockWouldOccur(35, "EDEADLK", "Resource deadlock would occur"),
	/** ENAMETOOLONG: File name too long */
	FileNameTooLong(36, "ENAMETOOLONG", "File name too long"),
	/** ENOLCK: No record locks available */
	NoRecordLocksAvailable(37, "ENOLCK", "No record locks available"),
	/** ENOSYS: Function not implemented */
	FunctionNotImplemented(38, "ENOSYS", "Function not implemented"),
	/** ENOTEMPTY: Directory not empty */
	DirectoryNotEmpty(39, "ENOTEMPTY", "Directory not empty"),
	/** ELOOP: Too many symbolic links encountered */
	TooManySymbolicLinksEncountered(40, "ELOOP", "Too many symbolic links encountered"),
	/** EWOULDBLOCK: Operation would block */
	OperationWouldBlock(11, "EWOULDBLOCK", "Operation would block"),
	/** ENOMSG: No message of desired type */
	NoMessageOfDesiredType(42, "ENOMSG", "No message of desired type"),
	/** EIDRM: Identifier removed */
	IdentifierRemoved(43, "EIDRM", "Identifier removed"),
	/** ECHRNG: Channel number out of range */
	ChannelNumberOutOfRange(44, "ECHRNG", "Channel number out of range"),
	/** EL2NSYNC: Level 2 not synchronized */
	Level2NotSynchronized(45, "EL2NSYNC", "Level 2 not synchronized"),
	/** EL3HLT: Level 3 halted */
	Level3Halted(46, "EL3HLT", "Level 3 halted"),
	/** EL3RST: Level 3 reset */
	Level3Reset(47, "EL3RST", "Level 3 reset"),
	/** ELNRNG: Link number out of range */
	LinkNumberOutOfRange(48, "ELNRNG", "Link number out of range"),
	/** EUNATCH: Protocol driver not attached */
	ProtocolDriverNotAttached(49, "EUNATCH", "Protocol driver not attached"),
	/** ENOCSI: No CSI structure available */
	NoCSIStructureAvailable(50, "ENOCSI", "No CSI structure available"),
	/** EL2HLT: Level 2 halted */
	Level2Halted(51, "EL2HLT", "Level 2 halted"),
	/** EBADE: Invalid exchange */
	InvalidExchange(52, "EBADE", "Invalid exchange"),
	/** EBADR: Invalid request descriptor */
	InvalidRequestDescriptor(53, "EBADR", "Invalid request descriptor"),
	/** EXFULL: Exchange full */
	ExchangeFull(54, "EXFULL", "Exchange full"),
	/** ENOANO: No anode */
	NoAnode(55, "ENOANO", "No anode"),
	/** EBADRQC: Invalid request code */
	InvalidRequestCode(56, "EBADRQC", "Invalid request code"),
	/** EBADSLT: Invalid slot */
	InvalidSlot(57, "EBADSLT", "Invalid slot"),
	/** EBFONT: Bad font file format */
	BadFontFileFormat(59, "EBFONT", "Bad font file format"),
	/** ENOSTR: Device not a stream */
	DeviceNotAStream(60, "ENOSTR", "Device not a stream"),
	/** ENODATA: No data available */
	NoDataAvailable(61, "ENODATA", "No data available"),
	/** ETIME: Timer expired */
	TimerExpired(62, "ETIME", "Timer expired"),
	/** ENOSR: Out of streams resources */
	OutOfStreamsResources(63, "ENOSR", "Out of streams resources"),
	/** ENONET: Machine is not on the network */
	MachineIsNotOnTheNetwork(64, "ENONET", "Machine is not on the network"),
	/** ENOPKG: Package not installed */
	PackageNotInstalled(65, "ENOPKG", "Package not installed"),
	/** EREMOTE: Object is remote */
	ObjectIsRemote(66, "EREMOTE", "Object is remote"),
	/** ENOLINK: Link has been severed */
	LinkHasBeenSevered(67, "ENOLINK", "Link has been severed"),
	/** EADV: Advertise error */
	AdvertiseError(68, "EADV", "Advertise error"),
	/** ESRMNT: Srmount error */
	SrmountError(69, "ESRMNT", "Srmount error"),
	/** ECOMM: Communication error on send */
	CommunicationErrorOnSend(70, "ECOMM", "Communication error on send"),
	/** EPROTO: Protocol error */
	ProtocolError(71, "EPROTO", "Protocol error"),
	/** EMULTIHOP: Multihop attempted */
	MultihopAttempted(72, "EMULTIHOP", "Multihop attempted"),
	/** EDOTDOT: RFS specific error */
	RFSSpecificError(73, "EDOTDOT", "RFS specific error"),
	/** EBADMSG: Not a data message */
	NotADataMessage(74, "EBADMSG", "Not a data message"),
	/** EOVERFLOW: Value too large for defined data type */
	ValueTooLargeForDefinedDataType(75, "EOVERFLOW", "Value too large for defined data type"),
	/** ENOTUNIQ: Name not unique on network */
	NameNotUniqueOnNetwork(76, "ENOTUNIQ", "Name not unique on network"),
	/** EBADFD: File descriptor in bad state */
	FileDescriptorInBadState(77, "EBADFD", "File descriptor in bad state"),
	/** EREMCHG: Remote address changed */
	RemoteAddressChanged(78, "EREMCHG", "Remote address changed"),
	/** ELIBACC: Can not access a needed shared library */
	CanNotAccessANeededSharedLibrary(79, "ELIBACC", "Can not access a needed shared library"),
	/** ELIBBAD: Accessing a corrupted shared library */
	AccessingACorruptedSharedLibrary(80, "ELIBBAD", "Accessing a corrupted shared library"),
	/** ELIBSCN: .lib section in a.out corrupted */
	LibSectionInAOutCorrupted(81, "ELIBSCN", ".lib section in a.out corrupted"),
	/** ELIBMAX: Attempting to link in too many shared libraries */
	AttemptingToLinkInTooManySharedLibraries(82, "ELIBMAX", "Attempting to link in too many shared libraries"),
	/** ELIBEXEC: Cannot exec a shared library directly */
	CannotExecASharedLibraryDirectly(83, "ELIBEXEC", "Cannot exec a shared library directly"),
	/** EILSEQ: Illegal byte sequence */
	IllegalByteSequence(84, "EILSEQ", "Illegal byte sequence"),
	/** ERESTART: Interrupted system call should be restarted */
	InterruptedSystemCallShouldBeRestarted(85, "ERESTART", "Interrupted system call should be restarted"),
	/** ESTRPIPE: Streams pipe error */
	StreamsPipeError(86, "ESTRPIPE", "Streams pipe error"),
	/** EUSERS: Too many users */
	TooManyUsers(87, "EUSERS", "Too many users"),
	/** ENOTSOCK: Socket operation on non-socket */
	SocketOperationOnNonSocket(88, "ENOTSOCK", "Socket operation on non-socket"),
	/** EDESTADDRREQ: Destination address required */
	DestinationAddressRequired(89, "EDESTADDRREQ", "Destination address required"),
	/** EMSGSIZE: Message too long */
	MessageTooLong(90, "EMSGSIZE", "Message too long"),
	/** EPROTOTYPE: Protocol wrong type for socket */
	ProtocolWrongTypeForSocket(91, "EPROTOTYPE", "Protocol wrong type for socket"),
	/** ENOPROTOOPT: Protocol not available */
	ProtocolNotAvailable(92, "ENOPROTOOPT", "Protocol not available"),
	/** EPROTONOSUPPORT: Protocol not supported */
	ProtocolNotSupported(93, "EPROTONOSUPPORT", "Protocol not supported"),
	/** ESOCKTNOSUPPORT: Socket type not supported */
	SocketTypeNotSupported(94, "ESOCKTNOSUPPORT", "Socket type not supported"),
	/** EOPNOTSUPP: Operation not supported on transport endpoint */
	OperationNotSupportedOnTransportEndpoint(95, "EOPNOTSUPP", "Operation not supported on transport endpoint"),
	/** EPFNOSUPPORT: Protocol family not supported */
	ProtocolFamilyNotSupported(96, "EPFNOSUPPORT", "Protocol family not supported"),
	/** EAFNOSUPPORT: Address family not supported by protocol */
	AddressFamilyNotSupportedByProtocol(97, "EAFNOSUPPORT", "Address family not supported by protocol"),
	/** EADDRINUSE: Address already in use */
	AddressAlreadyInUse(98, "EADDRINUSE", "Address already in use"),
	/** EADDRNOTAVAIL: Cannot assign requested address */
	CannotAssignRequestedAddress(99, "EADDRNOTAVAIL", "Cannot assign requested address"),
	/** ENETDOWN: Network is down */
	NetworkIsDown(100, "ENETDOWN", "Network is down"),
	/** ENETUNREACH: Network is unreachable */
	NetworkIsUnreachable(101, "ENETUNREACH", "Network is unreachable"),
	/** ENETRESET: Network dropped connection because of reset */
	NetworkDroppedConnectionBecauseOfReset(102, "ENETRESET", "Network dropped connection because of reset"),
	/** ECONNABORTED: Software caused connection abort */
	SoftwareCausedConnectionAbort(103, "ECONNABORTED", "Software caused connection abort"),
	/** ECONNRESET: Connection reset by peer */
	ConnectionResetByPeer(104, "ECONNRESET", "Connection reset by peer"),
	/** ENOBUFS: No buffer space available */
	NoBufferSpaceAvailable(105, "ENOBUFS", "No buffer space available"),
	/** EISCONN: Transport endpoint is already connected */
	TransportEndpointIsAlreadyConnected(106, "EISCONN", "Transport endpoint is already connected"),
	/** ENOTCONN: Transport endpoint is not connected */
	TransportEndpointIsNotConnected(107, "ENOTCONN", "Transport endpoint is not connected"),
	/** ESHUTDOWN: Cannot send after transport endpoint shutdown */
	CannotSendAfterTransportEndpointShutdown(108, "ESHUTDOWN", "Cannot send after transport endpoint shutdown"),
	/** ETOOMANYREFS: Too many references: cannot splice */
	TooManyReferencesCannotSplice(109, "ETOOMANYREFS", "Too many references: cannot splice"),
	/** ETIMEDOUT: Connection timed out */
	ConnectionTimedOut(110, "ETIMEDOUT", "Connection timed out"),
	/** ECONNREFUSED: Connection refused */
	ConnectionRefused(111, "ECONNREFUSED", "Connection refused"),
	/** EHOSTDOWN: Host is down */
	HostIsDown(112, "EHOSTDOWN", "Host is down"),
	/** EHOSTUNREACH: No route to host */
	NoRouteToHost(113, "EHOSTUNREACH", "No route to host"),
	/** EALREADY: Operation already in progress */
	OperationAlreadyInProgress(114, "EALREADY", "Operation already in progress"),
	/** EINPROGRESS: Operation now in progress */
	OperationNowInProgress(115, "EINPROGRESS", "Operation now in progress"),
	/** ESTALE: Stale NFS file handle */
	StaleNFSFileHandle(116, "ESTALE", "Stale NFS file handle"),
	/** EUCLEAN: Structure needs cleaning */
	StructureNeedsCleaning(117, "EUCLEAN", "Structure needs cleaning"),
	/** ENOTNAM: Not a XENIX named type file */
	NotAXENIXNamedTypeFile(118, "ENOTNAM", "Not a XENIX named type file"),
	/** ENAVAIL: No XENIX semaphores available */
	NoXENIXSemaphoresAvailable(119, "ENAVAIL", "No XENIX semaphores available"),
	/** EISNAM: Is a named type file */
	IsANamedTypeFile(120, "EISNAM", "Is a named type file"),
	/** EREMOTEIO: Remote I/O error */
	RemoteIOError(121, "EREMOTEIO", "Remote I/O error"),
	/** EDQUOT: Quota exceeded */
	QuotaExceeded(122, "EDQUOT", "Quota exceeded"),
	/** ENOMEDIUM: No medium found */
	NoMediumFound(123, "ENOMEDIUM", "No medium found"),
	/** EMEDIUMTYPE: Wrong medium type */
	WrongMediumType(124, "EMEDIUMTYPE", "Wrong medium type"),
	/** ECANCELED: Operation Canceled */
	OperationCanceled(125, "ECANCELED", "Operation Canceled"),
	/** ENOKEY: Required key not available */
	RequiredKeyNotAvailable(126, "ENOKEY", "Required key not available"),
	/** EKEYEXPIRED: Key has expired */
	KeyHasExpired(127, "EKEYEXPIRED", "Key has expired"),
	/** EKEYREVOKED: Key has been revoked */
	KeyHasBeenRevoked(128, "EKEYREVOKED", "Key has been revoked"),
	/** EKEYREJECTED: Key was rejected by service */
	KeyWasRejectedByService(129, "EKEYREJECTED", "Key was rejected by service"),
	/** EOWNERDEAD: Owner died */
	OwnerDied(130, "EOWNERDEAD", "Owner died"),
	/** ENOTRECOVERABLE: State not recoverable */
	StateNotRecoverable(131, "ENOTRECOVERABLE", "State not recoverable"),
	/** ERFKILL: Operation not possible due to RF-kill */
	OperationNotPossibleDueToRfKill(132, "ERFKILL", "Operation not possible due to RF-kill"),
	/** EHWPOISON: Memory page has hardware error */
	MemoryPageHasHardwareError(133, "EHWPOISON", "Memory page has hardware error");
	
	final int code;
	final String key;
	final String msg;
	final static Errno[] values = buildValues();
	
	Errno(int code, String key, String msg) {
		this.code = code;
		this.key = key;
		this.msg = msg;
	}
	
	private static Errno[] buildValues() {
		Errno[] es = values();
		int maxCode = 0;
		for (Errno e : es)
			if (maxCode < e.code)
				maxCode = e.code;
		Errno[] map = new Errno[maxCode + 1];
		for (Errno e : es)
			map[e.code] = e;
		return map;
	}
	
	public static Errno fromCode(int code) {
		return values[code];
	}
}
