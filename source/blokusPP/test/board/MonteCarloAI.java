package blokusPP.test.board;

import java.util.HashSet;
import java.util.Iterator;

import blokusPP.player.AbstractPlayer;
import blokusPP.preset.Move;
import eu.nepster.toolkit.io.IO;

/**
 * MonteCarlo AI
 * 
 * @author Dominick Leppich
 *
 */
public class MonteCarloAI extends AbstractPlayer {
	private static final int ITERATIONS = 1;

	// ------------------------------------------------------------

	/**
	 * Probiere f&uuml;r alle m&ouml;glichen Z&uuml;ge aus, wie viele
	 * Zuf&auml;llige Spiele mit diesem Zug zum Sieg f&uuml;hren. W&auml;hle den
	 * Zug mit den meisten Siegen
	 */
	@Override
	public Move deliver() throws Exception {
		// Zeit messen
		long start = System.currentTimeMillis();

		// TODO Testen

		// Fuehre fuer jeden moeglichen Zug eine Simulation durch
		HashSet<Move> moves = board.getValidMoves();
		Iterator<Move> it = moves.iterator();
		Move bestMove = null;
		int mostWins = 0;
		int count = 0;
		int[] wins = new int[moves.size()];

		int maxIt = moves.size() * ITERATIONS;
		IO.debugln("Starting simulation with " + maxIt + " iterations @ MonteCarloAI.deliver");
		while (it.hasNext()) {
			Move move = it.next();
			for (int iteration = 0; iteration < ITERATIONS; iteration++) {
				int itNo = ITERATIONS * count + iteration + 1;
				IO.debugln("Simulation No " + itNo + "/" + maxIt + " (" + ((double) itNo / maxIt)
						+ ") @ MonteCarloAI.deliver");

				MonteCarloBoard copy = new MonteCarloBoard(board);
				if (isWon(copy.simulateGame(move, 11)))
					wins[count]++;
				if (wins[count] > mostWins) {
					mostWins = wins[count];
					bestMove = move;
				}
			}
			count++;
		}

		IO.debugln("Finished simulation in " + ((double) (System.currentTimeMillis() - start) / 1000)
				+ "s @ MonteCarloAI.deliver");
		return bestMove;
	}

	// ------------------------------------------------------------

	/**
	 * Bestimmt bei einem Punktestand, ob der aktuelle Spieler Sieger ist
	 * 
	 * @param score
	 *            Punktestand
	 * @return Sieger
	 */
	private boolean isWon(int[] score) {
		int myScore = 0;
		int oppScore = 0;

		// Pruefe ob der Score mir oder dem anderen Spieler gehoert
		for (int color = 0; color < GAME_COLORS; color++) {
			boolean isMyColor = false;
			for (int myColor : colors) {
				if (myColor == color) {
					isMyColor = true;
					break;
				}
			}
			if (isMyColor)
				myScore += score[color];
			else
				oppScore += score[color];
		}

		return myScore > oppScore;
	}

}
