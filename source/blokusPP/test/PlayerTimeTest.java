package blokusPP.test;

import java.awt.Graphics2D;

import javax.swing.JFrame;

import blokusPP.Start;
import blokusPP.graphic.GraphicPlayerTime;
import eu.nepster.toolkit.gui.GameFrame;

public class PlayerTimeTest extends GameFrame {
	private static final long serialVersionUID = 1L;

	public PlayerTimeTest(int width, int height, String title, boolean activeRender, double lowRenderPercentage) {
		super(width, height, title, activeRender, lowRenderPercentage);
		// TODO Auto-generated constructor stub
		gpt = new GraphicPlayerTime(200, 50);
		gpt.translate(400, 300);
		this.setFps(60);
		this.startRefresh();
		this.setVisible(true);
	}
	
	GraphicPlayerTime gpt;
	
	// ------------------------------------------------------------

	double a = 0.0;
	
	@Override
	public void tick() {
		gpt.setProgress(1.0);
//		gpt.setProgress(a);
//		a += 0.01;
//		if (a > 1.0)
//			a = 0;
	}

	@Override
	public void draw(Graphics2D g) {
		gpt.draw(g);
	}
	
	// ------------------------------------------------------------
	
	public static void main(String[] args) {
		Start.initGraphics();
		PlayerTimeTest p = new PlayerTimeTest(800, 600, "Test", true, 1.0);
		p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
