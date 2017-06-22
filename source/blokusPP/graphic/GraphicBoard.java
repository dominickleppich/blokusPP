package blokusPP.graphic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import blokusPP.board.Board;
import blokusPP.board.Polyomino;
import blokusPP.preset.Position;
import blokusPP.preset.Setting;
import blokusPP.preset.Viewer;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.gfx.objects.GraphicContainer;
import eu.nepster.toolkit.gfx.objects.GraphicText;
import eu.nepster.toolkit.gfx.objects.image.GraphicStaticImage;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.settings.Settings;

/**
 * Grafische Repr&auml;sentation eines Spielbretts
 * 
 * @author Dominick Leppich
 *
 */
public class GraphicBoard extends GraphicContainer implements Setting, GraphicConstants {
	private static final long serialVersionUID = 1L;

	public static Font BORDER_FONT;

	// ------------------------------------------------------------

	// Spielbrett Anzeige
	private GraphicStaticImage[][] boardFields;
	private Shape[][] boardFieldShapes;

	// Highlight
	private GraphicContainer highlightPositionContainer;
	private GraphicStaticImage[][] highlightPositionFields;
	private GraphicContainer highlightPolyominoContainer;
	private GraphicStaticImage[][] highlightPolyominoFields;
	private int highlightCount = 0;
	private boolean highlightDimOn;

	private double width, height;

	// ------------------------------------------------------------

	/**
	 * Initialisiere statisches
	 */
	static {
		try {
			BORDER_FONT = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/Augusta.ttf")).deriveFont(70f);
		} catch (FontFormatException e) {
			IO.errorln("FontFormatException " + e + " @ GameWindow.static");
			e.printStackTrace();
		} catch (IOException e) {
			IO.errorln("IOException " + e + " @ GameWindow.static");
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------

	/**
	 * Erzeuge neues grafisches Board
	 */
	public GraphicBoard() {
		init();
	}

	// ------------------------------------------------------------

	/**
	 * Erzeuge alle Grafikkomponenten
	 */
	private void init() {
		// ------------------------------
		// SPIELBRETT!!!
		// ------------------------------
		highlightPositionContainer = new GraphicContainer();
		highlightPositionFields = new GraphicStaticImage[Board.SIZE][Board.SIZE];
		highlightPolyominoContainer = new GraphicContainer();
		highlightPolyominoFields = new GraphicStaticImage[Board.SIZE][Board.SIZE];
		boardFields = new GraphicStaticImage[Board.SIZE][Board.SIZE];
		boardFieldShapes = new Shape[Board.SIZE][Board.SIZE];
		// Hintergrund des Spielbretts
		BufferedImage boardBackgroundImage = GraphicLoader.GFX.get("background.board")[0];
		GraphicStaticImage boardBackground = new GraphicStaticImage(boardBackgroundImage);
		boardBackground.translate(BORDER_WIDTH, BORDER_HEIGHT);
		boardBackground.scale((double) Board.SIZE * FIELD_WIDTH / boardBackgroundImage.getWidth(),
				(double) Board.SIZE * FIELD_HEIGHT / boardBackgroundImage.getHeight());
		add(boardBackground);
		// Spielfelder
		for (int letter = 0; letter < Board.SIZE; letter++) {
			for (int number = 0; number < Board.SIZE; number++) {
				int xPos = BORDER_WIDTH + letter * FIELD_WIDTH, yPos = BORDER_HEIGHT + number * FIELD_HEIGHT;
				
				GraphicStaticImage hg = new GraphicStaticImage();
				hg.addImage("none", GraphicLoader.GFX.get("field.none"));
				hg.addImage("blue", GraphicLoader.GFX.get("field.blue"));
				hg.addImage("green", GraphicLoader.GFX.get("field.green"));
				hg.addImage("red", GraphicLoader.GFX.get("field.red"));
				hg.addImage("yellow", GraphicLoader.GFX.get("field.yellow"));
				hg.setImage("none");
				hg.translate(xPos, yPos);
				highlightPositionFields[letter][number] = hg;
				highlightPositionContainer.add(hg);

				GraphicStaticImage pg = new GraphicStaticImage();
				pg.addImage("none", GraphicLoader.GFX.get("field.none"));
				pg.addImage("blue", GraphicLoader.GFX.get("field.blue"));
				pg.addImage("green", GraphicLoader.GFX.get("field.green"));
				pg.addImage("red", GraphicLoader.GFX.get("field.red"));
				pg.addImage("yellow", GraphicLoader.GFX.get("field.yellow"));
				pg.setImage("none");
				pg.translate(xPos, yPos);
				highlightPolyominoFields[letter][number] = pg;
				highlightPolyominoContainer.add(pg);
				
				GraphicStaticImage ag = new GraphicStaticImage();
				ag.addImage("none", GraphicLoader.GFX.get("field.none"));
				ag.addImage("blue", GraphicLoader.GFX.get("field.blue"));
				ag.addImage("green", GraphicLoader.GFX.get("field.green"));
				ag.addImage("red", GraphicLoader.GFX.get("field.red"));
				ag.addImage("yellow", GraphicLoader.GFX.get("field.yellow"));
				ag.setImage("none");
				ag.translate(xPos, yPos);
				boardFields[letter][number] = ag;
				add(ag);
			}
		}
		add(highlightPositionContainer);
		add(highlightPolyominoContainer);
		highlightPositionContainer.setVisible(false);
		highlightPolyominoContainer.setVisible(false);

		// Fuege Rand hinzu
		// Horizontal
		GraphicStaticImage borderTop = new GraphicStaticImage(GraphicLoader.GFX.get("border.top"));
		GraphicStaticImage borderBottom = new GraphicStaticImage(GraphicLoader.GFX.get("border.bottom"));
		GraphicStaticImage borderLeft = new GraphicStaticImage(GraphicLoader.GFX.get("border.left"));
		GraphicStaticImage borderRight = new GraphicStaticImage(GraphicLoader.GFX.get("border.right"));
		borderTop.translate(BORDER_WIDTH, 0);
		borderBottom.translate(BORDER_WIDTH, BORDER_HEIGHT + Board.SIZE * FIELD_HEIGHT);
		borderLeft.translate(0, BORDER_HEIGHT);
		borderRight.translate(BORDER_WIDTH + Board.SIZE * FIELD_WIDTH, BORDER_HEIGHT);
		add(borderTop);
		add(borderBottom);
		add(borderLeft);
		add(borderRight);
		// Beschriftung
		for (int letter = 0; letter < Board.SIZE; letter++) {
			GraphicText borderHorizontalTopText = new GraphicText("" + (char) ('A' + letter), BORDER_FONT, BORDER_COLOR,
					true);
			borderHorizontalTopText.translate(BORDER_WIDTH + FIELD_WIDTH / 2 + FIELD_WIDTH * letter,
					BORDER_HEIGHT * 3 / 4);
			add(borderHorizontalTopText);
			GraphicText borderHorizontalBotText = new GraphicText("" + (char) ('A' + letter), BORDER_FONT, BORDER_COLOR,
					true);
			borderHorizontalBotText.translate(BORDER_WIDTH + FIELD_WIDTH / 2 + FIELD_WIDTH * letter,
					BORDER_HEIGHT + FIELD_WIDTH * Board.SIZE + BORDER_HEIGHT / 4);
			add(borderHorizontalBotText);
		}
		// Vertical
		for (int number = 0; number < Board.SIZE; number++) {
			GraphicText borderVerticalLeftText = new GraphicText("" + (number + 1), BORDER_FONT, BORDER_COLOR, true);
			borderVerticalLeftText.translate(BORDER_WIDTH * 3 / 4,
					BORDER_HEIGHT + FIELD_HEIGHT / 2 + FIELD_HEIGHT * number);
			add(borderVerticalLeftText);
			GraphicText borderVerticalRightText = new GraphicText("" + (number + 1), BORDER_FONT, BORDER_COLOR, true);
			borderVerticalRightText.translate(BORDER_WIDTH / 4 + FIELD_WIDTH * Board.SIZE + BORDER_WIDTH,
					BORDER_HEIGHT + FIELD_HEIGHT / 2 + FIELD_HEIGHT * number);
			add(borderVerticalRightText);
		}
		// Ecken
		GraphicStaticImage cornerTopLeft = new GraphicStaticImage(GraphicLoader.GFX.get("border.top_left"));
		GraphicStaticImage cornerTopRight = new GraphicStaticImage(GraphicLoader.GFX.get("border.top_right"));
		GraphicStaticImage cornerBottomLeft = new GraphicStaticImage(GraphicLoader.GFX.get("border.bottom_left"));
		GraphicStaticImage cornerBottomRight = new GraphicStaticImage(GraphicLoader.GFX.get("border.bottom_right"));
		// cornerTopLeft.translate(-BORDER_WIDTH / 2, -BORDER_HEIGHT / 2);
		cornerTopRight.translate(BORDER_WIDTH + FIELD_WIDTH * Board.SIZE, 0);
		cornerBottomLeft.translate(0, BORDER_HEIGHT + FIELD_HEIGHT * Board.SIZE);
		cornerBottomRight.translate(BORDER_WIDTH + FIELD_WIDTH * Board.SIZE, BORDER_HEIGHT + FIELD_HEIGHT * Board.SIZE);
		add(cornerTopLeft);
		add(cornerTopRight);
		add(cornerBottomLeft);
		add(cornerBottomRight);

		// Setze angezeigtes Board zurueck
		reset();

		calculateDimensions();
		calculateHitBoxes();
	}

	/**
	 * Setze Board zur&uuml;ck
	 */
	public void reset() {
		for (int letter = 0; letter < Board.SIZE; letter++)
			for (int number = 0; number < Board.SIZE; number++)
				boardFields[letter][number].setImage("none");
	}

	/**
	 * Update das Board
	 * 
	 * @param viewer
	 *            Viewer
	 */
	public void updateBoard(Viewer viewer) {
		// Wenn es keinen Viewer gibt, mache nichts
		if (viewer == null)
			return;

		for (int letter = 0; letter < Board.SIZE; letter++) {
			for (int number = 0; number < Board.SIZE; number++) {
				switch (viewer.getColor(letter, number)) {
				case BLUE:
					boardFields[letter][number].setImage("blue");
					break;
				case GREEN:
					boardFields[letter][number].setImage("green");
					break;
				case RED:
					boardFields[letter][number].setImage("red");
					break;
				case YELLOW:
					boardFields[letter][number].setImage("yellow");
					break;
				default:
					boardFields[letter][number].setImage("none");
				}
			}
		}
	}

	/**
	 * Berechne Dimensionen
	 */
	private void calculateDimensions() {
		width = (20 * FIELD_WIDTH + 2 * BORDER_WIDTH) * getScaleX();
		height = (20 * FIELD_HEIGHT + 2 * BORDER_HEIGHT) * getScaleY();
	}

	/**
	 * Berechne Shapes f&uuml;r Hitboxen neu
	 */
	private void calculateHitBoxes() {
		// Shapes
		for (int letter = 0; letter < Board.SIZE; letter++)
			for (int number = 0; number < Board.SIZE; number++)
				boardFieldShapes[letter][number] = new Rectangle2D.Double(
						getTranslateX() + (letter * FIELD_WIDTH + BORDER_WIDTH) * getScaleX(),
						getTranslateY() + (number * FIELD_HEIGHT + BORDER_HEIGHT) * getScaleY(),
						getScaleX() * FIELD_WIDTH, getScaleY() * FIELD_HEIGHT);
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
	 * @param sx
	 *            X-Skalierung
	 * @param sy
	 *            Y-Skalierung
	 */
	@Override
	public void scale(double sx, double sy) {
		super.scale(sx, sy);
		calculateDimensions();
		calculateHitBoxes();
	}

	/**
	 * Setze Translation
	 * 
	 * @param tx
	 *            X-Verschiebung
	 * @param ty
	 *            Y-Verschiebung
	 */
	public void setToTranslation(double tx, double ty) {
		super.setToTranslation(tx, ty);
		calculateHitBoxes();
	}

	/**
	 * Berechne das Feld an den Maus Koordinaten x und y.<code>null</code>wenn*
	 * es kein solches Feld gibt**
	 * 
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @return Position
	 */
	public Position getMouseBoardPosition(int x, int y) {
		for (int letter = 0; letter < Board.SIZE; letter++)
			for (int number = 0; number < Board.SIZE; number++)
				if (boardFieldShapes[letter][number].contains(x, y))
					return new Position(letter, number);
		return null;
	}

	// ------------------------------------------------------------

	/**
	 * Markiere Positionen
	 * 
	 * @param positions
	 *            Positionen
	 * @param color
	 *            Farbe
	 */
	public void highlightPositions(ArrayList<Position> positions, int color) {
		String colorString = "";
		switch (color) {
		case BLUE:
			colorString = "blue";
			break;
		case YELLOW:
			colorString = "yellow";
			break;
		case RED:
			colorString = "red";
			break;
		case GREEN:
			colorString = "green";
			break;
		}

		// Clear
		for (int letter = 0; letter < Board.SIZE; letter++)
			for (int number = 0; number < Board.SIZE; number++)
				highlightPositionFields[letter][number].setImage("none");

		if (colorString == "" || positions == null)
			return;

		// Setze Positionen
		for (Position p : positions)
			highlightPositionFields[p.getLetter()][p.getNumber()].setImage(colorString);
	}

	/**
	 * Markiere Polyomino
	 * 
	 * @param polyomino
	 *            Polyomino
	 * @param color
	 *            Farbe
	 * @param alpha
	 *            Transparenz
	 */
	public void highlightPolyomino(Polyomino polyomino, int color, float alpha) {
		String colorString = "";
		switch (color) {
		case BLUE:
			colorString = "blue";
			break;
		case YELLOW:
			colorString = "yellow";
			break;
		case RED:
			colorString = "red";
			break;
		case GREEN:
			colorString = "green";
			break;
		}

		// Clear
		for (int letter = 0; letter < Board.SIZE; letter++)
			for (int number = 0; number < Board.SIZE; number++)
				highlightPolyominoFields[letter][number].setImage("none");

		if (colorString == "" || polyomino == null)
			return;
		
		highlightPolyominoContainer.setAlpha(alpha);

		// Setze Positionen
		for (Position p : polyomino.getPolyomino())
			highlightPolyominoFields[p.getLetter()][p.getNumber()].setImage(colorString);
	}

	/**
	 * Schalte Highlighting ein und aus
	 * 
	 * @param value
	 *            Wert
	 */
	public void setHighlighting(boolean value) {
		highlightPositionContainer.setVisible(value);
		highlightPolyominoContainer.setVisible(value);
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
	public void draw(Graphics2D g, AffineTransform superTransform, float alpha) {
		super.draw(g, superTransform, alpha);

		if (Settings.CFG.is("gui-debug")) {
			g.setTransform(new AffineTransform());
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g.setColor(Color.RED);
			for (int letter = 0; letter < Board.SIZE; letter++)
				for (int number = 0; number < Board.SIZE; number++)
					g.draw(boardFieldShapes[letter][number]);
		}
	}

	/**
	 * Aktualisiere Animationen
	 */
	@Override
	public void tick() {
		// TODO Highlight fuer gueltige Startpositionen
		// Hightlight pochen
		if (highlightCount > 0 && highlightCount < HIGHLIGHT_STEP) {
			highlightPositionContainer.setAlpha(1.0f / HIGHLIGHT_STEP * highlightCount * MAX_POLYOMINO_START_POSITION_ALPHA);
			if (highlightDimOn)
				highlightCount++;
			else
				highlightCount--;
		} else if (highlightCount == HIGHLIGHT_STEP) {
			highlightPositionContainer.setAlpha(1.0f * MAX_POLYOMINO_START_POSITION_ALPHA);
			highlightDimOn = false;
			highlightCount--;
		} else {
			highlightPositionContainer.setAlpha(0.0f);
			highlightDimOn = true;
			highlightCount++;
		}

		// TODO showMove mit Alpha

	}
}
