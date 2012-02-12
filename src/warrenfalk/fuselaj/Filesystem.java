package warrenfalk.fuselaj;


public abstract class Filesystem {
	public int run(String[] args) {
		System.loadLibrary("fuselaj");
		Fuselaj fuselaj = new Fuselaj();
		return fuselaj.initialize(this, args);
	}

	protected final String getPath() {
		return Fuselaj.getCurrentPath();
	}

	public void readdir() {
		System.out.println("readdir(" + getPath() + ")");
	}
}
