package blokusPP.graphic;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import blokusPP.board.Polyomino;
import blokusPP.preset.Position;
import blokusPP.preset.Setting;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.gfx.objects.GraphicContainer;
import eu.nepster.toolkit.gfx.objects.image.GraphicStaticImage;
import eu.nepster.toolkit.io.IO;

/**
 * Grafische Repr&auml;sentation eine Polyomino Steins
 * 
 * @author Dominick Leppich
 */
public class GraphicPolyomino extends GraphicContainer implements Setting, GraphicConstants {
	private static final long serialVersionUID = 1L;

	private MODE mode;

	private Polyomino resetPolyomino;
	private Polyomino polyomino;
	private boolean active;

	private int highlightCount = 0;
	private boolean highlightDimOn;

	private int transformCount = 0;

	private GraphicContainer highlightContainer;
	private GraphicStaticImage[] highlightGraphics;
	private GraphicStaticImage[] squareGraphics;

	private int rotation;

	private double width, height;
	private double translateX, translateY;

	enum MODE {
		SHOW, ROTATING_LEFT, ROTATING_RIGHT, FLIPPING_HORIZONTAL, FLIPPING_VERTICAL;
	};

	// ------------------------------------------------------------

	/**
	 * Erzeuge neues grafische Polyomino von einem Polyomino
	 * 
	 * @param polyomino
	 *            Polyomino
	 */
	public GraphicPolyomino(Polyomino polyomino) {
		resetPolyomino = polyomino.norm();
		this.polyomino = resetPolyomino;
		mode = MODE.SHOW;
		active = false;
		highlightDimOn = true;
		init();
	}

	// ------------------------------------------------------------

	/**
	 * Setzt Polyomino zur&uuml;ck
	 */
	public void reset() {
		polyomino = resetPolyomino;
		rotation = 0;
		calculatePositions();
	}

	/**
	 * Lege f&uuml;r jedes Quadrat des Polyominos Grafiken an
	 */
	private void init() {
		highlightContainer = new GraphicContainer();
		int size = polyomino.getPolyomino().size();
		highlightGraphics = new GraphicStaticImage[size];
		squareGraphics = new GraphicStaticImage[size];
		for (int i = 0; i < size; i++) {
			squareGraphics[i] = new GraphicStaticImage();
			GraphicStaticImage active = new GraphicStaticImage(GraphicLoader.GFX.get("field.active"));
			active.translate(-FIELD_WIDTH, -FIELD_HEIGHT);
			highlightGraphics[i] = active;
			highlightContainer.add(active);
			GraphicStaticImage square = new GraphicStaticImage();
			square.addImage("blue", GraphicLoader.GFX.get("field.blue"));
			square.addImage("yellow", GraphicLoader.GFX.get("field.yellow"));
			square.addImage("red", GraphicLoader.GFX.get("field.red"));
			square.addImage("green", GraphicLoader.GFX.get("field.green"));
			squareGraphics[i] = square;
		}
		add(highlightContainer);
		for (int i = 0; i < size; i++)
			add(squareGraphics[i]);

		reset();
		
		translate(translateX, translateY);
	}

	/**
	 * Berechne korrekte Positionen
	 */
	private synchronized void calculatePositions() {
		int index = 0;
		for (Position p : polyomino.getPolyomino()) {
			highlightGraphics[index].setToTranslation((p.getLetter() - 1) * FIELD_WIDTH,
					(p.getNumber() - 1) * FIELD_HEIGHT);
			squareGraphics[index].setToTranslation((double) p.getLetter() * FIELD_WIDTH + FIELD_WIDTH / 2,
					(double) p.getNumber() * FIELD_HEIGHT + FIELD_HEIGHT / 2);
			squareGraphics[index].rotate(Math.toRadians(90 * rotation));
			squareGraphics[index].translate((double) -FIELD_WIDTH / 2, (double) -FIELD_HEIGHT / 2);
			index++;
		}
		calculateDimensions();
	}

	/**
	 * Berechne Dimensionen
	 */
	private void calculateDimensions() {
		width = polyomino.getWidth() * FIELD_WIDTH;
		height = polyomino.getHeight() * FIELD_HEIGHT;
		if (width > height) {
			translateX = 0;
			translateY = (width - height) / 2;
		} else {
			translateX = (height - width) / 2;
			translateY = 0;
		}
	}

	// ------------------------------------------------------------

	/**
	 * Setze Farbe des grafischen Polyominos
	 * 
	 * @param color
	 *            Farbe
	 */
	public void setColor(int color) {
		String selection = "";
		switch (color) {
		case BLUE:
			selection = "blue";
			break;
		case YELLOW:
			selection = "yellow";
			break;
		case RED:
			selection = "red";
			break;
		case GREEN:
			selection = "green";
			break;
		default:
			throw new IllegalArgumentException("No color " + color + " defined!");
		}
		for (GraphicStaticImage img : squareGraphics)
			img.setImage(selection);
	}

	/**
	 * Aktivier oder deaktiviere das Polyomino
	 * 
	 * @param active
	 *            Status
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

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
	 * Gib aktuelles Polyomino zur&uuml;ck
	 * 
	 * @return Polyomino
	 */
	public Polyomino getPolyomino() {
		return polyomino;
	}

	// ------------------------------------------------------------

	/**
	 * Drehe Polyomino nach rechts
	 */
	public synchronized void rotateRight() {
		if (mode != MODE.SHOW)
			return;

		mode = MODE.ROTATING_RIGHT;
		transformCount = 0;
		try {
			wait();
		} catch (InterruptedException e) {
			IO.errorln("InterruptedException " + e + " @ GraphicPolyomino.rotateRight");
		}
	}

	/**
	 * Drehe Polyomino nach links
	 */
	public synchronized void rotateLeft() {
		if (mode != MODE.SHOW)
			return;

		mode = MODE.ROTATING_LEFT;
		transformCount = 0;
		try {
			wait();
		} catch (InterruptedException e) {
			IO.errorln("InterruptedException " + e + " @ GraphicPolyomino.rotateLeft");
		}
	}

	/**
	 * Spiegle Polyomino horizontal
	 */
	public synchronized void flipHorizontal() {
		if (mode != MODE.SHOW)
			return;

		mode = MODE.FLIPPING_HORIZONTAL;
		transformCount = 0;
		try {
			wait();
		} catch (InterruptedException e) {
			IO.errorln("InterruptedException " + e + " @ GraphicPolyomino.flipHorizontal");
		}
	}

	/**
	 * Spiegle Polyomino vertikal
	 */
	public synchronized void flipVertical() {
		if (mode != MODE.SHOW)
			return;

		mode = MODE.FLIPPING_VERTICAL;
		transformCount = 0;
		try {
			wait();
		} catch (InterruptedException e) {
			IO.errorln("InterruptedException " + e + " @ GraphicPolyomino.flipVertical");
		}
	}

	// ------------------------------------------------------------

	/**
	 * Animation des Highlights
	 */
	@Override
	public void tick() {
		if (!isVisible())
			return;

		// Hightlight pochen
		if (highlightCount > 0 && highlightCount < HIGHLIGHT_STEP) {
			highlightContainer.setAlpha(1.0f / HIGHLIGHT_STEP * highlightCount);
			if (highlightDimOn)
				highlightCount++;
			else
				highlightCount--;
		} else if (highlightCount == HIGHLIGHT_STEP) {
			highlightContainer.setAlpha(1.0f);
			highlightDimOn = false;
			highlightCount--;
		} else if (highlightCount == 0 && active) {
			highlightContainer.setAlpha(0.0f);
			highlightDimOn = true;
			highlightCount++;
		} else {
			highlightContainer.setAlpha(0.0f);
			highlightDimOn = true;
		}
		
		switch (mode) {
		case SHOW:
			break;
		case ROTATING_LEFT:
			if (transformCount < ROTATE_STEP) {
				translate(getWidth() / 2, getHeight() / 2);
				rotate(-Math.toRadians(90.0 / ROTATE_STEP));
				translate(-getWidth() / 2, -getHeight() / 2);
			} else {
				polyomino = polyomino.rotateLeft();
				rotation = (rotation + 3) % 4;
				calculatePositions();
				setToTranslation(translateX, translateY);
				mode = MODE.SHOW;
				synchronized (this) {
					notify();
				}
			}
			transformCount++;
			break;
		case ROTATING_RIGHT:
			if (transformCount < ROTATE_STEP) {
				translate(getWidth() / 2, getHeight() / 2);
				rotate(Math.toRadians(90.0 / ROTATE_STEP));
				translate(-getWidth() / 2, -getHeight() / 2);
			} else {
				polyomino = polyomino.rotateRight();
				rotation = (rotation + 1) % 4;
				calculatePositions();
				setToTranslation(translateX, translateY);
				mode = MODE.SHOW;
				synchronized (this) {
					notify();
				}
			}
			transformCount++;
			break;
		case FLIPPING_HORIZONTAL:
			if (transformCount < FLIP_STEP / 2) {
				double scale = 1.0 - (double) transformCount * 2 / FLIP_STEP;
				setToTranslation((double) translateX + polyomino.getWidth() * (1 - scale) * FIELD_WIDTH / 2, translateY);
				scale(scale, 1.0);
			} else if (transformCount == FLIP_STEP / 2) {
				scale(0.0, 1.0);
				polyomino = polyomino.flip(Polyomino.FLIP_HORIZONTAL);
				calculatePositions();
			} else if (transformCount < FLIP_STEP) {
				double scale = (double) (transformCount - FLIP_STEP / 2) * 2 / FLIP_STEP;
				setToTranslation((double) translateX + polyomino.getWidth() * (1 - scale) * FIELD_WIDTH / 2, translateY);
				scale(scale, 1.0);
			} else {
				setToTranslation(translateX, translateY);
				calculatePositions();
				mode = MODE.SHOW;
				synchronized (this) {
					notify();
				}
			}
			transformCount++;
			break;
		case FLIPPING_VERTICAL:
			if (transformCount < FLIP_STEP / 2) {
				double scale = 1.0 - (double) transformCount * 2 / FLIP_STEP;
				setToTranslation(translateX, (double) translateY + polyomino.getHeight() * (1 - scale) * FIELD_HEIGHT / 2);
				scale(1.0, scale);
			} else if (transformCount == FLIP_STEP / 2) {
				scale(1.0, 0.0);
				polyomino = polyomino.flip(Polyomino.FLIP_VERTICAL);
				calculatePositions();
			} else if (transformCount < FLIP_STEP) {
				double scale = (double) (transformCount - FLIP_STEP / 2) * 2 / FLIP_STEP;
				setToTranslation(translateX, (double) translateY + polyomino.getHeight() * (1 - scale) * FIELD_HEIGHT / 2);
				scale(1.0, scale);
			} else {
				setToTranslation(translateX, translateY);
				calculatePositions();
				mode = MODE.SHOW;
				synchronized (this) {
					notify();
				}
			}
			transformCount++;
			break;
		}
	}

	// ------------------------------------------------------------

	/**
	 * Skaliere das Objekt. Muss &uuml;berschrieben werden, um Dimensionen bei
	 * Skalierung anzupassen.
	 * 
	 * @param sx
	 *            X-Skalierung
	 * @param sy
	 *            Y-Skalierung
	 */
	@Override
	public void scale(double sx, double sy) {
		super.scale(sx, sy);
		calculateDimensions();
	}

	/**
	 * Wird &uuml;berschrieben, um das zeichnen synchronisiert machen zu
	 * k&ouml;nnen
	 * 
	 * @param g
	 *            Graphics2D Kontext
	 * @param superTransform
	 *            Transformationsmatrix des &uuml;bergeordneten Containers
	 * @param alpha
	 *            Transparenzwert des &uuml;bergeordneten Containers
	 */
	@Override
	public synchronized void draw(Graphics2D g, AffineTransform superTransform, float alpha) {
		super.draw(g, superTransform, alpha);
	}
}
