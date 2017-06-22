package blokusPP.net;

import blokusPP.player.NetworkPlayer;
import blokusPP.preset.Player;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.io.output.SystemOut;
import eu.nepster.toolkit.plugin.ObjectLoader;

public class DummyNetPlayer {
	private static Object monitor = new Object();

	/**
	 * Erzeuge einen Netzwerkspieler und melde ihn bei einer RMI Registry an,
	 * indem ein Objekt einer lokalen KI erzeugt wird.
	 * 
	 * Parameter 1 - Pfad zur KI Parameter 2 - Name der KI Parameter 3 - RMI
	 * Host Parameter 4 - RMI Port
	 * 
	 * @param args
	 *            Parameter
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java -jar DummyNetPlayer.jar <path-to-local-ai> <name-of-ai>");
			System.out.println("Example: java -jar DummyNetPlayer.jar blokusPP.player.Random RandomPlayer");
		} else {
			IO.register(new SystemOut(), IO.LEVEL_ALL_OUTPUT, true);
			System.out.println("\n---------------------------------------------------------------\n");
			try {
				ObjectLoader<Player> loader = new ObjectLoader<Player>();
				Player p = loader.loadClass(args[0]);
				System.out.println("AI " + args[0] + " loaded correctly.");
				NetworkPlayer np = new NetworkPlayer(p);
				RMI.registerPlayer("localhost", 1099, np, args[1]);
				synchronized (monitor) {
					monitor.wait();
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
