package blokusPP.player.ai;

import java.util.ArrayList;
import java.util.Random;

import blokusPP.board.Polyomino;
import blokusPP.player.AbstractPlayer;
import blokusPP.preset.Move;

/**
 * Liefert willk&uuml;rliche Z&uuml;ge, vermutlich nicht erlaubte. Nur zu
 * Testzwecken
 * 
 * @author Dominick Leppich
 *
 */
public class IllegalAI extends AbstractPlayer {

	/**
	 * Liefert einen unsinnigen Zug zur&uuml;ck
	 * 
	 * @return unsinniger Zug
	 */
	@Override
	public Move deliver() throws Exception {
		Random rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		ArrayList<Polyomino> polyominos = board.getAvailablePolyominos();
		Polyomino res = polyominos.get(rnd.nextInt(polyominos.size()));
		res = res.move(rnd.nextInt(15), rnd.nextInt(15));
		for (int i = 0; i < 3; i++)
			if (rnd.nextBoolean())
				res = res.rotate(Polyomino.ROTATE_90);
		if (rnd.nextBoolean())
			res = res.flip(Polyomino.FLIP_HORIZONTAL);
		if (rnd.nextBoolean())
			res = res.flip(Polyomino.FLIP_VERTICAL);
		return res;
	}

}
