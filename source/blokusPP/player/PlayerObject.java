package blokusPP.player;

import blokusPP.preset.Player;

/**
 * Datenstruktur, die neben dem Spieler auch weitere Informationen &uuml,ber ihn
 * sammeln kann.
 * 
 * @author Dominick Leppich
 */
public class PlayerObject implements Comparable<PlayerObject> {
	private static int playerCount = 1;

	private Player player;
	private String name;
	private int gamesPlayed;
	private int gamesWon;

	// ------------------------------------------------------------

	/**
	 * Erzeuge neues Spielerobjekt
	 * 
	 * @param player
	 *            Spieler
	 * @param name
	 *            Name
	 */
	public PlayerObject(Player player, String name) {
		this.player = player;
		this.name = name;
		if (name == null || name == "")
			name = "Player " + playerCount++;
		if (player instanceof HumanPlayer)
			((HumanPlayer) player).setName(name);
		this.gamesPlayed = 0;
		this.gamesWon = 0;
	}

	// ------------------------------------------------------------

	/**
	 * Gib die Spielerreferenz zur&uuml;ck
	 * 
	 * @return Spieler Refernz
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gib den Namen des Spielers zur&uuml;ck
	 * 
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setze den Spielernamen
	 * 
	 * @param name
	 *            Name
	 */
	public void setName(String name) {
		this.name = name;
		if (player instanceof HumanPlayer)
			((HumanPlayer) player).setName(name);
	}

	/**
	 * Gib die Anzahl der gespielten Spiele zur&uuml;ck
	 * 
	 * @return Anzahl gespielte Spiele
	 */
	public int getGamesPlayed() {
		return gamesPlayed;
	}

	/**
	 * Gib die Anzahl der gewonnenen Spiele zur&uuml;ck
	 * 
	 * @return Anzahl gewonnener Spiele
	 */
	public int getGamesWon() {
		return gamesWon;
	}

	/**
	 * Gib die Anzahl der verlorenen Spiele zur&uuml;ck
	 * 
	 * @return Anzahl verlorener Spiele
	 */
	public int getGamesLost() {
		return gamesPlayed - gamesWon;
	}

	// ------------------------------------------------------------

	/**
	 * Hat ein Spiel gewonnen
	 */
	public void win() {
		gamesPlayed++;
		gamesWon++;
	}

	/**
	 * Hat ein Spiel verloren
	 */
	public void lose() {
		gamesPlayed++;
	}

	// ------------------------------------------------------------

	/**
	 * Vergleich auf Gleichheit mit einem Spieler Objekt
	 * 
	 * @param object
	 *            Spieler
	 * @return Gleichheit
	 */
	public boolean equals(Object object) {
		if (object == null)
			return false;

		if (object instanceof PlayerObject)
			return ((PlayerObject) object).player == this.player || ((PlayerObject) object).name.equals(this.name);
		else if (object instanceof Player)
			return ((Player) object) == this.player;

		return false;
	}

	/**
	 * Vergleiche Spieler anhand der Punktezahl
	 */
	@Override
	public int compareTo(PlayerObject arg0) {
		if (arg0 == null)
			return -1;
		
		if (arg0.gamesWon == gamesWon)
			return 0;
		return gamesWon > arg0.gamesWon ? 1 : -1;
	}
}
