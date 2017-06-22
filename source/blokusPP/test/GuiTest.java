package blokusPP.test;

import java.io.File;

import javax.swing.JFrame;

import blokusPP.board.Board;
import blokusPP.board.Polyomino;
import blokusPP.graphic.GameWindow;
import blokusPP.io.AsciiBoard;
import blokusPP.preset.Move;
import blokusPP.preset.Viewer;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.io.output.SystemOut;

public class GuiTest {
	private static Board b;
	private static Viewer v;
	private static GameWindow gui;

	// ------------------------------------------------------------

	public static void main(String[] args) {
		init();

		AsciiBoard.print(v);

		makeMove(Polyomino.POLY_t);
		makeMove(Polyomino.POLY_3.move(4, 4));
		makeMove(Polyomino.POLY_3.move(5, 4));
		// Zug ist illegal, Gruen darf nochmal ziehen
		makeMove(Polyomino.POLY_3.move(5, 4));
		makeMove(Polyomino.POLY_3.move(6, 4));
		// Ungueltige Verschiebung mit move, der Zug wird null, Blau pausiert
		makeMove(Polyomino.POLY_1.move(-1, 0));
		makeMove(Polyomino.POLY_X.move(8, 8));
		makeMove(Polyomino.POLY_X.move(11, 8));
		makeMove(Polyomino.POLY_X.move(8, 11));
		makeMove(Polyomino.POLY_X.move(11, 11));
		// Lassen wir die naechsten zwei Farben auch pausieren
		makeMove(null);
		makeMove(null);
		makeMove(Polyomino.POLY_U.move(1, 17));
		makeMove(Polyomino.POLY_U.move(5, 17));
		makeMove(Polyomino.POLY_U.move(9, 17));
		makeMove(Polyomino.POLY_U.move(13, 17));
	}

	// ------------------------------------------------------------

	/**
	 * Init everything
	 */
	private static void init() {
		IO.register(new SystemOut(), IO.LEVEL_ALL_OUTPUT, true);

//		GraphicLoader.GFX.showLoading(true);
		GraphicLoader.GFX.loadSynchron(new File("res/gfx"), true, "");

		b = new Board();
		v = b.viewer();
		gui = new GameWindow();
		gui.setViewer(v);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setFps(60);
		gui.startRefresh();
	}

	/**
	 * make move and update graphic
	 * 
	 * @param move
	 *            Move
	 */
	private static void makeMove(Move move) {
		try {
			Thread.sleep(100);
		} catch(Exception e) {}
		b.makeMove(move);
		System.out.println("\n--------------------\n");
		AsciiBoard.print(v);
		System.out.println(b.getStatus());
		gui.updateGui();
	}
}
