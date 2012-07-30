import java.io.File;

class JavaHelp {
	public static void main(String[] args) {
		if (args.length == 0 || "include".equals(args[0]))
			printInclude();
	}
	
	public static void printInclude() {
		String home = System.getProperty("java.home");
		File fhome = new File(home);
		File include = new File(home, "include");
		if (!include.isDirectory()) {
			fhome = fhome.getParentFile();
			include = new File(home, "include");
		}
		System.out.println("-I" + fhome + "/include -I" + fhome + "/include/linux");
	}
}