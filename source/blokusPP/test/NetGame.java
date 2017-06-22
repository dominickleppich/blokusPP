package blokusPP.test;

import java.util.ArrayList;

import blokusPP.Start;
import blokusPP.game.Match;
import blokusPP.game.MatchSetup;
import blokusPP.graphic.GameWindow;
import blokusPP.net.RMI;
import blokusPP.player.PlayerObject;
import blokusPP.preset.Setting;
import eu.nepster.toolkit.settings.Settings;

public class NetGame implements Setting {
	public static void main(String[] args) {
		Start.initSettings(args);
		Start.initIO();
		Start.initLanguage();
		Start.initGraphics();

		GameWindow gui = Start.initGui();

		for (String s : RMI.listPlayers("localhost", Settings.CFG.getInt("rmi-port")))
			System.out.println("Registered: " + s);
		// System.out.println();
		// RMI.unregisterPlayer("localhost", "karl");
		// for (String s : RMI.listPlayers("localhost"))
		// System.out.println("Registered: " + s);
		MatchSetup setup;
		Match match;

		PlayerObject p1, p2, p3;
		players = new ArrayList<PlayerObject>();
		p1 = new PlayerObject(RMI.getPlayer("localhost", Settings.CFG.getInt("rmi-port"), "wilhelm"), "Wilhelm");
		p2 = new PlayerObject(RMI.getPlayer("localhost", Settings.CFG.getInt("rmi-port"), "karl"), "Karl");
		p3 = new PlayerObject(RMI.getPlayer("localhost", Settings.CFG.getInt("rmi-port"), "tobi"), "Tobi");

		players.add(p1);
		players.add(p2);

		while (true) {
			setup = new MatchSetup();
			setup.addPlayer(p1, new int[] { RED, BLUE });
			setup.addPlayer(p2, new int[] { GREEN, YELLOW });
			match = new Match(setup, gui);
			match.startMatch();
			match.waitMatchEnd();
			showPlayerStats();

			setup = new MatchSetup();
			setup.addPlayer(p2, new int[] { RED, BLUE });
			setup.addPlayer(p3, new int[] { GREEN, YELLOW });
			match = new Match(setup, gui);
			match.startMatch();
			match.waitMatchEnd();
			showPlayerStats();

			setup = new MatchSetup();
			setup.addPlayer(p3, new int[] { RED, BLUE });
			setup.addPlayer(p1, new int[] { GREEN, YELLOW });
			match = new Match(setup, gui);
			match.startMatch();
			match.waitMatchEnd();
			showPlayerStats();
		}
	}

	private static ArrayList<PlayerObject> players;

	private static void showPlayerStats() {
		System.out.println("---------------------------------------------------------------\nStats:");
		for (PlayerObject p : players)
			System.out.println("- " + p.getName() + "\t\t: Played: " + p.getGamesPlayed() + ",\tWins: "
					+ p.getGamesWon() + ",\tLost: " + p.getGamesLost());
		System.out.println("---------------------------------------------------------------");
	}
}
