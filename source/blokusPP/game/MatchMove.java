package blokusPP.game;

import blokusPP.io.HTMLOutput;
import blokusPP.preset.Move;
import blokusPP.preset.Setting;
import blokusPP.preset.Status;

/**
 * Spielzug wird in dieser Datenstruktur abgelegt
 * 
 * @author Dominick Leppich
 *
 */
public class MatchMove implements Setting, HTMLOutput {
	public final Move move;
	public final int number;
	public final String boardCode;
	public final String playerName;
	public final int color;
	public final long time;
	public final Status status;
	public final int[] scores;

	// ------------------------------------------------------------

	/**
	 * Erzeuge neues Spielzug Objekt
	 * 
	 * @param move
	 *            Zug
	 * @param number
	 *            Zug Nummer
	 * @param boardCode
	 *            HTML Repr&auml;sentation des Spielbretts
	 * @param playerName
	 *            Name des Spielers
	 * @param color
	 *            Farbe
	 * @param time
	 *            Zugzeit
	 * @param status
	 *            Status nach dem Zug
	 * @param scores
	 *            Punktest&auml;nde
	 */
	public MatchMove(Move move, int number, String boardCode, String playerName, int color, long time, Status status,
			int[] scores) {
		this.move = move;
		this.number = number;
		this.boardCode = boardCode;
		this.playerName = playerName;
		this.color = color;
		this.time = time;
		this.status = status;
		this.scores = scores;
	}

	// ------------------------------------------------------------
	
	/**
	 * Gib eine HTML Repr&auml;sentation des Objektes zur&uuml;ck
	 * 
	 * @return HTML String
	 */
	@Override
	public String html() {
		String res = "";
		String colorString = "";
		switch (color) {
		case BLUE:
			colorString = "blue";
			break;
		case YELLOW:
			colorString = "yellow";
			break;
		case RED:
			colorString = "red";
			break;
		case GREEN:
			colorString = "green";
			break;
		}
		res += "\t\t<div class=\"move_" + colorString + "\">\n";
		res += "\t\t\t<table border=\"0\">\n";
		res += "\t\t\t\t<tr>\n";
		res += "\t\t\t\t\t<td align=\"right\"><b>Spieler:</b></td>\n";
		res += "\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;<b>" + playerName + "</b></td>\n";
		res += "\t\t\t\t</tr>\n";
		res += "\t\t\t\t<tr>\n";
		res += "\t\t\t\t\t<td align=\"right\"><b>Zug " + number + ":</b></td>\n";
		res += "\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + move + "</td>\n";
		res += "\t\t\t\t</tr>\n";
		String s1, s2;
		s1 = String.valueOf(time / 1000);
		s2 = String.valueOf(time % 1000);

		while (s2.length() < 3)
			s2 = "0" + s2;
		res += "\t\t\t\t<tr>\n";
		res += "\t\t\t\t\t<td align=\"right\"><b>Zeit:</b></td>\n";
		res += "\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + s1 + "." + s2 + "s</td>\n";
		res += "\t\t\t\t</tr>\n";
		res += "\t\t\t\t<tr>\n";
		res += "\t\t\t\t\t<td align=\"right\"><b>Status:</b></td>\n";
		res += "\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + status + "</td>\n";
		res += "\t\t\t\t</tr>\n";
		res += "\t\t\t\t<tr>\n";
		res += "\t\t\t\t\t<td align=\"right\"><b>Punkte:</b></td>\n";
		res += "\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;BLUE:" + scores[BLUE] + " YELLOW:" + scores[YELLOW] + " RED:" + scores[RED] + " GREEN:" + scores[GREEN] + "</td>\n";
		res += "\t\t\t\t</tr>\n";
		res += "\t\t\t</table>\n";
		res += "\t\t\t<br />\n";
		res += boardCode;
		res += "\t\t</div>\n";

		return res;
	}
}