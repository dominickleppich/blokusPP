package blokusPP.board;

import java.util.ArrayList;
import java.util.HashSet;

import blokusPP.preset.Move;
import blokusPP.preset.Position;
import blokusPP.preset.Status;
import blokusPP.preset.Viewer;

/**
 * <h1>Viewer f&uuml;r das Spielbrett</h1>
 * 
 * <p>
 * Liefert alle notwendigen Informationen, um das Spielfeld anzeigen zu
 * k&ouml;nnen.
 * </p>
 * 
 * @author Dominick Leppich
 *
 */
public class BoardViewer implements Viewer {
	// Referenz auf das Spielbrett, das angezeigt werden soll
	private Board board;

	// ------------------------------------------------------------

	/**
	 * Erzeuge einen BoardViewer
	 * 
	 * @param board
	 *            Board, welches angezeigt werden soll
	 */
	public BoardViewer(Board board) {
		this.board = board;
	}

	// ------------------------------------------------------------

	/**
	 * Welche Farbe muss jetzt ziehen?
	 * 
	 * @return Farbe
	 */
	@Override
	public int turn() {
		return board.getActiveColor();
	}

	/**
	 * Gib alle verf&uuml;gbaren Polyominos der aktuellen Farbe zur&uuml;ck.
	 * Wird ben&ouml;tigt, um in der GUI die verf&uuml;gbaren Steine anzeigen zu
	 * k&ouml;nnen.
	 * 
	 * @return ArrayList von Steinen
	 */
	@Override
	public ArrayList<Polyomino> getAvailablePolyominos() {
		return board.getAvailablePolyominos();
	}
	
	/**
	 * Gib alle g&uuml;ltigen Startpositionen f&uuml;r den n&auml;chsten Zug
	 * zur&uuml;ck
	 * 
	 * @return ArrayList von Startpositionen
	 */
	@Override
	public ArrayList<Position> getValidStartPositions() {
		return board.getValidStartPositions();
	}

	/**
	 * Liefert alle g&uuml;ltigen Z&uuml;ge zur&uuml;ck, die der Spieler in
	 * diesem Zug machen kann.
	 * 
	 * @return HashSet von g&uuml;ltigen Z&uuml;gen
	 */
	@Override
	public HashSet<Move> getValidMoves() {
		return board.getValidMoves();
	}

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
	@Override
	public int getColor(int letter, int number) {
		return board.getColor(letter, number);
	}

	/**
	 * Gib den aktuellen Status des Spielbretts zur&uuml;ck
	 * 
	 * @return Status
	 */
	@Override
	public Status getStatus() {
		return board.getStatus();
	}

	/**
	 * Pr&uuml;ft ob ein Zug g&uuml;ltig ist
	 * 
	 * @return Zug g&uuml;ltig
	 */
	@Override
	public boolean isValidMove(Move move) {
		return board.isValidMove(move, false);
	}
}
