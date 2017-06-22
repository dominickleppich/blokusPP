package blokusPP.test.bck;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import blokusPP.board.Board;
import blokusPP.graphic.GraphicConstants;
import blokusPP.graphic.GraphicDebug;
import blokusPP.preset.Move;
import blokusPP.preset.Position;
import blokusPP.preset.Requestable;
import blokusPP.preset.Setting;
import blokusPP.preset.Viewer;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.gfx.objects.GraphicContainer;
import eu.nepster.toolkit.gfx.objects.GraphicShape;
import eu.nepster.toolkit.gfx.objects.GraphicText;
import eu.nepster.toolkit.gfx.objects.image.GraphicStaticImage;
import eu.nepster.toolkit.gui.GameFrame;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.io.Outputable;
import eu.nepster.toolkit.settings.Settings;

/**
 * Fenster f&uuml;r das Spiel
 * 
 * @author Dominick Leppich
 *
 */
public class GameWindow extends GameFrame implements Setting, GraphicConstants, Requestable, Outputable {
	private static final long serialVersionUID = 1L;

	public static Font BORDER_FONT;
	// private static final Color BORDER_COLOR = new Color(199, 207, 155);
	

	enum MODE {
		SHOW, TEXT, VS, REQUEST;
	};

	enum REQUEST_MODE {
		PICK, ROTATE, FLIP, MOVE;
	};

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

	private Object gameWindowMonitor = new Object();
	private int gameWindowMonitorWaiting = 0;
	private Object requestMonitor = new Object();
	private int requestMonitorWaiting = 0;

	private GraphicContainer mainContainer;

	// Hintergrund
	private GraphicStaticImage background;

	// Spielbrett Anzeige
	private Viewer viewer;
	private GraphicContainer boardContainer;
	private GraphicStaticImage[][] boardFields;
	private Shape[][] boardFieldShapes;
	private double boardX, boardY, boardScale;

	// Text auf der GUI
	private GraphicContainer textContainer;
	private GraphicShape textBackground;
	private GraphicText graphicText;
	private Font graphicTextFont;
	private LinkedList<String> textsToShow;
	private int textTick = 0;
	private int textStart = 0;

	// Abdunkeln
	private GraphicShape blackOverlay;
	// Debug
	private GraphicDebug graphicDebug;

	private int mouseX = 0, mouseY = 0;
	private double oldWidth, oldHeight;

	// ------------------------------
	// MODI
	// ------------------------------
	private MODE mode;
	private REQUEST_MODE requestMode;

	// ------------------------------------------------------------

	/**
	 * Erzeuge neues Spielfenster
	 */
	public GameWindow() {
		super(1024, 768, "blokusPP", true, 0.2);
		oldWidth = getWidth();
		oldHeight = getHeight();
		textsToShow = new LinkedList<String>();
		init();
		reset();

		// Setze Fenster Icon
		try {
			setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
			IO.errorln("Icon not found @ GameWindow.<init>");
		}

		setVisible(true);
	}

	// ------------------------------------------------------------

	/**
	 * Erzeuge alle Grafikkomponenten
	 */
	private void init() {
		mode = MODE.SHOW;
		requestMode = REQUEST_MODE.PICK;

		mainContainer = new GraphicContainer();

		// ------------------------------
		// Hintergrund
		// ------------------------------
		BufferedImage backgroundImage = GraphicLoader.GFX.get("background.back")[0];
		background = new GraphicStaticImage();
		background.addImage("background", backgroundImage);
		background.scale((double) getWidth() / backgroundImage.getWidth(),
				(double) getHeight() / backgroundImage.getHeight());
		mainContainer.add(background);

		// ------------------------------
		// SPIELBRETT!!!
		// ------------------------------
		boardContainer = new GraphicContainer();
		boardFields = new GraphicStaticImage[Board.SIZE][Board.SIZE];
		boardFieldShapes = new Shape[Board.SIZE][Board.SIZE];
		// Hintergrund des Spielbretts
		BufferedImage boardBackgroundImage = GraphicLoader.GFX.get("background.board")[0];
		GraphicStaticImage boardBackground = new GraphicStaticImage(boardBackgroundImage);
		boardBackground.translate(BORDER_WIDTH, BORDER_HEIGHT);
		boardBackground.scale((double) Board.SIZE * FIELD_WIDTH / boardBackgroundImage.getWidth(),
				(double) Board.SIZE * FIELD_HEIGHT / boardBackgroundImage.getHeight());
		boardContainer.add(boardBackground);
		// Spielfelder
		for (int letter = 0; letter < Board.SIZE; letter++) {
			for (int number = 0; number < Board.SIZE; number++) {
				GraphicStaticImage ag = new GraphicStaticImage();
				ag.addImage("none", GraphicLoader.GFX.get("field.none"));
				ag.addImage("blue", GraphicLoader.GFX.get("field.blue"));
				ag.addImage("green", GraphicLoader.GFX.get("field.green"));
				ag.addImage("red", GraphicLoader.GFX.get("field.red"));
				ag.addImage("yellow", GraphicLoader.GFX.get("field.yellow"));
				ag.setImage("none");
				int xPos = BORDER_WIDTH + letter * FIELD_WIDTH, yPos = BORDER_HEIGHT + number * FIELD_HEIGHT;
				ag.translate(xPos, yPos);
				boardFields[letter][number] = ag;
				boardContainer.add(ag);
			}
		}
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
		boardContainer.add(borderTop);
		boardContainer.add(borderBottom);
		boardContainer.add(borderLeft);
		boardContainer.add(borderRight);
		// Beschriftung
		for (int letter = 0; letter < Board.SIZE; letter++) {
			GraphicText borderHorizontalTopText = new GraphicText("" + (char) ('A' + letter), BORDER_FONT, BORDER_COLOR,
					true);
			borderHorizontalTopText.translate(BORDER_WIDTH + FIELD_WIDTH / 2 + FIELD_WIDTH * letter,
					BORDER_HEIGHT * 3 / 4);
			boardContainer.add(borderHorizontalTopText);
			GraphicText borderHorizontalBotText = new GraphicText("" + (char) ('A' + letter), BORDER_FONT, BORDER_COLOR,
					true);
			borderHorizontalBotText.translate(BORDER_WIDTH + FIELD_WIDTH / 2 + FIELD_WIDTH * letter,
					BORDER_HEIGHT + FIELD_WIDTH * Board.SIZE + BORDER_HEIGHT / 4);
			boardContainer.add(borderHorizontalBotText);
		}
		// Vertical
		for (int number = 0; number < Board.SIZE; number++) {
			GraphicText borderVerticalLeftText = new GraphicText("" + (number + 1), BORDER_FONT, BORDER_COLOR, true);
			borderVerticalLeftText.translate(BORDER_WIDTH * 3 / 4,
					BORDER_HEIGHT + FIELD_HEIGHT / 2 + FIELD_HEIGHT * number);
			boardContainer.add(borderVerticalLeftText);
			GraphicText borderVerticalRightText = new GraphicText("" + (number + 1), BORDER_FONT, BORDER_COLOR, true);
			borderVerticalRightText.translate(BORDER_WIDTH / 4 + FIELD_WIDTH * Board.SIZE + BORDER_WIDTH,
					BORDER_HEIGHT + FIELD_HEIGHT / 2 + FIELD_HEIGHT * number);
			boardContainer.add(borderVerticalRightText);
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
		boardContainer.add(cornerTopLeft);
		boardContainer.add(cornerTopRight);
		boardContainer.add(cornerBottomLeft);
		boardContainer.add(cornerBottomRight);
		mainContainer.add(boardContainer);

		// ------------------------------
		// Text Area
		// ------------------------------
		textContainer = new GraphicContainer();
		graphicTextFont = new Font(Font.DIALOG, Font.BOLD, 100);
		graphicText = new GraphicText("", graphicTextFont, 5, null, null, Color.WHITE, Color.BLACK, true);
		textBackground = new GraphicShape(new Rectangle2D.Double(0, 0, getWidth(), getHeight() / 4), Color.BLACK, true);
		textBackground.setAlpha(0.6f);
		textContainer.add(textBackground);
		textContainer.add(graphicText);
		textContainer.setAlpha(0.0f);
		textContainer.setVisible(false);
		mainContainer.add(textContainer);

		// ------------------------------
		// Erzeuge Overlay
		// ------------------------------
		blackOverlay = new GraphicShape(new Rectangle2D.Double(0, 0, getWidth(), getHeight()), Color.BLACK, true);
		blackOverlay.setAlpha(0.7f);
		blackOverlay.setVisible(false);
		mainContainer.add(blackOverlay);

		// Passe Groesse der Komponenten an
		resizeGraphics();

		// Fuege grafische Debug-Ausgabe hinzu
		graphicDebug = new GraphicDebug();
		IO.register(graphicDebug, IO.LEVEL_DEBUG);
		if (!Settings.CFG.is("gui-debug"))
			graphicDebug.setVisible(false);

		// Setze angezeigtes Board zurueck
		reset();
	}

	/**
	 * Passe Gr&ouml;&szlig;e und Position der Grafikkomponenten an
	 */
	private synchronized void resizeGraphics() {
		if (mainContainer == null)
			return;

		double width = getWidth();
		double height = getHeight();

		// Passe Hintergrundbild an
		background.scale(width / oldWidth, height / oldHeight);

		// Zentriere Board
		if (width < height)
			boardScale = width * MAX_BOARD_WIDTH_HEIGHT_PERCENTAGE / BOARD_WIDTH;
		else
			boardScale = height * MAX_BOARD_WIDTH_HEIGHT_PERCENTAGE / BOARD_HEIGHT;
		boardX = (double) width / 2 - boardScale * BOARD_WIDTH / 2;
		boardY = (double) height / 2 - boardScale * BOARD_HEIGHT / 2;
		boardContainer.setToTranslation(boardX, boardY);
		boardX += boardScale * BORDER_WIDTH;
		boardY += boardScale * BORDER_HEIGHT;
		boardContainer.scale(boardScale, boardScale);
		// Shapes
		for (int letter = 0; letter < Board.SIZE; letter++) {
			for (int number = 0; number < Board.SIZE; number++) {
				boardFieldShapes[letter][number] = new Rectangle2D.Double(boardX + letter * boardScale * FIELD_WIDTH,
						boardY + number * boardScale * FIELD_HEIGHT, boardScale * FIELD_WIDTH,
						boardScale * FIELD_HEIGHT);
			}
		}

		// Text
		// graphicTextFont.deriveFont((float) (graphicTextFont.getSize() *
		// boardScale));
		// graphicText.setFont(graphicTextFont);
		graphicText.setToTranslation(width / 2, height / 4);
		graphicText.scale(boardScale, boardScale);
		textBackground.setShape(new Rectangle2D.Double(0, 0, width, height / 4));
		textBackground.setToTranslation(0, height / 8);

		// Passe Overlay an
		blackOverlay.setShape(new Rectangle2D.Double(0, 0, width, height));

		// Merke Groessenaenderung
		oldWidth = width;
		oldHeight = height;
	}

	// ------------------------------------------------------------

	/**
	 * Setze Grafik zur&uuml;ck
	 */
	public void reset() {
		for (int letter = 0; letter < Board.SIZE; letter++) {
			for (int number = 0; number < Board.SIZE; number++) {
				boardFields[letter][number].setImage("none");
			}
		}
		textsToShow.clear();
		textContainer.setVisible(false);
		textTick = 0;
		textStart = 0;
	}

	/**
	 * Aktualisiere die Grafikanzeige
	 */
	public void updateGui() {
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
	 * Setze Viewer
	 * 
	 * @param viewer
	 *            Viewer
	 */
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	// ------------------------------------------------------------

	/**
	 * Zeige Text an
	 * 
	 * @param s
	 *            Text
	 */
	@Override
	public void output(String s) {
		textsToShow.addLast(s);
		// TODO Was passiert wenn anderer Modus aktiv?
		mode = MODE.TEXT;
	}

	/**
	 * Warte bis GUI mit Anzeige fertig ist
	 */
	public void waitGameWindow() {
		try {
			IO.debugln("Waiting for gui @ GameWindow.waitGameWindow");
			synchronized (gameWindowMonitor) {
				gameWindowMonitorWaiting++;
				gameWindowMonitor.wait();
			}
			IO.debugln("Waiting for gui finished @ GameWindow.waitGameWindow");
		} catch (InterruptedException e) {
			IO.errorln("InterruptedException " + e + " @ GameWindow.waitGameWindow");
			e.printStackTrace();
		}
	}

	/**
	 * Wecke wartende Threads auf
	 */
	private void wakeUpGameWindow() {
		if (gameWindowMonitorWaiting > 0) {
			synchronized (gameWindowMonitor) {
				gameWindowMonitor.notify();
			}
			gameWindowMonitorWaiting--;
			IO.debugln("Waked up waiting threads @ GameWindow.wakeUpWaitingThreads");
		}
	}

	// ------------------------------------------------------------

	@Override
	public void tick() {
		mainContainer.tick();

		// TODO
		switch (mode) {
		case SHOW:
			wakeUpGameWindow();
			break;
		case TEXT:
			// Textanzeige
			if (textStart > 0) {
				textContainer.setAlpha(1.0f - 0.1f * textStart);
				textStart--;
			} else if (textTick <= 10 && textTick > 0) {
				textContainer.setAlpha(0.1f * textTick);
				textTick--;
			} else if (textTick > 0) {
				textTick--;
			} else if (!textsToShow.isEmpty()) {
				String text = textsToShow.getFirst();
				textsToShow.removeFirst();
				textStart = 10;
				textTick = 5 * text.length();
				graphicText.setText(text);
				textContainer.setVisible(true);
			} else {
				textContainer.setVisible(false);
				mode = MODE.SHOW;
			}
			break;
		case VS:

			break;
		case REQUEST:
			switch (requestMode) {
			case PICK:

				break;
			case ROTATE:

				break;
			case FLIP:

				break;
			case MOVE:

				break;
			}
			break;
		}

		if (isTextEntered("dark"))
			blackOverlay.setVisible(true);
		if (isTextEntered("white"))
			blackOverlay.setVisible(false);
		if (isTextEntered("move")) {
			resetText();
			moveDone();
		}
		if (isTextEntered("guidebug")) {
			resetText();
			Settings.CFG.set("gui-debug", !Settings.CFG.is("gui-debug"));
		}
		if (isTextEntered("debug")) {
			resetText();
			graphicDebug.setVisible(!graphicDebug.isVisible());
		}
	}

	/**
	 * Zeichne die Grafik
	 * 
	 * @param g
	 *            Graphics2D Kontext
	 */
	@Override
	public void draw(Graphics2D g) {
		mainContainer.draw(g);

		// Zeichne Hitboxen
		if (Settings.CFG.is("gui-debug")) {
			g.setTransform(new AffineTransform());
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g.setColor(Color.RED);
			for (int letter = 0; letter < Board.SIZE; letter++)
				for (int number = 0; number < Board.SIZE; number++)
					g.draw(boardFieldShapes[letter][number]);

			g.setColor(Color.BLUE);
			g.fillOval(mouseX - 3, mouseY - 3, 6, 6);
		}
		
		// Zeichne Debug Ausgabe
		graphicDebug.draw(g);
	}

	// ------------------------------------------------------------

	/**
	 * Maus wurde bewegt. Zeige Hover-Effekt
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	/**
	 * Maus hat geklickt.
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseClicked(MouseEvent e) {
		if (Settings.CFG.is("gui-debug")) {
			Position p = getMouseBoardPosition(e.getX(), e.getY());
			if (p != null)
				IO.debugln("Mouse clicked field " + p + " @ GameWindow.mouseClicked");
		}
	}

	/**
	 * Skaliere Fensterinhalt
	 * 
	 * @param width
	 *            neue Breite
	 * @param height
	 *            neue H&ouml;he
	 */
	public void setSize(int width, int height) {
		super.setSize(width, height);

		resizeGraphics();
	}

	// ------------------------------------------------------------

	/**
	 * Berechne das Feld an den Maus Koordinaten x und y. <code>null</code> wenn
	 * es kein solches Feld gibt
	 * 
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @return Position
	 */
	private Position getMouseBoardPosition(int x, int y) {
		for (int letter = 0; letter < Board.SIZE; letter++)
			for (int number = 0; number < Board.SIZE; number++)
				if (boardFieldShapes[letter][number].contains(x, y))
					return new Position(letter, number);
		return null;
	}

	// ------------------------------------------------------------

	/**
	 * Fordere einen Zug vom Spieler an
	 * 
	 * @return Spielzug
	 */
	@Override
	public Move deliver() throws Exception {
		// TODO Testen
		IO.debugln("Waiting for gui move @ GameWindow.deliver");
		synchronized (requestMonitor) {
			requestMonitorWaiting++;
			requestMonitor.wait();
		}
		IO.debugln("Gui made move @ GameWindow.deliver");
		return null;
	}

	/**
	 * Zug wurde von der Gui erstellt
	 */
	private void moveDone() {
		if (requestMonitorWaiting > 0) {
			synchronized (requestMonitor) {
				requestMonitor.notify();
			}
			requestMonitorWaiting--;
		}
	}
}
