package blokusPP.test;

import java.util.Random;

import javax.swing.JFrame;

import blokusPP.Start;
import blokusPP.graphic.GameWindow;

public class GameFrameTest {
	public static void main(String[] args) {
		try {
			Start.initSettings(args);
			Start.initIO();
			Start.initLanguage();
			Start.initGraphics();
			GameWindow g = Start.initGui();
			g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			g.setFps(60);
			g.startRefresh();
			g.setVisible(true);

			// Thread.sleep(1000);
			//
			// g.vs("Dominick", 0, 2, "Christoph", 1, 3);
			// g.waitGameWindow();
			// System.out.println("Ende");
			// g.vs("Lena", 0, 1, "Marcel", 2, 3);
			// g.waitGameWindow();

			// Thread.sleep(1000);
			//
			// for (int i = 25; i >= 0; i--) {
			// g.setPlayerTime(0, i, i <= 10);
			// Thread.sleep(100);
			// }
			//
			// for (double i = 0.0; i <= 1.0; i+=0.01) {
			// g.setMoveTime(i);
			// Thread.sleep(100);
			// }
			Random rnd = new Random(System.currentTimeMillis());
			for (int i = 0; i < 1000; i++) {
				g.setPlayerColor(rnd.nextInt(2), rnd.nextInt(4));
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
		}
	}
}
