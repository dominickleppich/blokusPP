package blokusPP.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import blokusPP.player.PlayerObject;
import blokusPP.preset.Setting;
import eu.nepster.toolkit.io.IO;

/**
 * <h1>MatchSetup</h1>
 * 
 * <p>
 * Hier werden alle Einstellungen f&uuml;r ein Spiel festgelegt
 * </p>
 * 
 * @author Dominick Leppich
 *
 */
public class MatchSetup implements Setting {
	private static int gameCounter = 1;

	// ------------------------------------------------------------

	// Spielname
	private String gameName;

	// Welcher Spieler spielt mich welcher Farbe
	private HashMap<PlayerObject, int[]> players;

	// Wurden alle Farben einem Spieler zugewiesen?
	private boolean colorIsSet[];

	// ------------------------------------------------------------

	/**
	 * Erzeugt ein neues MatchSetup Objekt, dieses ist zu Beginn leer
	 */
	public MatchSetup() {
		gameName = "Game " + gameCounter++;
		players = new HashMap<PlayerObject, int[]>();
		colorIsSet = new boolean[GAME_COLORS];
	}

	// ------------------------------------------------------------

	/**
	 * Setze Spielname
	 * 
	 * @param gameName
	 *            Spielname
	 */
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	/**
	 * Gib Spielnamen zur&uuml;ck
	 * 
	 * @return Spielname
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * F&uuml;ge einen neuen Spieler hinzu
	 * 
	 * @param player
	 *            Spielerobjekt
	 * @param colors
	 *            Farben mit denen er spielt
	 */
	public void addPlayer(PlayerObject player, int colors[]) {
		if (player == null)
			throw new MatchSetupException("Player cannot be null");

		if (colors == null || colors.length > 4)
			throw new MatchSetupException("Invalid colors for player");

		if (players.containsKey(player))
			throw new MatchSetupException("Can't add player second time");

		// Pruefe die einzelnen Farben und teste ob diese existieren und noch
		// frei sind
		for (int c : colors)
			if (c < 0 || c >= GAME_COLORS || colorIsSet[c])
				throw new MatchSetupException("Color " + colorString[c] + " already used");

		// Alles okay, dann fuege den Spieler fuer jede Farbe hinzu
		players.put(player, colors);

		// Setze Farben des Spielers als belegt
		for (int c : colors)
			colorIsSet[c] = true;

		IO.debugln("Player " + player + " (" + player.getName() + ") added @ MatchSetup.addPlayer");
	}

	// ------------------------------------------------------------
	// Methoden die mit Farben arbeiten

	/**
	 * Gib das Spielerobjekt einer Farbe zur&uuml;ck
	 * 
	 * @param color
	 *            Farbe
	 * @return Spielerobjekt
	 */
	public PlayerObject getPlayer(int color) {
		if (!colorIsSet[color])
			return null;

		// Durchsuche alle gespeicherten Spieler
		for (Entry<PlayerObject, int[]> e : players.entrySet()) {
			// Durchsuche die Farben des aktuellen Spielers
			for (int i = 0; i < e.getValue().length; i++)
				if (e.getValue()[i] == color)
					return e.getKey();
		}
		// Nicht gefunden, null (kann eigentlich nicht auftreten)
		return null;
	}

	// ------------------------------------------------------------
	// Methoden die mit Playern arbeiten

	/**
	 * Gib die Farben eines Spielers zur&uuml;ck
	 * 
	 * @param player
	 *            Spieler
	 * @return Farben
	 */
	public int[] getPlayerColors(PlayerObject player) {
		if (!players.containsKey(player))
			return null;

		return players.get(player);
	}

	/**
	 * Gib alle gegnerischen Spieler eines Spielers zur&uuml;ck
	 * 
	 * @param player
	 *            Spieler
	 * @return Alle anderen Spieler
	 */
	public ArrayList<PlayerObject> getOpponentPlayers(PlayerObject player) {
		if (!isReady())
			return null;

		ArrayList<PlayerObject> res = new ArrayList<PlayerObject>();

		for (PlayerObject p : players.keySet())
			if (p != player && !res.contains(p))
				res.add(p);

		return res;
	}

	// ------------------------------------------------------------

	/**
	 * Gib alle Spieler zur&uuml;ck
	 * 
	 * @return ArrayList von Spielern
	 */
	public ArrayList<PlayerObject> getPlayers() {
		if (!isReady())
			return null;

		ArrayList<PlayerObject> res = new ArrayList<PlayerObject>();
		for (PlayerObject p : players.keySet())
			if (!res.contains(p))
				res.add(p);
		return res;
	}

	// ------------------------------------------------------------

	/**
	 * Sind alle erforderlichen Einstellungen gemacht?
	 * 
	 * @return Spiel kann beginnen
	 */
	public boolean isReady() {
		// Wurde jede Farbe einem Spieler zugewiesen?
		for (boolean b : colorIsSet)
			if (!b)
				return false;

		return true;
	}
}
