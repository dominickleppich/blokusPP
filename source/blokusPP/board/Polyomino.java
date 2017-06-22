package blokusPP.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import blokusPP.preset.Move;
import blokusPP.preset.Position;

/**
 * <h1>Polyomino Klasse</h1>
 * 
 * <h2>Konstanten</h2>
 * <p>
 * Diese Klasse enth&auml;lt die 21 konstanten Polyomino Steine bestehend aus
 * einem 1-Polyomino, einem 2-Polyomino, zwei 3-Polyominos, f&uuml;nf
 * 4-Polyominos und zw&ouml;lf 5-Polyominos. Benannt wie folgt:
 * <ul>
 * <li><code>POLY_1</code></li>
 * <li><code>POLY_2</code></li>
 * <li><code>POLY_3</code>, <code>POLY_v</code></li>
 * <li><code>POLY_4</code>, <code>POLY_l</code>, <code>POLY_o</code>,
 * <code>POLY_t</code>, <code>POLY_z</code></li>
 * <li><code>POLY_5</code>, <code>POLY_F</code>, <code>POLY_L</code>,
 * <code>POLY_N</code>, <code>POLY_P</code>, <code>POLY_T</code>,
 * <code>POLY_U</code>, <code>POLY_V</code>, <code>POLY_W</code>,
 * <code>POLY_X</code>, <code>POLY_Y</code>, <code>POLY_Z</code></li>
 * </ul>
 * </p>
 * 
 * <h2>Transformationen</h2>
 * <p>
 * Jedes Polyomino l&auml;sst sich um 90, 180 oder 270 Grad drehen, horizontal
 * und vertikal spiegeln sowie beliebig verschieben. Nach jeder Transformation
 * bekommt man ein neues Polyomino Objekt zur&uuml;ck. Hierf&uuml;r sind
 * folgende Methoden vorgesehen:
 * <ul>
 * <li>Drehen: <code>rotate(degree)</code> (<code>DEGREE_90</code>,
 * <code>DEGREE_180</code>, <code>DEGREE_270</code>)</li>
 * <li>Spiegeln: <code>flip(mode)</code> (<code>FLIP_HORIZONTAL</code>,
 * <code>FLIP_VERTICAL</code>)</li>
 * <li>Verschieben: <code>move(letter, number)</code></li>
 * </ul>
 * </p>
 * 
 * @author Christoph Rauterberg, Dominick Leppich
 *
 */
public class Polyomino extends Move {
	private static final long serialVersionUID = 1L;

	public static final int ROTATE_90 = 1;
	public static final int ROTATE_180 = 2;
	public static final int ROTATE_270 = 3;

	public static final int FLIP_HORIZONTAL = 4;
	public static final int FLIP_VERTICAL = 5;

	// 21 verschiedene Polyominos als Konstanten
	public static final Polyomino POLY_1;
	public static final Polyomino POLY_2;
	public static final Polyomino POLY_3;
	public static final Polyomino POLY_v;
	public static final Polyomino POLY_4;
	public static final Polyomino POLY_l;
	public static final Polyomino POLY_o;
	public static final Polyomino POLY_t;
	public static final Polyomino POLY_z;
	public static final Polyomino POLY_5;
	public static final Polyomino POLY_F;
	public static final Polyomino POLY_L;
	public static final Polyomino POLY_N;
	public static final Polyomino POLY_P;
	public static final Polyomino POLY_T;
	public static final Polyomino POLY_U;
	public static final Polyomino POLY_V;
	public static final Polyomino POLY_W;
	public static final Polyomino POLY_X;
	public static final Polyomino POLY_Y;
	public static final Polyomino POLY_Z;

	// ------------------------------------------------------------

	/**
	 * Erzeuge konstante Polyominos
	 */
	static {
		POLY_1 = new Polyomino();
		POLY_2 = new Polyomino();
		POLY_3 = new Polyomino();
		POLY_v = new Polyomino();
		POLY_4 = new Polyomino();
		POLY_l = new Polyomino();
		POLY_o = new Polyomino();
		POLY_t = new Polyomino();
		POLY_z = new Polyomino();
		POLY_5 = new Polyomino();
		POLY_F = new Polyomino();
		POLY_L = new Polyomino();
		POLY_N = new Polyomino();
		POLY_P = new Polyomino();
		POLY_T = new Polyomino();
		POLY_U = new Polyomino();
		POLY_V = new Polyomino();
		POLY_W = new Polyomino();
		POLY_X = new Polyomino();
		POLY_Y = new Polyomino();
		POLY_Z = new Polyomino();

		// Initialisiere alle 21 Polyominos
		POLY_1.addPosition(new Position(0, 0));

		POLY_2.addPosition(new Position(0, 0));
		POLY_2.addPosition(new Position(0, 1));

		POLY_3.addPosition(new Position(0, 0));
		POLY_3.addPosition(new Position(0, 1));
		POLY_3.addPosition(new Position(0, 2));

		POLY_v.addPosition(new Position(0, 0));
		POLY_v.addPosition(new Position(0, 1));
		POLY_v.addPosition(new Position(1, 1));

		POLY_4.addPosition(new Position(0, 0));
		POLY_4.addPosition(new Position(0, 1));
		POLY_4.addPosition(new Position(0, 2));
		POLY_4.addPosition(new Position(0, 3));

		POLY_l.addPosition(new Position(0, 0));
		POLY_l.addPosition(new Position(0, 1));
		POLY_l.addPosition(new Position(0, 2));
		POLY_l.addPosition(new Position(1, 2));

		POLY_o.addPosition(new Position(0, 0));
		POLY_o.addPosition(new Position(0, 1));
		POLY_o.addPosition(new Position(1, 0));
		POLY_o.addPosition(new Position(1, 1));

		POLY_t.addPosition(new Position(0, 0));
		POLY_t.addPosition(new Position(1, 0));
		POLY_t.addPosition(new Position(2, 0));
		POLY_t.addPosition(new Position(1, 1));

		POLY_z.addPosition(new Position(0, 0));
		POLY_z.addPosition(new Position(1, 0));
		POLY_z.addPosition(new Position(1, 1));
		POLY_z.addPosition(new Position(2, 1));

		POLY_5.addPosition(new Position(0, 0));
		POLY_5.addPosition(new Position(0, 1));
		POLY_5.addPosition(new Position(0, 2));
		POLY_5.addPosition(new Position(0, 3));
		POLY_5.addPosition(new Position(0, 4));

		POLY_F.addPosition(new Position(1, 0));
		POLY_F.addPosition(new Position(2, 0));
		POLY_F.addPosition(new Position(0, 1));
		POLY_F.addPosition(new Position(1, 1));
		POLY_F.addPosition(new Position(1, 2));

		POLY_L.addPosition(new Position(0, 0));
		POLY_L.addPosition(new Position(0, 1));
		POLY_L.addPosition(new Position(0, 2));
		POLY_L.addPosition(new Position(0, 3));
		POLY_L.addPosition(new Position(1, 3));

		POLY_N.addPosition(new Position(1, 0));
		POLY_N.addPosition(new Position(0, 1));
		POLY_N.addPosition(new Position(1, 1));
		POLY_N.addPosition(new Position(0, 2));
		POLY_N.addPosition(new Position(0, 3));

		POLY_P.addPosition(new Position(0, 0));
		POLY_P.addPosition(new Position(0, 1));
		POLY_P.addPosition(new Position(1, 0));
		POLY_P.addPosition(new Position(1, 1));
		POLY_P.addPosition(new Position(0, 2));

		POLY_T.addPosition(new Position(0, 0));
		POLY_T.addPosition(new Position(1, 0));
		POLY_T.addPosition(new Position(2, 0));
		POLY_T.addPosition(new Position(1, 1));
		POLY_T.addPosition(new Position(1, 2));

		POLY_U.addPosition(new Position(0, 0));
		POLY_U.addPosition(new Position(2, 0));
		POLY_U.addPosition(new Position(0, 1));
		POLY_U.addPosition(new Position(1, 1));
		POLY_U.addPosition(new Position(2, 1));

		POLY_V.addPosition(new Position(0, 0));
		POLY_V.addPosition(new Position(0, 1));
		POLY_V.addPosition(new Position(0, 2));
		POLY_V.addPosition(new Position(1, 2));
		POLY_V.addPosition(new Position(2, 2));

		POLY_W.addPosition(new Position(0, 0));
		POLY_W.addPosition(new Position(0, 1));
		POLY_W.addPosition(new Position(1, 1));
		POLY_W.addPosition(new Position(1, 2));
		POLY_W.addPosition(new Position(2, 2));

		POLY_X.addPosition(new Position(1, 0));
		POLY_X.addPosition(new Position(0, 1));
		POLY_X.addPosition(new Position(1, 1));
		POLY_X.addPosition(new Position(2, 1));
		POLY_X.addPosition(new Position(1, 2));

		POLY_Y.addPosition(new Position(1, 0));
		POLY_Y.addPosition(new Position(0, 1));
		POLY_Y.addPosition(new Position(1, 1));
		POLY_Y.addPosition(new Position(1, 2));
		POLY_Y.addPosition(new Position(1, 3));

		POLY_Z.addPosition(new Position(0, 0));
		POLY_Z.addPosition(new Position(1, 0));
		POLY_Z.addPosition(new Position(1, 1));
		POLY_Z.addPosition(new Position(1, 2));
		POLY_Z.addPosition(new Position(2, 2));

		// Fuehre Dimensionsberechnungen durch
		POLY_1.calculateDimensions();
		POLY_2.calculateDimensions();
		POLY_3.calculateDimensions();
		POLY_v.calculateDimensions();
		POLY_4.calculateDimensions();
		POLY_l.calculateDimensions();
		POLY_o.calculateDimensions();
		POLY_t.calculateDimensions();
		POLY_z.calculateDimensions();
		POLY_5.calculateDimensions();
		POLY_F.calculateDimensions();
		POLY_L.calculateDimensions();
		POLY_N.calculateDimensions();
		POLY_P.calculateDimensions();
		POLY_T.calculateDimensions();
		POLY_U.calculateDimensions();
		POLY_V.calculateDimensions();
		POLY_W.calculateDimensions();
		POLY_X.calculateDimensions();
		POLY_Y.calculateDimensions();
		POLY_Z.calculateDimensions();
	}

	/**
	 * Liefere eine ArrayList aller m&ouml;glichen Polyominos
	 * 
	 * @return ArrayList aller Steiner
	 */
	public static ArrayList<Polyomino> getAllPolyominos() {
		ArrayList<Polyomino> res = new ArrayList<Polyomino>();
		res.add(POLY_1);
		res.add(POLY_2);
		res.add(POLY_3);
		res.add(POLY_v);
		res.add(POLY_4);
		res.add(POLY_l);
		res.add(POLY_o);
		res.add(POLY_t);
		res.add(POLY_z);
		res.add(POLY_5);
		res.add(POLY_F);
		res.add(POLY_L);
		res.add(POLY_N);
		res.add(POLY_P);
		res.add(POLY_T);
		res.add(POLY_U);
		res.add(POLY_V);
		res.add(POLY_W);
		res.add(POLY_X);
		res.add(POLY_Y);
		res.add(POLY_Z);
		return res;
	}

	// ------------------------------------------------------------

	private int width, height, minLetter, maxLetter, minNumber, maxNumber;

	/**
	 * Erzeuge leeres Polyomino
	 */
	public Polyomino() {
		resetDimensions();
	}

	/**
	 * Erzeuge Polyomino mit &uuml;bergebenen Positionen
	 * 
	 * @param poly
	 *            Positionen
	 */
	public Polyomino(HashSet<Position> poly) {
		for (Position p : poly)
			addPosition(p);
		calculateDimensions();
	}

	// ------------------------------------------------------------

	/**
	 * Setze Dimensionen zur&uuml;ck
	 */
	private void resetDimensions() {
		width = 0;
		height = 0;
		minLetter = Board.SIZE;
		maxLetter = -1;
		minNumber = Board.SIZE;
		maxLetter = -1;
	}

	/**
	 * Berechne Gr&ouml;&szlig;e des Polyominos
	 */
	private void calculateDimensions() {
		resetDimensions();
		// Bestimme alle Minima und Maxima
		for (Position p : getPolyomino()) {
			if (p.getLetter() < minLetter)
				minLetter = p.getLetter();
			if (p.getLetter() > maxLetter)
				maxLetter = p.getLetter();
			if (p.getNumber() < minNumber)
				minNumber = p.getNumber();
			if (p.getNumber() > maxNumber)
				maxNumber = p.getNumber();
		}

		width = maxLetter - minLetter + 1;
		height = maxNumber - minNumber + 1;
	}

	/**
	 * Gib die Anzahl der Quadrate des Polyominos zur&uuml;ck
	 * 
	 * @return Anzahl Quadrate
	 */
	public int size() {
		return getPolyomino().size();
	}

	/**
	 * Gib die Breite an Quadraten des Polyominos zur&uuml;ck
	 * 
	 * @return Breite
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gib die H&ouml;he an Quadraten des Polyominos zur&uuml;ck
	 * 
	 * @return H&ouml;he
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gib den kleinsten Buchstaben zur&uuml;ck
	 * 
	 * @return kleinster Buchstabe
	 */
	public int getMinLetter() {
		return minLetter;
	}

	/**
	 * Gib den gr&ouml;&szlig;ten Buchstaben zur&uuml;ck
	 * 
	 * @return gr&ouml;&szlig;ter Buchstabe
	 */
	public int getMaxLetter() {
		return maxLetter;
	}

	/**
	 * Gib die kleinste Zahl zur&uuml;ck
	 * 
	 * @return kleinste Zahl
	 */
	public int getMinNumber() {
		return minNumber;
	}

	/**
	 * Gib die gr&ouml;&szlig;te Zahl zur&uuml;ck
	 * 
	 * @return gr&ouml;&szlig;te Zahl
	 */
	public int getMaxNumber() {
		return maxNumber;
	}

	// ------------------------------------------------------------

	/**
	 * Drehe ein Polyomino und gib eine gedrehte neue Instanz zur&uuml;ck
	 * 
	 * @param degree
	 *            Grad der Drehung (<code>ROTATE_90</code>,
	 *            <code>ROTATE_180</code> oder <code>ROTATE_270</code>)
	 * @return gedrehtes Polyomino
	 */
	public Polyomino rotate(int degree) {
		if (degree != ROTATE_90 && degree != ROTATE_180 && degree != ROTATE_270)
			throw new IllegalArgumentException("Can't rotate this way!");

		if (degree == ROTATE_90)
			return rotateRight();
		// Muss so gemacht werden, da 90 Grad Rotation eventuell null sein kann,
		// 180 Grad jedoch wieder korrekt
		if (degree == ROTATE_180)
			return flip(FLIP_HORIZONTAL).flip(FLIP_VERTICAL);
		// ROTATE_270
		return flip(FLIP_HORIZONTAL).flip(FLIP_VERTICAL).rotateRight();
	}

	// /**
	// * Drehe das Polyomino um 90 Grad nach rechts
	// *
	// * @return gedrehtes Polyomino
	// */
	// public Polyomino rotateRight() {
	// Polyomino res = new Polyomino();
	// Position old = new Position(getMinLetter(), getMinNumber());
	// for (Position p : move(-getMinLetter(), -getMinNumber()).getPolyomino())
	// res.addPosition(new Position(-p.getNumber(), p.getLetter()));
	// res = res.move(-res.getMinLetter() + old.getLetter(), -res.getMinNumber()
	// + old.getNumber());
	// return res;
	// }

	/**
	 * Drehe das Polyomino um 90 Grad nach rechts
	 * 
	 * @return gedrehtes Polyomino
	 */
	public Polyomino rotateRight() {
		Polyomino res = new Polyomino();
		Polyomino norm = norm();
		for (Position p : norm.getPolyomino())
			res.addPosition(new Position(-p.getNumber(), p.getLetter()));
		res = res.move(norm.getMaxNumber() + minLetter, minNumber);
		res.width = height;
		res.height = width;
		res.minLetter = minLetter;
		res.maxLetter = minLetter + height - 1;
		res.minNumber = minNumber;
		res.maxNumber = minNumber + width - 1;
		return res;
	}

	/**
	 * Drehe das Polyomino um 90 Grad nach links
	 * 
	 * @return gedrehtes Polyomino
	 */
	public Polyomino rotateLeft() {
		Polyomino res = new Polyomino();
		Polyomino norm = norm();
		for (Position p : norm.getPolyomino())
			res.addPosition(new Position(p.getNumber(), -p.getLetter()));
		res = res.move(minLetter, minNumber + norm.getMaxLetter());
		res.width = height;
		res.height = width;
		res.minLetter = minLetter;
		res.maxLetter = minLetter + height - 1;
		res.minNumber = minNumber;
		res.maxNumber = minNumber + width - 1;
		return res;
	}

	/**
	 * Spiegel ein Polyomino und gib eine gespiegelte neue Instanz zur&uuml;ck
	 * 
	 * @param mode
	 *            Art der Spiegelung (<code>FLIP_HORIZONTAL</code> oder
	 *            <code>FLIP_VERTICAL</code>)
	 * @return gespiegeltes Polyomino
	 */
	public Polyomino flip(int mode) {
		if (mode != FLIP_HORIZONTAL && mode != FLIP_VERTICAL)
			throw new IllegalArgumentException("Can't flip this way!");

		// Ergebnis Polyomino
		Polyomino res = new Polyomino();
		if (mode == FLIP_HORIZONTAL)
			for (Position p : getPolyomino())
				res.addPosition(new Position(minLetter + maxLetter - p.getLetter(), p.getNumber()));
		else
			for (Position p : getPolyomino())
				res.addPosition(new Position(p.getLetter(), minNumber + maxNumber - p.getNumber()));

		// Berechne neue Dimensionen (werden einfach uebernommen)
		res.width = width;
		res.height = height;
		res.minLetter = minLetter;
		res.maxLetter = maxLetter;
		res.minNumber = minNumber;
		res.maxNumber = maxNumber;
		return res;
	}

	/**
	 * Verschiebe das Polyomino um <code>letter</code> nach rechts/links und
	 * <code>number</code> nach unten/oben. Liefere eine verschobene Instanz
	 * zur&uuml;ck
	 * 
	 * @param letter
	 *            Spaltenverschiebung
	 * @param number
	 *            Zeilenverschiebung
	 * @return Verschobenes Polyomino
	 */
	public Polyomino move(int letter, int number) {
		Polyomino res = new Polyomino();

		// Erzeuge alle verschobenen Positionen
		for (Position p : getPolyomino())
			res.addPosition(new Position(p.getLetter() + letter, p.getNumber() + number));

		// Berechne neue Dimensionen
		res.width = width;
		res.height = height;
		res.minLetter = minLetter + letter;
		res.maxLetter = maxLetter + letter;
		res.minNumber = minNumber + number;
		res.maxNumber = maxNumber + number;

		// Gib verschobenes Polyomino zurueck
		return res;
	}

	/**
	 * Verschiebe das Polyomino nach oben links (normierte Stellung)
	 * 
	 * @return normiertes Polynomino
	 */
	public Polyomino norm() {
		return move(-minLetter, -minNumber);
	}

	// ------------------------------------------------------------

	/**
	 * Klont das aktuelle Polyomino Objekt
	 * 
	 * @return Klon
	 */
	public Polyomino clone() {
		return new Polyomino(getPolyomino());
	}

	/**
	 * Pr&uuml;ft, ob zwei Polyomino Objekte gleich sind. Hierbei wird
	 * gepr&uuml;ft, ob der Spielstein der selbe ist, unabh&auml;ngig von
	 * Drehung, Spiegelung oder Verschiebung.
	 * 
	 * @param p
	 *            zu vergleichendes Polyomino
	 * 
	 * @return Gleichheit
	 */
	public boolean equalsPolyomino(Polyomino p) {
		// IO.debugln("Polyomino equals " + this + ", " + p +
		// " @ Polyomino.equalsPolyomino");

		if (p == null)
			return false;

		// Versuche alle Moeglichkeiten der Ungleichheit schnell auszuschliessen
		// Wenn die Anzahl der Steine ungleich, koennen die Polyominos nicht
		// gleich sein
		if (p.size() != size())
			return false;

		// Normiere beide Polyominos (nach oben links verschieben)
		Polyomino p1 = norm();
		Polyomino p2 = p.norm();

		// Probiere Brute-Force alle moeglichen Drehungen und Spiegelungen aus,
		// bis Gleichheit erreicht wird
		for (int i = 0; i < 4; i++) {
			if (p1.equalsPositions(p2))
				return true;
			if (p1.equalsPositions(p2.flip(FLIP_HORIZONTAL)))
				return true;
			p2 = p2.rotateRight();
		}

		return false;
	}

	/**
	 * Pr&uuml;ft, ob die Positionen der Polyominos &uuml;bereinstimmen
	 * 
	 * @param p
	 *            zu vergleichendes Polyomino
	 * @return Gleichheit
	 */
	private boolean equalsPositions(Polyomino p) {
		return getPolyomino().equals(p.getPolyomino());
	}

	// ------------------------------------------------------------

	/**
	 * Pr&uuml;ft, ob ein Collection einen bestimmen Polyomino enth&auml;lt
	 * 
	 * @param collection
	 *            Collection
	 * @param polyomino
	 *            Polyomino
	 * @return Polyomino enthalten
	 */
	public static boolean contains(Collection<Polyomino> collection, Polyomino polyomino) {
		for (Polyomino p : collection)
			if (p.equalsPolyomino(polyomino))
				return true;
		return false;
	}
}
