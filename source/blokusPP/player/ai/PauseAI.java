package blokusPP.player.ai;

import blokusPP.player.AbstractPlayer;
import blokusPP.preset.Move;

/**
 * Die KI sendet einfach <code>null</code> und pausiert mit seinen Farben. Nur
 * zu Testzwecken vorhanden.
 * 
 * @author Dominick Leppich
 *
 */
public class PauseAI extends AbstractPlayer {

	/**
	 * Liefere <code>null</code> als Zug zur&uuml;ck
	 * 
	 * @return Spielzug
	 */
	@Override
	public Move deliver() throws Exception {
		return null;
	}

}
