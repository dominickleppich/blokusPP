package blokusPP.graphic;

import blokusPP.board.Polyomino;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.gfx.objects.GraphicContainer;
import eu.nepster.toolkit.gfx.objects.image.GraphicStaticImage;

/**
 * Anzeige, wo ein GraphicPolyomino gedreht und gespiegelt werden kann.
 * 
 * @author Dominick Leppich
 *
 */
public class GraphicPolyominoRotationFlipper extends GraphicContainer implements GraphicConstants {
	private static final long serialVersionUID = 1L;

	// Aktuelle Farbe
	private int color;
	private GraphicStaticImage overlay;
	private GraphicContainer graphicPolyominoContainer;
	private GraphicPolyomino graphicPolyomino;

	public static enum MODE {
		ROTATE, FLIP;
	}

	// ------------------------------------------------------------

	/**
	 * Erzeuge neue Anzeige
	 */
	public GraphicPolyominoRotationFlipper() {
		init();
	}

	/**
	 * Initialisiere Anzeige
	 */
	private void init() {
		overlay = new GraphicStaticImage();
		overlay.addImage("rotate", GraphicLoader.GFX.get("overlay.rotate"));
		overlay.addImage("flip", GraphicLoader.GFX.get("overlay.flip"));
		overlay.translate(-overlay.getImage().getWidth() / 2, -overlay.getImage().getHeight() / 2);
		overlay.setAlpha(GRAPHIC_POLYOMINO_OVERLAY_ALPHA);
		add(overlay);
		graphicPolyominoContainer = new GraphicContainer();
		add(graphicPolyominoContainer);
	}

	// ------------------------------------------------------------

	/**
	 * Setze Modus
	 * 
	 * @param mode
	 *            Modus
	 */
	public void setMode(MODE mode) {
		if (mode == MODE.ROTATE)
			overlay.setImage("rotate");
		else
			overlay.setImage("flip");
	}

	/**
	 * Setze Farbe des Steins
	 * 
	 * @param color
	 *            Farbe
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * Setze das zu bearbeitende Polyomino
	 * 
	 * @param polyomino
	 *            Polyomino
	 */
	public void setPolyomino(Polyomino polyomino) {
		graphicPolyomino = new GraphicPolyomino(polyomino);
		graphicPolyomino.setActive(true);
		graphicPolyomino.setColor(color);
		graphicPolyominoContainer.clear();
		graphicPolyominoContainer.add(graphicPolyomino);
		double maxDimension = Math.max(graphicPolyomino.getWidth(), graphicPolyomino.getHeight());
		graphicPolyominoContainer.setToTranslation(-maxDimension / 2, -maxDimension / 2);
	}

	/**
	 * Gib bearbeitetes Polyomino zur&uuml;ck
	 * 
	 * @return Polyomino
	 */
	public Polyomino getPolyomino() {
		return graphicPolyomino.getPolyomino();
	}

	// ------------------------------------------------------------

	/**
	 * Drehe Polyomino nach rechts
	 */
	public void rotateRight() {
		graphicPolyomino.rotateRight();
	}

	/**
	 * Drehe Polyomino nach links
	 */
	public void rotateLeft() {
		graphicPolyomino.rotateLeft();
	}

	/**
	 * Spiegle Polyomino horizontal
	 */
	public void flipHorizontal() {
		graphicPolyomino.flipHorizontal();
	}

	/**
	 * Spiegle Polyomino vertikal
	 */
	public void flipVertical() {
		graphicPolyomino.flipVertical();
	}
}
