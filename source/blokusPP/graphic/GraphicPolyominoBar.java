package blokusPP.graphic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import blokusPP.board.Polyomino;
import eu.nepster.toolkit.gfx.objects.GraphicContainer;
import eu.nepster.toolkit.gfx.objects.GraphicShape;
import eu.nepster.toolkit.io.IO;

/**
 * Zeigt alle verf&uuml;gbaren Steine eines Spielers an, sodass dieser dann mit
 * der Maus ausw&auml;hlen kann, welchen er nehmen m&ouml;chte.
 * 
 * @author Dominick Leppich
 *
 */
public class GraphicPolyominoBar extends GraphicContainer implements GraphicConstants {
	private static final long serialVersionUID = 1L;

	private int slideCount;

	private final int maxNeighbors;
	private ArrayList<GraphicPolyomino> polyominos;
	private LinkedList<GraphicPolyomino> shownPolyominos;
	private boolean[] available;
	private int index;

	private GraphicShape overlay;
	private GraphicContainer polyominoContainer;

	private double width, height;
	private double space;

	private MODE mode;

	enum MODE {
		SHOW, SLIDE_LEFT, SLIDE_RIGHT;
	}

	// ------------------------------------------------------------

	/**
	 * Erzeuge neue grafische PolyominoBar
	 */
	public GraphicPolyominoBar() {
		this(MAX_NEIGHBORS);
	}

	/**
	 * Erzeuge neue grafische PolyominoBar
	 * 
	 * @param maxNeighbors
	 *            Anzahl maximaler Nachbarn
	 */
	public GraphicPolyominoBar(int maxNeighbors) {
		this.maxNeighbors = maxNeighbors;
		init();
	}

	/**
	 * Initialisiere alle Grafiken
	 */
	private void init() {
		// Overlay
		overlay = new GraphicShape(
				new Rectangle2D.Double(0, 0, (1 + 2 * maxNeighbors) * MAX_POLY_WIDTH * 5, MAX_POLY_HEIGHT), Color.BLACK,
				true);
		overlay.setAlpha(POLYOMINO_BAR_OVERLAY_ALPHA);
		add(overlay);

		polyominoContainer = new GraphicContainer();
		add(polyominoContainer);

		// Alle Polyominos
		polyominos = new ArrayList<GraphicPolyomino>();
		for (Polyomino poly : Polyomino.getAllPolyominos()) {
			GraphicPolyomino graphicPolyomino = new GraphicPolyomino(poly);
			polyominos.add(graphicPolyomino);
		}
		available = new boolean[polyominos.size()];
		// TODO muss automatisch erkannt werden
		for (int i = 0; i < available.length; i++)
			available[i] = true;

		index = 0;

		calculateDimensions();
		calculatePolyominos();

		mode = MODE.SHOW;
	}

	/**
	 * Berechne Dimensionen
	 */
	private void calculateDimensions() {
		width = (1 + 2 * maxNeighbors) * MAX_POLY_WIDTH * getScaleX();
		height = MAX_POLY_HEIGHT * getScaleY();
	}

	/**
	 * Berechne die Positionen der Polyominos neu und bestimme, welche sichtbar
	 * sind
	 */
	private void calculatePolyominos() {
		// Array von sichtbaren Polyominos
		shownPolyominos = new LinkedList<GraphicPolyomino>();
		// Muss noch korrekt bestimmt werden (falls zwischendurch einer fehlt)

		for (GraphicPolyomino poly : polyominos)
			poly.setVisible(false);

		int count, i;
		// Fuege Index hinzu
		GraphicPolyomino active = polyominos.get(index);
		shownPolyominos.add(active);
		active.setActive(true);
		active.setAlpha(1.0f);
		active.setVisible(true);

		// Suche jetzt maxNeighbors Polyominos als Nachfolger
		count = 0;
		i = (index + 1) % polyominos.size();
		while (count < maxNeighbors + 1) {
			if (available[i]) {
				GraphicPolyomino poly = polyominos.get(i);
				shownPolyominos.addLast(poly);
				poly.setActive(false);
				float alpha = MAX_NEIGHBOR_ALPHA - NEIGHBOR_ALPHA_LOSE * count;
				if (alpha < 0.0f)
					alpha = 0.0f;
				poly.setAlpha(alpha);
				poly.setVisible(true);
				count++;
			}
			i = (i + 1) % polyominos.size();
		}
		// Suche maxNeighbors nach links
		count = 0;
		i = (index + polyominos.size() - 1) % polyominos.size();
		while (count < maxNeighbors + 1) {
			if (available[i]) {
				GraphicPolyomino poly = polyominos.get(i);
				shownPolyominos.addFirst(poly);
				poly.setActive(false);
				float alpha = MAX_NEIGHBOR_ALPHA - NEIGHBOR_ALPHA_LOSE * count;
				if (alpha < 0.0f)
					alpha = 0.0f;
				poly.setAlpha(alpha);
				poly.setVisible(true);
				count++;
			}
			i = (i + polyominos.size() - 1) % polyominos.size();
		}

		synchronized (this) {
			polyominoContainer.clear();
			// Korrekte Position einstellen
			for (i = 0; i < shownPolyominos.size(); i++) {
				GraphicPolyomino poly = shownPolyominos.get(i);
				poly.setToTranslation(
						MAX_POLY_WIDTH * (i - 1) + i * space + (MAX_POLY_WIDTH - poly.getWidth()) / 2 - space / 2,
						(MAX_POLY_HEIGHT - poly.getHeight()) / 2);
				polyominoContainer.add(poly);
			}
		}
	}

	// ------------------------------------------------------------

	/**
	 * Gehe zum n&auml;chsten Polyomino
	 */
	public void next() {
		polyominos.get(index).setActive(false);
		do {
			index = (index + 1) % polyominos.size();
		} while (!available[index]);
		slideCount = 0;
		mode = MODE.SLIDE_LEFT;
		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
			IO.errorln("InterruptedException " + e + " @ GraphicPolyominoBar.next");
		}
	}

	/**
	 * Gehe zur&uuml;ck zum letzten vorherigen
	 */
	public void previous() {
		polyominos.get(index).setActive(false);
		do {
			index = (index + polyominos.size() - 1) % polyominos.size();
		} while (!available[index]);
		slideCount = 0;
		mode = MODE.SLIDE_RIGHT;
		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
			IO.errorln("InterruptedException " + e + " @ GraphicPolyominoBar.previous");
		}
	}

	/**
	 * Setze alle verf&uuml;gbaren Polyominos
	 * 
	 * @param availablePolyominos
	 *            Verf&uuml;gbare Polyominos
	 */
	public void setAvailablePolyominos(ArrayList<Polyomino> availablePolyominos) {
		for (int i = 0; i < polyominos.size(); i++)
			available[i] = Polyomino.contains(availablePolyominos, polyominos.get(i).getPolyomino());
		// Neuen Startindex bestimmen
		index = 0;
		while (!available[index])
			index++;
		calculatePolyominos();
	}

	/**
	 * Gib ausgew&auml;hltes Polyomino zur&uuml;ck
	 * 
	 * @return Polyomino
	 */
	public Polyomino getPolyomino() {
		return polyominos.get(index).getPolyomino();
	}

	/**
	 * Setze Farbe des grafischen Polyominos
	 * 
	 * @param color
	 *            Farbe
	 */
	public void setColor(int color) {
		for (GraphicPolyomino poly : polyominos)
			poly.setColor(color);
	}

	// ------------------------------------------------------------

	/**
	 * Gib Breite des grafischen Polyominos zur&uuml;ck
	 * 
	 * @return Breite
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Gib H&ouml;he des grafischen Polyominos zur&uuml;ck
	 * 
	 * @return H&ouml;he
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Skaliere das Objekt. Muss &uuml;berschrieben werden, um Dimensionen bei
	 * Skalierung anzupassen.
	 * 
	 * @param width
	 *            neue Breite
	 * @param height
	 *            neue H&ouml;he
	 */
	@Override
	public void scale(double width, double height) {
		double polyWidth = height;
		double polyHeight = height;
		double scaleX = polyWidth / MAX_POLY_WIDTH;
		double scaleY = polyHeight / MAX_POLY_HEIGHT;

		overlay.setShape(new Rectangle2D.Double(0, 0, width, height));

		polyominoContainer.setToScale(scaleY, scaleY);

		double freeSpace = width - (1 + 2 * maxNeighbors) * polyWidth;
		space = freeSpace / (1 + 2 * maxNeighbors) / scaleX;

		calculateDimensions();
		calculatePolyominos();
	}

	// ------------------------------------------------------------

	/**
	 * Zeichnen
	 * 
	 * @param g
	 *            Graphics2D Kontext
	 * @param superTransform
	 *            Transformation des &uuml;bergeordneten Containers
	 * @param alpha
	 *            Alpha-Wert des &uuml;bergeordneten Containers
	 */
	@Override
	public synchronized void draw(Graphics2D g, AffineTransform superTransform, float alpha) {
		super.draw(g, superTransform, alpha);
	}

	/**
	 * Slide Animation
	 */
	public void tick() {
		super.tick();

		switch (mode) {
		case SHOW:
			break;
		case SLIDE_LEFT:
			if (slideCount++ < SLIDE_STEP)
				for (GraphicPolyomino poly : shownPolyominos)
					poly.translate(-(double) (MAX_POLY_WIDTH + space) / SLIDE_STEP, 0);
			else {
				calculatePolyominos();
				mode = MODE.SHOW;
				synchronized (this) {
					notify();
				}
			}
			break;
		case SLIDE_RIGHT:
			if (slideCount++ < SLIDE_STEP)
				for (GraphicPolyomino poly : shownPolyominos)
					poly.translate((double) (MAX_POLY_WIDTH + space) / SLIDE_STEP, 0);
			else {
				calculatePolyominos();
				mode = MODE.SHOW;
				synchronized (this) {
					notify();
				}
			}
			break;
		}
	}
}
