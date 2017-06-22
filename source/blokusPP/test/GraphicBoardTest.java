package blokusPP.test;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import blokusPP.board.Polyomino;
import blokusPP.graphic.GraphicBoard;
import blokusPP.preset.Position;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.gfx.objects.GraphicContainer;
import eu.nepster.toolkit.gui.GameFrame;
import eu.nepster.toolkit.settings.Settings;

public class GraphicBoardTest extends GameFrame {
	private static final long serialVersionUID = 1L;

	private GraphicContainer all;
	private GraphicBoard board;

	public GraphicBoardTest() {
		super(1000, 1000, "test", true, 1.0);
		init();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFps(60);
		startRefresh();
		setVisible(true);

	}

	// ------------------------------------------------------------

	private void init() {
		all = new GraphicContainer();
		board = new GraphicBoard();
		board.scale(0.25, 0.25);
		System.out.println(board.getWidth() + ", " + board.getHeight());
		board.setToTranslation((getWidth() - board.getWidth()) / 2, (getHeight() - board.getHeight()) / 2);
		board.scale(0.25, 0.25);
		ArrayList<Position> corners = new ArrayList<Position>();
		corners.add(new Position(0, 0));
		corners.add(new Position(19, 0));
		corners.add(new Position(0, 19));
		corners.add(new Position(19, 19));
		board.highlightPositions(corners, 0);
		board.highlightPolyomino(Polyomino.POLY_F.move(4, 8), 2, 0.6f);
		board.setHighlighting(true);
		all.add(board);
	}

	// ------------------------------------------------------------

	@Override
	public void tick() {
		all.tick();

	}

	@Override
	public void draw(Graphics2D g) {
		all.draw(g);
	}

	public void mouseClicked(MouseEvent e) {
		Position p = board.getMouseBoardPosition(e.getX(), e.getY());
		if (p != null)
			System.out.println(p);
	}

	// ------------------------------------------------------------

	public static void main(String[] args) {
		GraphicLoader.GFX.loadSynchron(new File("res/gfx"), true, "");
		Settings.CFG.add("gui-debug", false);
		new GraphicBoardTest();
	}
}
