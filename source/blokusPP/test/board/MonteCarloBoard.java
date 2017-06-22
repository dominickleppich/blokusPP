package blokusPP.test.board;

import java.util.Random;

import blokusPP.board.Board;
import blokusPP.preset.Move;

/**
 * <h1>MonteCarlo Board</h1>
 * 
 * <p>
 * Dieses Board kann f&uuml;r einen &uuml;bergebenen Zug ein zuf&auml;lliges
 * weiteres Spiel simulieren und das Ergebnis feststellen
 * </p>
 * 
 * @author Dominick Leppich
 *
 */
public class MonteCarloBoard extends Board {
	/**
	 * Default Konstruktor
	 */
	public MonteCarloBoard() {
		super();
	}

	/**
	 * Copy Konstruktor
	 * 
	 * @param source
	 *            Board
	 */
	public MonteCarloBoard(Board source) {
		super(source);
	}

	// ------------------------------------------------------------

	/**
	 * Simuliert ein Spiel f&uuml;r einen &uuml;bergebenen Zug. Liefert die
	 * Punktest&auml;nde der Farben zur&uuml;ck
	 * 
	 * @param move
	 *            Spielzug
	 * @return Punktestand
	 */
	public int[] simulateGame(Move move) {
		if (!makeMove(move, false))
			return null;

		Random rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());

		// Lasse Spiel komplett durchlaufen
		while (!status.isGameOver())
			fastMakeMove(validMoves.iterator().next());

		int score[] = new int[GAME_COLORS];
		for (int color = 0; color < GAME_COLORS; color++)
			score[color] = getScore(color);

		return score;
	}

	/**
	 * Simuliert ein Spiel f&uuml;r einen &uuml;bergebenen Zug. Liefert die
	 * Punktest&auml;nde der Farben zur&uuml;ck
	 * 
	 * @param move
	 *            Spielzug
	 * @param count
	 *            Anzahl Z&uuml;ge zur Simulation
	 * @return Punktestand
	 */
	public int[] simulateGame(Move move, int count) {
		if (!makeMove(move, false))
			return null;

		Random rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		int c = 0;
		
		// Lasse Spiel komplett durchlaufen
		while (c++ < count && !status.isGameOver())
			fastMakeMove(validMoves.iterator().next());

		int score[] = new int[GAME_COLORS];
		for (int color = 0; color < GAME_COLORS; color++)
			score[color] = getScore(color);

		return score;
	}

	// ------------------------------------------------------------

	/**
	 * F&uuml;hrt einen Zug ohne &Uuml;berpr&uuml;fungen aus. Funktioniert, da
	 * nur g&uuml;ltigen Z&uuml;ge &uuml;bergeben werden
	 * 
	 * @param move
	 *            Spielzug
	 */
	private void fastMakeMove(Move move) {
		// Pausiere im Falle von null
		if (move == null) {
			status.setScore(activeColor, PAUSE);
			// Wechsle zur naechsten Farbe
			nextColor();
			return;
		}

		// Wenn alles okay war, setze den Zug auf dem Board,
		setMoveOnBoard(move);

		// Entferne den gesetzten Polyomino
		removePolyomino(move);

		// Update Listen der Startpositionen und blockierten Positionen

		updateBoard(move);

		// Update Punkte
		score[activeColor] += move.getPolyomino().size();

		if (availablePolyominos.get(activeColor).isEmpty()) {
			// Extrapunkte
			score[activeColor] += ENDGAME_BONUS_POINTS;
			status.setScore(activeColor, FINISH);
		} else
			// setze den Status fuer den Spieler auf OK
			status.setScore(activeColor, OK);

		// und wechsle zum naechsten Spieler
		nextColor();

		// Berechne gueltige Zuege des naechsten Spielers, pausiere falls keine
		// vorhanden
		calculateNextValidMoves(false);
	}
}
