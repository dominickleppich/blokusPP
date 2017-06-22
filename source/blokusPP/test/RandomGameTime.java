package blokusPP.test;

import blokusPP.test.board.MonteCarloBoard;

/**
 * Misst die Zeit f&uuml;r ein Random Game
 * 
 * @author Dominick Leppich
 *
 */
public class RandomGameTime {
	public static void main(String[] args) {
		double sumNew = 0.0;
		int countNew = 0;
		while (true) {
			double measure;
			measure = measureNew();
			sumNew += measure;
			System.out.println("Game new: " + countNew + "\tTime:" + measure + "\tAvg:" + (sumNew / countNew));
		}
	}

	// ------------------------------------------------------------
	

	/**
	 * Messe Zeit
	 * 
	 * @return Zeit
	 */
	private static double measureNew() {
		MonteCarloBoard board = new MonteCarloBoard();
		long start = System.currentTimeMillis();
		board.simulateGame(board.getValidMoves().iterator().next());
		return (double) (System.currentTimeMillis() - start) / 1000;
	}
}
