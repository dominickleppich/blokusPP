package blokusPP.io;

import blokusPP.board.Board;
import blokusPP.preset.Setting;
import blokusPP.preset.Viewer;

/**
 * <h1>Ascii Text Repr&auml;sentation des Spielbretts</h1>
 * 
 * TODO
 * 
 * @author Dominick Leppich
 *
 */
public class AsciiBoard implements Setting {
	// Viewer auf das anzuzeigende Spielbrett
	private Viewer viewer;

	// ------------------------------------------------------------

	/**
	 * Erzeuge ein neues Ascii-Board
	 * 
	 * @param viewer
	 *            Viewer auf ein Spielbrett
	 */
	public AsciiBoard(Viewer viewer) {
		this.viewer = viewer;
	}

	// ------------------------------------------------------------

	/**
	 * Schreibe das Board auf die Standard-Ausgabe
	 */
	public void print() {
		System.out.print(getAsciiBoard());
	}

	/**
	 * Gib eine AsciiRepr&auml;sentation des aktuellen Spielfeldes zur&uuml;ck.
	 * Hierbei wird folgende Notation verwendet:
	 * <ul>
	 * <li><code>B</code> - Blau</li>
	 * <li><code>Y</code> - Gelb</li>
	 * <li><code>R</code> - Rot</li>
	 * <li><code>G</code> - Gr&uuml;n</li>
	 * <li><code>.</code> - Leer</li>
	 * </ul>
	 * 
	 * @return Ascii Repr&auml;sentation
	 */
	public String getAsciiBoard() {
		return AsciiBoard.getAsciiBoard(viewer);
	}

	// ------------------------------------------------------------

	/**
	 * Schreibe das Board auf die Standard-Ausgabe
	 * 
	 * @param viewer
	 *            Viewer auf das Spielbrett
	 */
	public static void print(Viewer viewer) {
		System.out.print(getAsciiBoard(viewer));
	}

	/**
	 * Gib eine AsciiRepr&auml;sentation des Spielfeldes zur&uuml;ck. Hierbei
	 * wird folgende Notation verwendet:
	 * <ul>
	 * <li><code>B</code> - Blau</li>
	 * <li><code>Y</code> - Gelb</li>
	 * <li><code>R</code> - Rot</li>
	 * <li><code>G</code> - Gr&uuml;n</li>
	 * <li><code>.</code> - Leer</li>
	 * </ul>
	 * 
	 * @param viewer
	 *            Viewer auf das Spielbrett
	 * @return Ascii Repr&auml;sentation
	 */
	public static String getAsciiBoard(Viewer viewer) {
		String s = "";
		s += "    A B C D E F G H I J K L M N O P Q R S T\n\n";
		for (int number = 0; number < Board.SIZE; number++) {
			s += ((1 + number) < 10 ? " " : "") + (1 + number) + "  ";
			for (int letter = 0; letter < Board.SIZE; letter++) {
				switch (viewer.getColor(letter, number)) {
				case BLUE:
					s += "B";
					break;
				case YELLOW:
					s += "Y";
					break;
				case RED:
					s += "R";
					break;
				case GREEN:
					s += "G";
					break;
				case NONE:
					s += ".";
					break;
				default:
					s += " ";
				}
				s += " ";
			}
			s += "\n";
		}
		return s;
	}
}
