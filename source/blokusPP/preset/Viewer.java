package blokusPP.preset;

import java.util.ArrayList;
import java.util.HashSet;

import blokusPP.board.Polyomino;

public interface Viewer {
	/**
	 * Welche Farbe muss jetzt ziehen?
	 * 
	 * @return Farbe
	 */
	int turn();

	/**
	 * Gib alle verf&uuml;gbaren Polyominos der aktuellen Farbe zur&uuml;ck.
	 * Wird ben&ouml;tigt, um in der GUI die verf&uuml;gbaren Steine anzeigen zu
	 * k&ouml;nnen.
	 * 
	 * @return ArrayList von Steinen
	 */
	public ArrayList<Polyomino> getAvailablePolyominos();

	/**
	 * Gib alle g&uuml;ltigen Startpositionen f&uuml;r den n&auml;chsten Zug
	 * zur&uuml;ck
	 * 
	 * @return ArrayList von Startpositionen
	 */
	public ArrayList<Position> getValidStartPositions();

	/**
	 * Liefert alle g&uuml;ltigen Z&uuml;ge zur&uuml;ck, die der Spieler in
	 * diesem Zug machen kann.
	 * 
	 * @return HashSet von g&uuml;ltigen Z&uuml;gen
	 */
	public HashSet<Move> getValidMoves();

	/**
	 * Pr&uuml;ft ob ein Zug g&uuml;ltig ist
	 * 
	 * @return HashSet von g&uuml;ltigen Z&uuml;gen
	 */
	public boolean isValidMove(Move move);

	/**
	 * Welche Farbe hat der Stein an Position (<code>letter</code>,
	 * <code>number</code>)
	 * 
	 * @param letter
	 *            Buchstabe (Spalte)
	 * @param number
	 *            Zahl (Zeile)
	 * @return Farbe des Steins
	 */
	int getColor(int letter, int number);

	/**
	 * Gib den aktuellen Status des Spielbretts zur&uuml;ck
	 * 
	 * @return Status
	 */
	Status getStatus();
}
