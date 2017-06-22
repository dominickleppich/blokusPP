package blokusPP.preset;

import java.rmi.*;

public interface Player extends Remote {
	/**
	 * Fordert einen Zug als <code>Move</code> Objekt vom Spieler an.
	 * 
	 * @return Zug
	 * @throws Exception
	 * @throws RemoteException
	 */
	Move request() throws Exception, RemoteException;

	/**
	 * Best&auml;tigt einen vom Spieler gemachten Zug. Es wird der Status des
	 * Spielbretts nach diesem Zug mit &uuml;bergeben, um Unstimmigkeiten
	 * zwischen dem eigenen und dem Board der Hauptklasse zu ermitteln.
	 * 
	 * @param boardStatus
	 *            Status des Hauptboards
	 * @throws Exception
	 * @throws RemoteException
	 */
	void confirm(Status boardStatus) throws Exception, RemoteException;

	/**
	 * Informiert einen Spieler &uuml;ber den Zug des / der gegnerischen
	 * Spieler. Neben dem Zug wird auch der Status des Zuges auf dem
	 * gegnerischen Board &uuml;bermittelt, um Unstimmigkeiten erkennen zu
	 * k&ouml;nnen.
	 * 
	 * @param opponentMove
	 *            Gegnersicher Zug
	 * @param boardStatus
	 *            Status des gegnerischen Boards
	 * @throws Exception
	 * @throws RemoteException
	 */
	void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException;

	/**
	 * Setze den Spieler zur&uuml;ck und setze seine Farben. Nach diesem
	 * Methodenaufruf ist es m&ouml;glich mit dem Spieler ein neues Spiel zu
	 * starten.
	 * 
	 * @param colors Neue Farben des Spielers
	 * @throws Exception
	 * @throws RemoteException
	 */
	void reset(int[] colors) throws Exception, RemoteException;
}
