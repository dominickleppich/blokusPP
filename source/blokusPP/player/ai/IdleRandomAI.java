package blokusPP.player.ai;

import java.util.Random;

import blokusPP.preset.Move;
import eu.nepster.toolkit.io.IO;

/**
 * RandomAI Spieler, der vor jedem Zug eine zuf&auml;llige Zeit wartet
 * 
 * @author Dominick Leppich
 *
 */
public class IdleRandomAI extends RandomAI {
	private int minWait, maxWait;
	private Random rnd;

	// ------------------------------------------------------------

	/**
	 * Erzeuge neuen Idle RandomAI Spieler
	 * 
	 * @param minWait
	 *            minimale Wartezeit
	 * @param maxWait
	 *            maximale Wartezeit
	 */
	public IdleRandomAI(int minWait, int maxWait) {
		this.minWait = minWait;
		this.maxWait = maxWait;
		rnd = new Random(System.currentTimeMillis());
	}
	
	// ------------------------------------------------------------
	
	/**
	 * Liefere einen Zug
	 * 
	 * @return Zug
	 */
	public Move deliver() {
		Move m = null;
		try {
			m = super.deliver();
			int sleep = minWait + (maxWait - minWait > 0 ? rnd.nextInt(maxWait - minWait) : 0);
			if (sleep > 0)
				Thread.sleep(sleep);
		} catch (Exception e) {
			IO.errorln("Error getting move @ IdleRandomAI.deliver");
		}
		return m;
	}
}
