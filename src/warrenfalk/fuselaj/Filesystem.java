package warrenfalk.fuselaj;

public abstract class Filesystem {
	public int run(String[] args) {
		System.loadLibrary("fuseconnector");
		Fuselaj fuselaj = new Fuselaj();
		return fuselaj.initialize(args);
	}
}
