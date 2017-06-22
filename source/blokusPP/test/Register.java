package blokusPP.test;

import blokusPP.Start;
import blokusPP.net.RMI;
import blokusPP.player.ai.RandomAI;
import eu.nepster.toolkit.settings.Settings;

public class Register {
	public static void main(String[] args) {
		Start.initSettings(args);
		Start.initIO();

		// RMI.startRegistry(1099);
		RMI.registerPlayer("localhost", 1099, new RandomAI(), Settings.CFG.getString("p"));
		while (true)
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
	}
}
