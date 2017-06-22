package blokusPP.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;

import blokusPP.graphic.GraphicPolyominoBar;
import blokusPP.preset.Setting;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.gfx.objects.GraphicContainer;
import eu.nepster.toolkit.gui.GameFrame;

public class GraphicPolyominoBarTest extends GameFrame implements Setting {
	private static final long serialVersionUID = 1L;

	private GraphicContainer all;
	private GraphicPolyominoBar bar;

	public GraphicPolyominoBarTest() {
		super(1500, 1000, "test", true, 1.0);
		init();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFps(60);
		startRefresh();
		setVisible(true);

	}

	// ------------------------------------------------------------

	private void init() {
		all = new GraphicContainer();
		bar = new GraphicPolyominoBar(3);
		bar.scale(0.25, 0.25);

		all.add(bar);
	}

	// ------------------------------------------------------------

	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			bar.previous();
			break;
		case KeyEvent.VK_RIGHT:
			bar.next();
			break;
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int count = e.getWheelRotation();
		if (count > 0)
			// while (count-- > 0)
			bar.next();
		else if (count < 0)
			// while (count++ < 0)
			bar.previous();
	}

	// ------------------------------------------------------------

	@Override
	public void tick() {
		all.tick();

		if (isTextEntered("blue")) {
			resetText();
			bar.setColor(BLUE);
		}
		if (isTextEntered("yellow")) {
			resetText();
			bar.setColor(YELLOW);
		}
		if (isTextEntered("red")) {
			resetText();
			bar.setColor(RED);
		}
		if (isTextEntered("green")) {
			resetText();
			bar.setColor(GREEN);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		all.draw(g);
	}

	// ------------------------------------------------------------

	public static void main(String[] args) {
		GraphicLoader.GFX.loadSynchron(new File("res/gfx"), true, "");
		new GraphicPolyominoBarTest();
	}
}
