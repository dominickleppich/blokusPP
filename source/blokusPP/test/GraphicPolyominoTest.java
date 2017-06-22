package blokusPP.test;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;

import blokusPP.board.Polyomino;
import blokusPP.graphic.GraphicPolyomino;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.gfx.objects.GraphicContainer;
import eu.nepster.toolkit.gui.GameFrame;

public class GraphicPolyominoTest extends GameFrame {
	private static final long serialVersionUID = 1L;

	private GraphicContainer all;
	private GraphicPolyomino poly;

	public GraphicPolyominoTest() {
		super(800, 800, "test", true, 1.0);
		init();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFps(60);
		startRefresh();
		setVisible(true);

	}

	// ------------------------------------------------------------

	private void init() {
		all = new GraphicContainer();
		poly = new GraphicPolyomino(Polyomino.POLY_L);
//		poly.translate(300, 300);
		// poly.scale(0.5, 0.5);
		poly.setActive(true);
		all.add(poly);
//		all.translate((getWidth() - poly.getWidth()) / 2, (getHeight() - poly.getHeight()) / 2);

//		center();
	}

	// ------------------------------------------------------------

	@Override
	public void tick() {
		all.tick();

		if (isTextEntered("on")) {
			resetText();
			poly.setActive(true);
		}
		if (isTextEntered("off")) {
			resetText();
			poly.setActive(false);
		}
		if (isTextEntered("blue")) {
			resetText();
			poly.setColor(0);
		}
		if (isTextEntered("yellow")) {
			resetText();
			poly.setColor(1);
		}
		if (isTextEntered("red")) {
			resetText();
			poly.setColor(2);
		}
		if (isTextEntered("green")) {
			resetText();
			poly.setColor(3);
		}
		if (isTextEntered("reset")) {
			resetText();
			poly.reset();
			center();
		}
	}

	@Override
	public void draw(Graphics2D g) {
		all.draw(g);
	}

	// ------------------------------------------------------------

	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);

		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			poly.translate(-10, 0);
			break;
		case KeyEvent.VK_RIGHT:
			poly.translate(10, 0);
			break;
		case KeyEvent.VK_UP:
			poly.translate(0, -10);
			break;
		case KeyEvent.VK_DOWN:
			poly.translate(0, 10);
			break;
		case KeyEvent.VK_ADD:
			poly.scale(1.1, 1.1);
			break;
		case KeyEvent.VK_SUBTRACT:
			poly.scale(0.9, 0.9);
			break;
		case KeyEvent.VK_4:
			poly.rotateRight();
			break;
		case KeyEvent.VK_1:
			poly.rotateLeft();
			break;
		case KeyEvent.VK_2:
			poly.flipHorizontal();
			break;
		case KeyEvent.VK_3:
			poly.flipVertical();
			break;

		}

//		center();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int count = e.getWheelRotation();
		if (count > 0)
			// while (count-- > 0)
			poly.rotateRight();
		else if (count < 0)
			// while (count++ < 0)
			poly.rotateLeft();

//		center();
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3)
			poly.flipHorizontal();
		else if (e.getButton() == MouseEvent.BUTTON2)
			poly.flipVertical();
		
//		center();
	}

	private void center() {
		System.out.println("center");
		double scale = poly.getScaleX();
		poly.setToTranslation((getWidth() - poly.getWidth()) / 2, (getHeight() - poly.getHeight()) / 2);
		poly.scale(scale, scale);
	}

	// ------------------------------------------------------------

	public static void main(String[] args) {
		GraphicLoader.GFX.loadSynchron(new File("res/gfx"), true, "");
		new GraphicPolyominoTest();
	}
}
