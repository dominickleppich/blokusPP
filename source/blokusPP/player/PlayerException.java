package blokusPP.player;

/**
 * Exceptions die vom Spieler ausgel&ouml;st werden k&ouml;nnen
 * 
 * @author Dominick Leppich
 *
 */
public class PlayerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	/**
	 * Konstruktor mit Fehlertext
	 * 
	 * @param s
	 *          Fehlertext
	 */
	public PlayerException(String s) {
		super(s);
	}

	/**
	 * Default-Konstruktor
	 */
	public PlayerException() {

	}
}
