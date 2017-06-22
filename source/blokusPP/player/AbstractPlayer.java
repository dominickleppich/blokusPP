package blokusPP.player;

import java.rmi.RemoteException;

import blokusPP.board.Board;
import blokusPP.preset.Move;
import blokusPP.preset.Player;
import blokusPP.preset.Setting;
import blokusPP.preset.Status;
import eu.nepster.toolkit.io.IO;

/**
 * <h1>Abstrakte Spielerklasse</h1>
 * 
 * <p>
 * Es werden alle Grundfunktionalit&auml;ten des Spielers implementiert. Die
 * einzelnen Spieler m&uuml;ssen lediglich die <code>deliver()</code> Methode
 * implementieren.
 * </p>
 * 
 * @author Dominick Leppich
 *
 */
public abstract class AbstractPlayer implements Setting, Player {
	public static final int NONE = -1;
	public static final int REQUEST = 0;
	public static final int CONFIRM = 1;
	public static final int UPDATE = 2;

	public static final String[] callString = { "REQUEST", "CONFIRM", "UPDATE" };

	// ------------------------------------------------------------

	protected Board board;
	protected int[] colors;

	private int expectedCall;

	private Move lastMove;

	// ------------------------------------------------------------

	/**
	 * Erzeuge Spieler, f&uuml;r diesen wird ein Spielfeld erzeugt
	 */
	public AbstractPlayer() {
		board = new Board();
		expectedCall = NONE;
	}

	// ------------------------------------------------------------

	/**
	 * Setze das Spielerboard. Ist n&ouml;tig um dem Spieler nach dem Laden ein
	 * ver&auml;ndertes Spielerboard zu geben
	 * 
	 * @param board
	 *            Spielbrett
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Setze die Spielerfarben
	 * 
	 * @param colors
	 *            Farben des Spielers
	 */
	public void setColors(int[] colors) {
		this.colors = new int[colors.length];

		// Zieht dieser Spieler zuerst? Das ist der Fall, wenn er die Farbe blau
		// bekommt
		expectedCall = UPDATE;

		boolean start = false;
		// Uebernehme die Farben
		for (int i = 0; i < colors.length; i++) {
			this.colors[i] = colors[i];
			if (colors[i] == BLUE)
				start = true;
		}

		if (start)
			setExpectedCall(REQUEST);
		else
			setExpectedCall(UPDATE);

		IO.debug("Colors ");
		for (int c : colors)
			IO.debug(colorString[c] + " ");
		IO.debugln("set for player " + this + " @ AbstractPlayer.setColors");
	}

	// ------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Move request() throws Exception, RemoteException {
		IO.debugln("Player " + this + " REQUEST @ AbstractPlayer.request");
		if (expectedCall == NONE)
			throw new PlayerException("Spieler muss zuerst resetted werden!");
		if (expectedCall != REQUEST)
			throw new PlayerException("Falsche Aufrufreihenfolge der Player-Funktionen, erwartet: REQUEST");

		lastMove = deliver();
		IO.debugln("Player sent " + lastMove + " @ AbstractPlayer.request");

		/* Erwarte als naechstes den folgenden Aufruf */
		setExpectedCall(CONFIRM);
		return lastMove;
	}

	/**
	 * Fordert den Zug vom Spieler an, muss &uuml;berschrieben werden
	 * 
	 * @return Zug
	 * @throws Exception
	 *             Fehler
	 */
	public abstract Move deliver() throws Exception;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void confirm(Status boardStatus) throws Exception, RemoteException {
		IO.debugln("Player " + this + " CONFIRM @ AbstractPlayer.confirm");
		if (expectedCall == NONE)
			throw new PlayerException("Spieler muss zuerst resetted werden!");
		if (expectedCall != CONFIRM)
			throw new PlayerException("Falsche Aufrufreihenfolge der Player-Funktionen, erwartet: CONFIRM");

		/* Fuehre letzten eigenen Zug aus */
		board.makeMove(lastMove);

		/* Vergleiche den Status */
		if (!board.getStatus().equals(boardStatus))
			throw new PlayerException("Boardstatus des Spiels " + boardStatus + " und Status des eigenen Player-Boards "
					+ board.getStatus() + " stimmen nicht ueberein");

		/* Erwarte als naechstes den folgenden Aufruf */
		if (imNext())
			setExpectedCall(REQUEST);
		else
			setExpectedCall(UPDATE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException {
		IO.debugln("Player " + this + " UPDATE @ AbstractPlayer.update");
		if (expectedCall == NONE)
			throw new PlayerException("Spieler muss zuerst resetted werden!");
		if (expectedCall != UPDATE)
			throw new PlayerException("Falsche Aufrufreihenfolge der Player-Funktionen, erwartet: UPDATE");

		/* Fuehre gegnerischen Zug aus */
		board.makeMove(opponentMove);

		/* Vergleiche den Status */
		if (!board.getStatus().equals(boardStatus))
			throw new PlayerException("Boardstatus des Spiels " + boardStatus + " und Status des eigenen Player-Boards "
					+ board.getStatus() + " stimmen nicht ueberein");

		/* Erwarte als naechstes den folgenden Aufruf */
		if (imNext())
			setExpectedCall(REQUEST);
		else
			setExpectedCall(UPDATE);
	}

	// ------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset(int[] colors) throws Exception, RemoteException {
		setColors(colors);

		board.reset();

		IO.debugln("Resetted player " + this + " @ AbstractPlayer.reset");
	}

	/**
	 * Pr&uuml;fe, ob der Spieler auch mit der n&auml;chsten Farbe am Zug ist
	 * 
	 * @return Als n&auml;chstes am Zug
	 */
	private boolean imNext() {
		int nextColor = board.getActiveColor();
		for (int color : colors)
			if (color == nextColor)
				return true;
		return false;
	}

	/**
	 * Stelle ein, welcher Aufruf als n&auml;chstes erwartet wird
	 * 
	 * @param call
	 *            Aufruf
	 */
	private void setExpectedCall(int call) {
		expectedCall = call;
		IO.debugln("Next expected call for player " + this + " is " + callString[call]
				+ " @ AbstractPlayer.setExpectedCall");
	}

}
