package blokusPP.player;

import java.rmi.RemoteException;

import blokusPP.graphic.GameWindow;
import blokusPP.preset.Move;
import blokusPP.preset.Requestable;
import blokusPP.preset.Status;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.lang.Language;

/**
 * Menschlicher Spieler
 * 
 * @author Dominick Leppich
 *
 */
public class HumanPlayer extends AbstractPlayer {
	// Objekt von dem Zuege angefordert werden
	private Requestable requestable;
	private String playerName;

	// ------------------------------------------------------------

	/**
	 * Erzeuge neuen menschlichen Spieler
	 * 
	 * @param requestable
	 *            Objekt von dem Z&uuml;ge angefordert werden
	 */
	public HumanPlayer(Requestable requestable) {
		this.requestable = requestable;
	}

	// ------------------------------------------------------------

	/**
	 * Setze Namen
	 * 
	 * @param playerName
	 *            Name
	 */
	public void setName(String playerName) {
		this.playerName = playerName;
	}

	// ------------------------------------------------------------

	/**
	 * Fordere einen Zug vom entsprechenden Objekt an und liefere ihn
	 * zur&uuml;ck
	 * 
	 * @return Zug
	 */
	@Override
	public Move deliver() throws Exception {
		if (requestable instanceof GameWindow) {
			GameWindow gui = (GameWindow) requestable;
			gui.setVisible(true);
			gui.updateGui();
			IO.print("gui", Language.get("your_turn", playerName));
		}

		return requestable.deliver();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 * @throws RemoteException
	 */
	@Override
	public void confirm(Status boardStatus) throws RemoteException, Exception {
		super.confirm(boardStatus);
		if (requestable instanceof GameWindow) {
			GameWindow gui = (GameWindow) requestable;
			gui.updateGui();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 * @throws RemoteException
	 */
	@Override
	public void update(Move opponentMove, Status boardStatus) throws RemoteException, Exception {
		super.update(opponentMove, boardStatus);
		if (requestable instanceof GameWindow) {
			GameWindow gui = (GameWindow) requestable;
			gui.updateGui();
		}
	}

	// ------------------------------------------------------------

	/**
	 * Setze Spieler zur&uuml;ck. Falls Gui noch keinen Viewer hat, setze den
	 * Viewer dieses Spielers
	 * 
	 * @throws Exception
	 * @throws RemoteException
	 * 
	 */
	@Override
	public void reset(int[] colors) throws RemoteException, Exception {
		super.reset(colors);

		if (requestable instanceof GameWindow) {
			GameWindow gui = (GameWindow) requestable;
			gui.setViewer(board.viewer());
			gui.updateGui();
		}
	}

}
