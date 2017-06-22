package blokusPP.test;

import javax.swing.JFrame;

import blokusPP.Start;
import blokusPP.game.Match;
import blokusPP.game.MatchSetup;
import blokusPP.graphic.GameWindow;
import blokusPP.player.PlayerObject;
import blokusPP.player.ai.IdleRandomAI;
import blokusPP.preset.Setting;
import eu.nepster.toolkit.settings.Settings;

public class TimeTest implements Setting {
	public static void main(String[] args) {
		Start.initSettings(args);
		Start.initIO();
		Start.initLanguage();
		Start.initGraphics();

		GameWindow gui = Start.initGui();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Settings.CFG.set("max-move-time", 5000L);
		Settings.CFG.set("max-player-time", 5000L);
//		Settings.CFG.set("max-move-time", 0L);
//		Settings.CFG.set("max-player-time", 0L);

		int idleTime = 1000;
		PlayerObject p1 = new PlayerObject(new IdleRandomAI(idleTime, idleTime), "Oleg");
		PlayerObject p2 = new PlayerObject(new IdleRandomAI(idleTime, idleTime), "Andrej");
		PlayerObject p3 = new PlayerObject(new IdleRandomAI(idleTime, idleTime), "Carsten");
		MatchSetup setup;
		Match m;
		
		setup = new MatchSetup();
		setup.addPlayer(p1, new int[] { BLUE, RED });
		setup.addPlayer(p2, new int[] { YELLOW, GREEN });
//		setup.addPlayer(p3, new int[] { RED });
		m = new Match(setup, gui);
		gui.setVisible(true);
		m.showVS();
		m.startMatch();
		m.waitMatchEnd();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setup = new MatchSetup();
		setup.addPlayer(p1, new int[] { BLUE, RED });
		setup.addPlayer(p2, new int[] { YELLOW, GREEN });
//		setup.addPlayer(p3, new int[] { RED });
		m = new Match(setup, gui);
		gui.setVisible(true);
		m.showVS();
		m.startMatch();
		m.waitMatchEnd();
	}
}
