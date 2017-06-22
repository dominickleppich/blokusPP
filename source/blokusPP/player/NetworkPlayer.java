package blokusPP.player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import blokusPP.preset.Move;
import blokusPP.preset.Player;
import blokusPP.preset.Status;

/**
 * Network player
 * 
 * @author Dominick Leppich
 *
 */
public class NetworkPlayer extends UnicastRemoteObject implements Player {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	// Reference to real player to play with
	private Player player;

	// ------------------------------------------------------------

	/**
	 * Create a network player given a local player
	 * 
	 * @param player
	 *            Player to offer to the network
	 * @throws RemoteException
	 */
	public NetworkPlayer(Player player) throws RemoteException {
		super();
		this.player = player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Move request() throws Exception, RemoteException {
		return player.request();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void confirm(Status boardStatus) throws Exception, RemoteException {
		player.confirm(boardStatus);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException {
		player.update(opponentMove, boardStatus);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset(int[] colors) throws Exception, RemoteException {
		player.reset(colors);
	}
}
