package blokusPP.player.ai;

import java.util.HashSet;
import java.util.Random;

import blokusPP.player.AbstractPlayer;
import blokusPP.preset.Move;

/**
 * <h1>RandomAI</h1>
 * 
 * <p>
 * Diese KI liefert einfach willk&uuml;rlich zuf&auml;llige aber g&uuml;ltige Z&uuml;ge
 * </p>
 * 
 * @author Dominick Leppich
 *
 */
public class RandomAI extends AbstractPlayer {
	private Random rnd;
	
	// ------------------------------------------------------------
	
	/**
	 * Erzeuge neue RandomAI
	 */
	public RandomAI() {
		super();
		rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
	}

	// ------------------------------------------------------------

	/**
	 * Liefere einen zuf&auml;lligen g&uuml;ltigen Zug
	 * 
	 * @return Zug
	 */
	@Override
	public Move deliver() throws Exception {
		HashSet<Move> possibleMoves = board.getValidMoves();
		
		if (possibleMoves.isEmpty())
			return null;
		int count = 0;
		int index = rnd.nextInt(possibleMoves.size());
		for (Move move : possibleMoves) {
			if (index == count++)
				return move;
		}
		// Sollte nicht auftreten
		return null;
	}

}
