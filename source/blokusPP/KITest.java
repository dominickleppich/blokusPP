package blokusPP;

import blokusPP.test.Match;
import eu.nepster.toolkit.settings.Settings;

import javax.swing.JFrame;

import blokusPP.game.MatchSetup;
import blokusPP.graphic.GameWindow;
import blokusPP.net.RMI;
import blokusPP.player.PlayerObject;
import blokusPP.player.ai.RandomAI;
import blokusPP.preset.Player;
import blokusPP.preset.Setting;

/**
 * Startet KI-Tests
 * 
 * @author Dominick Leppich
 *
 */
public class KITest implements Setting {

	public static void main(String[] args) {
		Start.initSettings(new String[] {""});
		Settings.CFG.set("max-player-time", 0L);
		Settings.CFG.set("max-move-time", 0L);
		Settings.CFG.set("min-timeout", 10);
		Start.initIO();
		Start.initLanguage();
		Start.initGraphics();
		GameWindow gui = Start.initGui();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		while (true) {
			String[] list = RMI.listPlayers("localhost", 1099);
			if (list.length > 0) {
				RandomAI rnd = new RandomAI();
				Player p = RMI.getPlayer("localhost", 1099, list[0]);
				Match m;
				MatchSetup m1, m2;
				PlayerObject p1 = new PlayerObject(rnd, "Random");
				PlayerObject p2 = new PlayerObject(p, list[0]);
				while (true)  {
					m1 = new MatchSetup();
					m1.addPlayer(p1, new int[]{BLUE, RED});
					m1.addPlayer(p2, new int[]{YELLOW, GREEN});
					m2 = new MatchSetup();
					m2.addPlayer(p1, new int[]{YELLOW, GREEN});
					m2.addPlayer(p2, new int[]{BLUE, RED});
					m = new Match(m1, gui);
					gui.setVisible(true);
					m.startMatch();
					m.waitMatchEnd();
					m = new Match(m2, gui);
					gui.setVisible(true);
					m.startMatch();
					m.waitMatchEnd();
				}
			}
		}
	}

}
