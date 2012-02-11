package warrenfalk.fuselaj;

public class Fuselaj {
	
	native void initialize();

	public static void main(String[] args) {
		System.loadLibrary("fuseconnector");
		Fuselaj fuselaj = new Fuselaj();
		fuselaj.initialize();
	}

}
