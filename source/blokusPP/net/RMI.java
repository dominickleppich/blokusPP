package blokusPP.net;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import blokusPP.player.NetworkPlayer;
import blokusPP.preset.Player;
import eu.nepster.toolkit.io.IO;

/**
 * Diese Klasse ist f&uuml;r das Starten, Beenden und die Kommunikation mit der
 * RMI Registry verantwortlich.
 * 
 * @author Dominick Leppich
 *
 */
public class RMI {
	/**
	 * Starte eine RMI Registry
	 * 
	 * @param port
	 *            Port
	 */
	public static void startRegistry(int port) {
		try {
			LocateRegistry.createRegistry(port);
			IO.debugln("RMI registry started on port " + port + " @ RMI.startRegistry");
		} catch (Exception e) {
			IO.errorln("Error starting RMI registry @ RMI.startRegistry");
//			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------

	/**
	 * Registriere einen Spieler in der RMI Registry
	 * 
	 * @param host
	 *            Host der RMI Registry
	 * @param port
	 *            Port
	 * @param player
	 *            Spieler
	 * @param name
	 *            Spielername
	 */
	public static void registerPlayer(String host, int port, Player player, String name) {
		try {
			Registry registry = LocateRegistry.getRegistry(host, port);
			// Erzeuge NetworkPlayer
			NetworkPlayer netPlayer = new NetworkPlayer(player);
			registry.rebind(name, netPlayer);
			IO.debugln("Player " + player + " (" + name + ") registered on RMI registry " + host
					+ " @ RMI.registerPlayer");
		} catch (RemoteException e) {
			IO.errorln("Error registering player " + player + " (" + name + "). RemoteException @ RMI.registerPlayer");
//			e.printStackTrace();
		}
	}

	/**
	 * Entferne einen Spieler aus der RMI Registry
	 * 
	 * @param host
	 *            Host der RMI Registry
	 * @param port
	 *            Port
	 * @param name
	 *            Spielername
	 */
	public static void unregisterPlayer(String host, int port, String name) {
		try {
			Registry registry = LocateRegistry.getRegistry(host, port);
			registry.unbind(name);
			IO.debugln("Player (" + name + ") unregistered from RMI registry " + host + " @ RMI.unregisterPlayer");
		} catch (RemoteException e) {
			IO.errorln("Error unregistering player (" + name + "). RemoteException @ RMI.unregisterPlayer");
//			e.printStackTrace();
		} catch (NotBoundException e) {
			IO.errorln("Error unregistering player (" + name + "). Player not bound @ RMI.unregisterPlayer");
//			e.printStackTrace();
		}
	}

	/**
	 * Gib eine Liste aller registrierten Spielernamen zur&uuml;ck.
	 * 
	 * @param host
	 *            Host der RMI Registry
	 * @param port
	 *            Port
	 * @return Liste aller Spielernamen
	 */
	public static String[] listPlayers(String host, int port) {
		try {
			Registry registry = LocateRegistry.getRegistry(host, port);
			String[] s = registry.list();
			for (String player : s)
				IO.debugln("Found player (" + player + ") on RMI registry " + host + " @ RMI.listPlayers");
			return s;
		} catch (RemoteException e) {
			IO.errorln("Error listing players. RemoteException @ RMI.listPlayers");
//			e.printStackTrace();
		}
		// Sonst gib leeres Array zurueck
		return new String[0];
	}

	/**
	 * Gib einen bei der RMI Registry registrierten Spieler zur&uuml;ck
	 * 
	 * @param host
	 *            Host der RMI Registry
	 * @param name
	 *            Spielername
	 */
	public static Player getPlayer(String host, int port, String name) {
		try {
			Registry registry = LocateRegistry.getRegistry(host, port);
			Player player = (Player) registry.lookup(name);
			IO.debugln("Got player " + player + "(" + player + ") from RMI registry " + host + " @ RMI.getPlayer");
			return player;
		} catch (RemoteException e) {
			IO.errorln("Error getting player (" + name + "). RemoteException @ RMI.getPlayer");
//			e.printStackTrace();
		} catch (NotBoundException e) {
			IO.errorln("Error getting player (" + name + "). Player not bound @ RMI.getPlayer");
//			e.printStackTrace();
		}
		// Falls Spieler nicht existiert
		return null;
	}
}
