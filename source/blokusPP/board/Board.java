package blokusPP.board;

import java.util.ArrayList;
import java.util.HashSet;

import blokusPP.game.Match;
import blokusPP.io.HTMLOutput;
import blokusPP.preset.Move;
import blokusPP.preset.Position;
import blokusPP.preset.Setting;
import blokusPP.preset.Status;
import blokusPP.preset.Viewable;
import blokusPP.preset.Viewer;
import eu.nepster.toolkit.io.IO;

/**
 * <h1>Spielbrett-Klasse f&uuml;r Blokus</h1>
 * 
 * <h2>Konstanten</h2>
 * <p>
 * <ul>
 * <li><code>SIZE</code>: Gr&ouml;&szlig;e des Spielfeldes</li>
 * </ul>
 * </p>
 * 
 * @author Christoph Rauterberg, Dominick Leppich
 *
 */
public class Board implements Setting, Viewable, HTMLOutput {
	// Klassenkonstanten
	public static final int SIZE = 20;

	public static final int FULL_BLOCK_SCORE = 1 * 1 + 1 * 2 + 2 * 3 + 5 * 4 + 12 * 5;

	protected static final int ENDGAME_BONUS_POINTS = 15;

	// ------------------------------------------------------------

	// Status des Spielbretts
	protected Status status;

	// Spielfeld [letter][number]
	protected int board[][];

	// Farbe des Spielers, von dem der naechste Zug erwartet wird
	protected int activeColor;

	// Punktestand pro Spieler
	protected int score[];

	// Verfuegbare Steine jeder Farbe
	protected ArrayList<ArrayList<Polyomino>> availablePolyominos;

	// Gueltige StartPositionen
	protected boolean[][][] startPositions;

	// Blockierte Positionen (Durch eigene Steine blockiert oder durch gesetzten
	// Stein)
	protected boolean[][][] blockedPositions;

	// ------------------------------------------------------------

	/**
	 * Erzeuge ein neues Spielbrett der Gr&ouml;&szlig;e <code>SIZE</code> x
	 * <code>SIZE</code>
	 */
	public Board() {
		board = new int[SIZE][SIZE];
		score = new int[GAME_COLORS];
		availablePolyominos = new ArrayList<>();
		startPositions = new boolean[GAME_COLORS][SIZE][SIZE];
		blockedPositions = new boolean[GAME_COLORS][SIZE][SIZE];
		reset();
	}

	/**
	 * Copy Konstruktor
	 * 
	 * @param source
	 *            Board
	 */
	public Board(Board source) {
		this();

		// Uebernehme alles!
		status = new Status(source.status);
		for (int letter = 0; letter < SIZE; letter++)
			for (int number = 0; number < SIZE; number++)
				board[letter][number] = source.board[letter][number];
		activeColor = source.activeColor;
		for (int color = 0; color < GAME_COLORS; color++) {
			score[color] = source.score[color];
			// Klone verfuegbare Steine
			ArrayList<Polyomino> list = new ArrayList<Polyomino>();
			for (Polyomino poly : source.availablePolyominos.get(color))
				list.add(poly);
			availablePolyominos.add(list);
			// Kopiere gueltige Startpositionen und blockierte Positionen
			for (int letter = 0; letter < SIZE; letter++) {
				for (int number = 0; number < SIZE; number++) {
					startPositions[color][letter][number] = source.startPositions[color][letter][number];
					blockedPositions[color][letter][number] = source.blockedPositions[color][letter][number];
				}
			}
		}
	}

	/**
	 * Liefert eine echte Kopie des Spielbretts in seiner jetzigen Situation
	 * zur&uuml;ck
	 * 
	 * @return Klon
	 */
	public Board clone() {
		return new Board(this);
	}

	/**
	 * Setze das Spielfeld in Ausgangssituation zur&uuml;ck
	 */
	public void reset() {
		status = new Status(OK);
		activeColor = BLUE;

		for (int i = 0; i < score.length; i++)
			score[i] = 0;

		// Jede Farbe hat alle Steine zur Verfuegung (4 ArrayListen in der
		// gesamten ArrayListe; Index entspricht Farbe)
		availablePolyominos.clear();
		availablePolyominos.add(Polyomino.getAllPolyominos());
		availablePolyominos.add(Polyomino.getAllPolyominos());
		availablePolyominos.add(Polyomino.getAllPolyominos());
		availablePolyominos.add(Polyomino.getAllPolyominos());

		// Setze Spielfeld leer
		for (int letter = 0; letter < SIZE; letter++) {
			for (int number = 0; number < SIZE; number++) {
				board[letter][number] = NONE;
				for (int color = 0; color < GAME_COLORS; color++) {
					startPositions[color][letter][number] = false;
					blockedPositions[color][letter][number] = false;
				}
			}
		}

		// Ecken sind fuer alle Spieler gueltige Startpositionen
		for (int color = 0; color < GAME_COLORS; color++) {
			startPositions[color][0][0] = true;
			startPositions[color][0][SIZE - 1] = true;
			startPositions[color][SIZE - 1][0] = true;
			startPositions[color][SIZE - 1][SIZE - 1] = true;
		}

		// Berechne die gueltigen Zuege des ersten Spielers (alle folgenden
		// werden nach makeMove berechnet)
		calculateValidMoves(BLUE);
	}

	// ------------------------------------------------------------

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
	public int getColor(int letter, int number) {
		if (letter < 0 || number < 0 || letter >= SIZE || number >= SIZE)
			throw new IndexOutOfBoundsException("Position (" + letter + ", " + number + ") out of bounds!");

		return board[letter][number];
	}

	/**
	 * Gib die Farbe des aktuell aktiven Spielers zur&uuml;ck
	 * 
	 * @return Farbe
	 */
	public int getActiveColor() {
		return activeColor;
	}

	/**
	 * Liefert den aktuellen Spielstatus zur&uuml;ck
	 * 
	 * @return Status
	 */
	public Status getStatus() {
		return new Status(status);
	}

	/**
	 * Gibt den Punktestand f&uuml;r eine beliebige Farbe zur&uuml;ck
	 * 
	 * @param color
	 *            Farbe
	 * @return Punktestand
	 */
	public int getScore(int color) {
		if (color < 0 || color >= GAME_COLORS)
			throw new IllegalArgumentException("There is no color " + color + "!");

		return score[color];
	}

	/**
	 * Liefert einen Viewer f&uuml;r das Spielfeld zur&uuml;ck
	 */
	public Viewer viewer() {
		return new BoardViewer(this);
	}

	/**
	 * Gib eine HTML Repr&auml;sentation des Objektes zur&uuml;ck
	 * 
	 * @return HTML String
	 */
	@Override
	public String html() {
		String res = "";
		res += "\t\t\t<div class=\"board\">\n";
		res += "\t\t\t\t<img class=\"board\" style=\"z-index: 0;\" src=\"../res/gfx/background/board.jpg\" />\n";
		for (int letter = 0; letter < SIZE; letter++) {
			for (int number = 0; number < SIZE; number++) {
				String label = "";
				switch (board[letter][number]) {
				case NONE:
					label = "none";
					break;
				case BLUE:
					label = "blue";
					break;
				case YELLOW:
					label = "yellow";
					break;
				case RED:
					label = "red";
					break;
				case GREEN:
					label = "green";
					break;
				}
				res += "\t\t\t\t<div class=\"field\" style=\"top: " + (number * Match.FIELD_SIZE) + "px; left: "
						+ (letter * Match.FIELD_SIZE) + "px; width: " + Match.FIELD_SIZE + "px; height: "
						+ Match.FIELD_SIZE + "px; z-index: 1;\" >\n";
				res += "\t\t\t\t\t<img class=\"field\" src=\"../res/gfx/field/" + label + ".png\" width=\""
						+ Match.FIELD_SIZE + "\" height=\"" + Match.FIELD_SIZE + "\" />\n";
				res += "\t\t\t\t</div>\n";
			}
		}
		res += "\t\t\t</div>\n";
		return res;
	}

	// ------------------------------------------------------------

	/**
	 * F&uuml;hre einen Zug auf dem Spielfeld aus. Der Zug wird in der aktuell
	 * aktiven Farbe ausgef&uuml;hrt. Wird <code>null</code> &uuml;bergeben,
	 * pausiert diese Farbe bis zum Ende des Spiels. Im Falle eines Fehlers,
	 * wird der Status des Spielbretts auf <code>ILLEGAL</code> gesetzt und
	 * <code>false</code> zur&uuml;ckgegeben.
	 * 
	 * @param move
	 *            Zug
	 * @return Zug erfolgreich ausgef&uuml;hrt
	 */
	public boolean makeMove(Move move) {
		return makeMove(move, true);
	}

	/**
	 * F&uuml;hre einen Zug auf dem Spielfeld aus. Der Zug wird in der aktuell
	 * aktiven Farbe ausgef&uuml;hrt. Wird <code>null</code> &uuml;bergeben,
	 * pausiert diese Farbe bis zum Ende des Spiels. Im Falle eines Fehlers,
	 * wird der Status des Spielbretts auf <code>ILLEGAL</code> gesetzt und
	 * <code>false</code> zur&uuml;ckgegeben.
	 * 
	 * @param move
	 *            Zug
	 * @param debug
	 *            Zeige Debugausgabe
	 * @return Zug erfolgreich ausgef&uuml;hrt
	 */
	public boolean makeMove(Move move, boolean debug) {
		if (debug)
			IO.debugln("Got move " + move + " from color " + colorString[activeColor] + " @ Board.makeMove");
		// Wenn Spiel zu Ende, werden keine Zuege mehr entgegengenommen
		if (status.isGameOver()) {
			if (debug)
				IO.debugln("Game already over @ Board.makeMove");
			return false;
		}
		// Pausiere im Falle von null
		if (move == null) {
			status.setScore(activeColor, PAUSE);
			if (debug)
				IO.debugln("Color " + colorString[activeColor] + " waiting now @ Board.makeMove");
			// Wechsle zur naechsten Farbe
			nextColor();
			return true;
		}

		// Ist der Zug nicht gueltig, setze Status fuer diese Farbe auf ILLEGAL
		// und gib false zurueck
		if (!isValidMove(activeColor, move, true)) {
			// TODO Testen.. Keine grosse Veraenderung
			// if (!validMoves.contains(move)) {
			status.setScore(activeColor, ILLEGAL);
			if (debug)
				IO.debugln("Illegal move made " + move + " @ Board.makeMove");
			return false;
		}

		// Wenn alles okay war, setze den Zug auf dem Board,
		setMoveOnBoard(move);
		if (debug)
			IO.debugln(
					"Polyomino " + move + " set on board for color " + colorString[activeColor] + " @ Board.makeMove");

		// Entferne den gesetzten Polyomino
		removePolyomino(move);
		if (debug)
			IO.debugln("Polyomino " + move + " removed from color " + colorString[activeColor] + " @ Board.makeMove");

		// Update Listen der Startpositionen und blockierten Positionen
		updateBoard(activeColor, move);
		if (debug)
			IO.debugln("Board updated @ Board.makeMove");

		// Update Punkte
		score[activeColor] += move.getPolyomino().size();

		if (availablePolyominos.get(activeColor).isEmpty()) {
			// Extrapunkte
			score[activeColor] += ENDGAME_BONUS_POINTS;
			status.setScore(activeColor, FINISH);
		} else
			// setze den Status fuer den Spieler auf OK
			status.setScore(activeColor, OK);

		// Berechne fuer alle Farben die gueltigen Zuege, um zu sehen wer
		// pausiert und wer nicht
		for (int i = 0; i < GAME_COLORS; i++)
			if (status.getScore(i) == OK && calculateValidMoves(i).isEmpty())
				status.setScore(i, PAUSE);

		// und wechsle zum naechsten Spieler
		nextColor();

		if (debug)
			IO.debugln("Move " + move + " okay @ Board.makeMove");
		return true;
	}

	// ------------------------------------------------------------

	/**
	 * Gib alle g&uuml;ltigen Z&uuml;ge der aktuellen Farbe zur&uuml;ck
	 * 
	 * @return Menge der g&uuml;ltigen Z&uuml;ge
	 */
	public HashSet<Move> getValidMoves() {
		return getValidMoves(activeColor);
	}

	/**
	 * Gib alle g&uuml;ltigen Z&uuml;ge einer bestimmten Farbe zur&uuml;ck
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @return Menge der g&uuml;ltigen Z&uuml;ge
	 */
	public HashSet<Move> getValidMoves(int color) {
		return calculateValidMoves(color);
	}

	/**
	 * Berechne alle g&uuml;ltigen Z&uuml;ge
	 * 
	 * @param color
	 *            Spielerfarbe
	 */
	public HashSet<Move> calculateValidMoves(int color) {
		HashSet<Move> validMoves = new HashSet<Move>();

		// Hole alle moeglichen Startpositionen
		ArrayList<Position> startPositions = getValidStartPositions(color);

		// Fuer jede Startposition werden alle Moeglichkeiten die verfuebaren
		// Polyominos anzufuegen durchprobiert. Hierfuer werden sie erst einmal
		// verschoben falls noetig, sodass ein Feld des Polyominos an der
		// Startposition liegt. Danach werden alle moeglichen Drehungen und
		// Spiegelungen mit Verschiebungskorrektur durchgefuehrt und geprueft,
		// ob ein gueltiger Zug enstanden ist
		for (Position pos : startPositions)
			addValidMoves(color, validMoves, pos);
		return validMoves;
	}

	/**
	 * Erzeuge alle g&uuml;ltigen Z&uuml;ge, die an einer bestimmten Position
	 * beginnen
	 *
	 * @param color
	 *            Spielerfarbe
	 * @param validMoves
	 *            Liste in der die g&uuml;ltigen Z&uuml;ge gespeichert werden
	 *            sollen
	 * @param position
	 *            Startposition
	 */
	private void addValidMoves(int color, HashSet<Move> validMoves, Position position) {
		for (Polyomino poly : getAvailablePolyominos(color))
			addValidMoves(color, validMoves, position, poly);
	}

	/**
	 * Erzeuge alle g&uuml;ltigen Z&uuml;ge, die an einer bestimmten Position
	 * beginnen und mit einem bestimmten Polyomino generiert werden k&ouml;nnen
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @param validMoves
	 *            Liste in der die g&uuml;ltigen Z&uuml;ge gespeichert werden
	 *            sollen
	 * @param position
	 *            Startposition
	 * @param polyomino
	 *            Polyomino
	 */
	private void addValidMoves(int color, HashSet<Move> validMoves, Position position, Polyomino polyomino) {
		Polyomino rotated = polyomino.move(position.getLetter(), position.getNumber());
		addValidMovesMoveAndFlip(color, validMoves, position, rotated);
		rotated = rotated.rotate(Polyomino.ROTATE_90);
		addValidMovesMoveAndFlip(color, validMoves, position, rotated);
		rotated = rotated.rotate(Polyomino.ROTATE_90);
		addValidMovesMoveAndFlip(color, validMoves, position, rotated);
		rotated = rotated.rotate(Polyomino.ROTATE_90);
		addValidMovesMoveAndFlip(color, validMoves, position, rotated);
	}

	/**
	 * Generiere alle m&ouml;glichen verschobenen und gespiegelten
	 * Polyomino-Z&uuml;ge
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @param validMoves
	 *            Liste in der die g&uuml;ltigen Z&uuml;ge gespeichert werden
	 *            sollen
	 * @param position
	 *            Startposition
	 * @param polyomino
	 *            Ausgangspolyomino
	 */
	private void addValidMovesMoveAndFlip(int color, HashSet<Move> validMoves, Position position, Polyomino polyomino) {
		// Bewege jeden Polyomino so, dass alle moeglichen Feldes des Polyominos
		// einmal auf der Startposition liegen. Auf diese Weise werden alle
		// Moeglichkeiten abgedeckt
		for (int letterMove = 0; letterMove < polyomino.getWidth(); letterMove++) {
			for (int numberMove = 0; numberMove < polyomino.getHeight(); numberMove++) {
				Polyomino tmp = polyomino.move(-letterMove, -numberMove);
				addValidMovesCheck(color, validMoves, tmp, position);
				tmp = tmp.flip(Polyomino.FLIP_HORIZONTAL);
				addValidMovesCheck(color, validMoves, tmp, position);
			}
		}
	}

	/**
	 * Pr&uuml;fe ob ein generierter Zug g&uuml;ltig ist und f&uuml;ge
	 * g&uuml;ltige hinzu
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @param validMoves
	 *            Liste in der die g&uuml;ltigen Z&uuml;ge gespeichert werden
	 *            sollen
	 * @param polyomino
	 *            Spielzug
	 * @param startPosition
	 *            Startposition
	 */
	private void addValidMovesCheck(int color, HashSet<Move> validMoves, Polyomino polyomino, Position startPosition) {
		// Pruefe kein Polyomino zweimal
		// if (validMoves.contains(move))
		// return;
		// System.out.println((double) (System.nanoTime() - start) / 1000000);

		// Teste ob die Startposition enthalten ist
		boolean hasStartPos = false;
		for (Position movePos : polyomino.getPolyomino()) {
			if (movePos.equals(startPosition)) {
				hasStartPos = true;
				break;
			}
		}
		if (!hasStartPos)
			return;

		// Liegt der Stein auf dem Spielfeld
		if (!isOnBoard(polyomino))
			return;

		// Sind alle Felder frei
		if (!isNotBlocked(color, polyomino))
			return;

		validMoves.add(new Move(polyomino.getPolyomino()));
	}

	// ------------------------------------------------------------

	// ------------------------------
	// Hilfsfunktionen
	// ------------------------------

	/**
	 * Gib alle verf&uuml;gbaren Polyominos der aktuellen Farbe zur&uuml;ck
	 * 
	 * @return ArrayList von Polyominos
	 */
	public ArrayList<Polyomino> getAvailablePolyominos() {
		return getAvailablePolyominos(activeColor);
	}

	/**
	 * Gib alle verf&uuml;gbaren Polyominos einer Farbe zur&uuml;ck.
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @return ArrayList von Polyominos
	 */
	public ArrayList<Polyomino> getAvailablePolyominos(int color) {
		return availablePolyominos.get(color);
	}

	/**
	 * Gib g&uuml;ltige Startpositionen der aktuellen Farbe zur&uuml;ck
	 * 
	 * @return ArrayList von Startpositionen
	 */
	public ArrayList<Position> getValidStartPositions() {
		return getValidStartPositions(activeColor);
	}

	/**
	 * Gib g&uuml;ltige Startpositionen zur&uuml;ck
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @return ArrayList von Startpositionen
	 */
	public ArrayList<Position> getValidStartPositions(int color) {
		ArrayList<Position> res = new ArrayList<Position>();
		for (int letter = 0; letter < SIZE; letter++)
			for (int number = 0; number < SIZE; number++)
				if (startPositions[color][letter][number])
					res.add(new Position(letter, number));
		return res;
	}
	
	/**
	 * Gib blockierte Positionen der aktuellen Farbe zur&uuml;ck
	 * 
	 * @return ArrayList von Startpositionen
	 */
	public ArrayList<Position> getBlockedPositions() {
		return getBlockedPositions(activeColor);
	}

	/**
	 * Gib blockierte Positionen einer Farbe zur&uuml;ck
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @return ArrayList von Startpositionen
	 */
	public ArrayList<Position> getBlockedPositions(int color) {
		ArrayList<Position> res = new ArrayList<Position>();
		for (int letter = 0; letter < SIZE; letter++)
			for (int number = 0; number < SIZE; number++)
				if (blockedPositions[color][letter][number])
					res.add(new Position(letter, number));
		return res;
	}

	/**
	 * Pr&uuml;ft, ob der Zug ein g&uuml;ltiger Zug ist. Hierbei werden alle
	 * Regeln des Spiels getestet.
	 * 
	 * @param move
	 *            Zug
	 * @param debug
	 *            Zeige Fehler an
	 * @return Zug ist g&uuml;ltig
	 */
	public boolean isValidMove(Move move, boolean debug) {
		return isValidMove(activeColor, move, debug);
	}

	/**
	 * Pr&uuml;ft, ob der Zug ein g&uuml;ltiger Zug ist. Hierbei werden alle
	 * Regeln des Spiels getestet.
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @param move
	 *            Zug
	 * @param debug
	 *            Zeige Fehler an
	 * @return Zug ist g&uuml;ltig
	 */
	public boolean isValidMove(int color, Move move, boolean debug) {
		// Liegt der Stein auf dem Spielfeld
		if (!isOnBoard(move)) {
			if (debug)
				IO.debugln("Move " + move + " not on board @ Board.isValidMove");
			return false;
		}

		// Sind alle Felder frei
		if (!isNotBlocked(color, move)) {
			if (debug)
				IO.debugln("Move " + move + " not on free space @ Board.isValidMove");
			return false;
		}

		// Ist es ein gueltiger Polyomino, der noch verfuegbar ist
		if (!isValidPolyomino(color, move)) {
			if (debug)
				IO.debugln("Move " + move + " is not an available polyomino of color " + colorString[color]
						+ " @ Board.isValidMove");
			return false;
		}

		// Hat der Zug eine gueltige Startposition und liegt auf nicht
		// blockierten Feldern?
		if (!hasValidStartPosition(color, move) || !isNotBlocked(color, move)) {
			if (debug)
				IO.debugln("Move " + move + " not on valid start position or not on free space @ Board.isValidMove");
			return false;
		}

		return true;
	}

	/**
	 * Pr&uuml;ft, ob der &uuml;bergebene Zug komplett auf dem Spielfeld liegt.
	 * Das hei&szlig;t die Grenzen nach links/rechts/oben/unten nicht
	 * &uuml;berschreitet.
	 * 
	 * @param move
	 *            Spielzug
	 * @return Liegt auf dem Spielbrett
	 */
	public static boolean isOnBoard(Move move) {
		for (Position p : move.getPolyomino())
			if (!isOnBoard(p))
				return false;
		return true;
	}

	/**
	 * Pr&uuml;ft, ob ein Feld auf dem Spielbrett liegt
	 * 
	 * @param pos
	 *            Feld
	 * @return Liegt auf dem Spielbrett
	 */
	public static boolean isOnBoard(Position pos) {
		return pos.getLetter() >= 0 && pos.getLetter() < SIZE && pos.getNumber() >= 0 && pos.getNumber() < SIZE;
	}

	/**
	 * Pr&uuml;ft, ob der Zug eine g&uuml;ltige Startposition enth&auml;lt
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @param move
	 *            Spielzug
	 * @return g&uuml;ltige Startposition enthalten
	 */
	public boolean hasValidStartPosition(int color, Move move) {
		for (Position movePos : move.getPolyomino())
			if (startPositions[color][movePos.getLetter()][movePos.getNumber()])
				return true;
		return false;
	}

	/**
	 * Pr&uuml;ft, ob f&uuml;r den &uuml;bergebenen Zug alle Felder auf dem
	 * Spielbrett frei sind
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @param move
	 *            Spielzug
	 * @return Felder sind frei
	 */
	public boolean isNotBlocked(int color, Move move) {
		for (Position p : move.getPolyomino())
			if (!isFreeSpace(color, p))
				return false;
		return true;
	}

	/**
	 * Pr&uuml;ft, ob ein Feld auf dem Spielfeld f&uuml;r die aktuelle Farbe
	 * frei ist
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @param pos
	 *            Feld
	 * @return Feld ist frei
	 */
	public boolean isFreeSpace(int color, Position pos) {
		return blockedPositions[color][pos.getLetter()][pos.getNumber()] == false
				&& board[pos.getLetter()][pos.getNumber()] == NONE;
	}

	/**
	 * Pr&uuml;ft, ob der Polyomino des &uuml;bergebenen Zuges ein noch
	 * verf&uuml;gbarer Polyomino ist
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @param move
	 *            Spielzug
	 * @return Polyomino vorhanden
	 */
	public boolean isValidPolyomino(int color, Move move) {
		Polyomino p = new Polyomino(move.getPolyomino());
		for (Polyomino tmp : availablePolyominos.get(color))
			if (tmp.equalsPolyomino(p))
				return true;
		return false;
	}

	/**
	 * Update das Board nach dem Zug. Der Stein muss gesetzt werden. Berechne
	 * neue Startpositionen. Dieser ist von nun an eine blockierte Position
	 * aller Farben. Die horizontal und vertikal benachbarten Felder sind
	 * blockiert f&uuml;r die aktuelle Farbe. Alle blockierten Felder entfernen
	 * ggf. vorhande Startpositionen.
	 * 
	 * @param color
	 *            Spielerfarbe
	 * @param move
	 *            Spielzug
	 */
	protected void updateBoard(int color, Move move) {
		// Alle diagonalen Nachbarn aller Positionen des Zuges sind potentielle
		// neue Startfelder, falls nicht, werden sie beim blockieren wieder
		// entfernt
		for (Position movePos : move.getPolyomino())
			for (Position neighbor : getDiagonalNeighbors(movePos))
				if (isFreeSpace(color, neighbor))
					startPositions[color][neighbor.getLetter()][neighbor.getNumber()] = true;

		// Der gesetzte Stein ist blockiert fuer alle Farben
		for (int cc = 0; cc < GAME_COLORS; cc++) {
			for (Position movePos : move.getPolyomino()) {
				blockedPositions[cc][movePos.getLetter()][movePos.getNumber()] = true;
				// Falls es eine Startposition war, wird diese entfernt
				startPositions[cc][movePos.getLetter()][movePos.getNumber()] = false;
			}
		}

		// Kanten (lineare Nachbarn) sind blockierte Positionen der aktuellen
		// Farbe
		for (Position movePos : move.getPolyomino()) {
			for (Position neighbor : getLinearNeighbors(movePos)) {
				blockedPositions[color][neighbor.getLetter()][neighbor.getNumber()] = true;
				startPositions[color][neighbor.getLetter()][neighbor.getNumber()] = false;
			}
		}
	}

	/**
	 * Gib die gerade benachbarten Felder einer Position zur&uuml;ck, die auf
	 * dem Feld liegen
	 * 
	 * @param p
	 *            Ausgangsposition
	 * @return ArrayList gerader benachbarter Felder
	 */
	private ArrayList<Position> getLinearNeighbors(Position p) {
		ArrayList<Position> res = new ArrayList<Position>();
		Position p1 = new Position(p.getLetter() - 1, p.getNumber());
		Position p2 = new Position(p.getLetter() + 1, p.getNumber());
		Position p3 = new Position(p.getLetter(), p.getNumber() - 1);
		Position p4 = new Position(p.getLetter(), p.getNumber() + 1);
		if (isOnBoard(p1))
			res.add(p1);
		if (isOnBoard(p2))
			res.add(p2);
		if (isOnBoard(p3))
			res.add(p3);
		if (isOnBoard(p4))
			res.add(p4);
		return res;
	}

	/**
	 * Gib die diagonal benachbarten Felder einer Position zur&uuml;ck, die auf
	 * dem Feld liegen
	 * 
	 * @param p
	 *            Ausgangsposition
	 * @return ArrayList diagonal benachbarter Felder
	 */
	private ArrayList<Position> getDiagonalNeighbors(Position p) {
		ArrayList<Position> res = new ArrayList<Position>();
		Position p1 = new Position(p.getLetter() - 1, p.getNumber() - 1);
		Position p2 = new Position(p.getLetter() - 1, p.getNumber() + 1);
		Position p3 = new Position(p.getLetter() + 1, p.getNumber() - 1);
		Position p4 = new Position(p.getLetter() + 1, p.getNumber() + 1);
		if (isOnBoard(p1))
			res.add(p1);
		if (isOnBoard(p2))
			res.add(p2);
		if (isOnBoard(p3))
			res.add(p3);
		if (isOnBoard(p4))
			res.add(p4);
		return res;
	}

	// ------------------------------------------------------------

	/**
	 * Setze einen Spielzug in der aktuellen Farbe auf dem Spielbrett
	 * 
	 * @param move
	 *            Spielzug
	 */
	protected void setMoveOnBoard(Move move) {
		for (Position p : move.getPolyomino())
			board[p.getLetter()][p.getNumber()] = activeColor;
	}

	/**
	 * Entferne gesetzten Polyomino aus Liste der verf&uuml;gbaren
	 * 
	 * @param move
	 *            Spielzug
	 */
	protected void removePolyomino(Move move) {
		Polyomino p = new Polyomino(move.getPolyomino());
		ArrayList<Polyomino> available = availablePolyominos.get(activeColor);
		for (int i = 0; i < available.size(); i++)
			if (available.get(i).equalsPolyomino(p))
				available.remove(i);
	}

	/**
	 * Wechsle zur n&auml;chsten Spielerfarbe
	 */
	protected void nextColor() {
		int c = activeColor;
		do {
			activeColor = (activeColor + 1) % GAME_COLORS;
		} while (status.getScore(activeColor) == PAUSE && activeColor != c);
	}
}
