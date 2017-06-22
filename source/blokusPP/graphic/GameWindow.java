package blokusPP.graphic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import blokusPP.board.Board;
import blokusPP.board.Polyomino;
import blokusPP.player.PlayerObject;
import blokusPP.preset.Move;
import blokusPP.preset.Position;
import blokusPP.preset.Requestable;
import blokusPP.preset.Setting;
import blokusPP.preset.Viewer;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.gfx.GraphicTools;
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

	private static final int ACTION_OKAY = 0;
	private static final int ACTION_BACK = 1;
	private static final int ACTION_RIGHT = 2;
	private static final int ACTION_LEFT = 3;
	private static final int ACTION_UP = 4;
	private static final int ACTION_DOWN = 5;

	public enum MODE {
		SHOW, VS, HIGHSCORE, REQUEST;
	};

	public enum REQUEST_MODE {
		PICK, ROTATE, FLIP, MOVE;
	};

	// ------------------------------------------------------------

	// Monitore fuer Synchronisation
	private Object gameWindowMonitor = new Object();
	private Object textMonitor = new Object();
	private int gameWindowMonitorWaiting = 0;
	private Object requestMonitor = new Object();

	// Spielbrett Anzeige
	private Viewer viewer;

	// Container, wo alles drin ist
	private GraphicContainer mainContainer;

	// Hintergrund
	private GraphicStaticImage background;

	// Spielbrett
	private GraphicBoard board;

	// PolyominoBar
	private GraphicPolyominoBar polyominoBar;

	// Graphic Polyomino
	private GraphicPolyominoRotationFlipper graphicPolyomino;

	// Text auf der GUI
	private GraphicContainer textContainer;
	private GraphicShape textBackground;
	private GraphicText graphicText;
	private Font graphicTextFont;
	private LinkedList<String> textsToShow;
	private int textTick = 0;
	private int textStart = 0;

	private int playerInfoTick = 0;
	private double playerScale;
	private GraphicContainer playerInfoContainer;
	private HashMap<PlayerObject, Integer> playerMap;
	private ArrayList<GraphicText> player;
	private ArrayList<GraphicText> playerTime;
	private GraphicPlayerTime moveTime;
	private Color playerInfoOldColor;
	private Color playerInfoNewColor;
	private int playerInfoOldPlayer;
	private int playerInfoNewPlayer;
	private boolean playerInfoFirstPlayer;
	private boolean playerInfoTime;

	// Abdunkeln
	private GraphicContainer vsContainer;
	private GraphicShape blackOverlay;
	// Debug
	private GraphicDebug graphicDebug;

	// ------------------------------
	// MODI
	// ------------------------------
	// VS
	private Color player1Color1 = null;
	private Color player1Color2 = null;
	private Color player2Color1 = null;
	private Color player2Color2 = null;
	private GraphicText playerOneVS;
	private GraphicText playerTwoVS;
	private float playerOneMove;
	private float playerTwoMove;
	private GraphicText vsText;
	private int vsTick = 0;

	// TODO auf private setzen
	private MODE mode;
	private REQUEST_MODE requestMode;

	// Maus + Fenster
	private int mouseX = 0, mouseY = 0;
	private Position mousePosition;
	private double oldWidth, oldHeight;

	// Spielzug
	private Polyomino polyomino, movedPolyomino;

	// ------------------------------------------------------------

	/**
	 * Erzeuge neues Spielfenster
	 */
	public GameWindow() {
		super(1024, 768, "blokusPP", true, 1.0);
		oldWidth = getWidth();
		oldHeight = getHeight();
		textsToShow = new LinkedList<String>();
		playerMap = new HashMap<PlayerObject, Integer>();

		Dimension screen = GraphicTools.getScreenDimension();
		setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);

		init();
		reset();

		// Setze Fenster Icon
		try {
			setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
			IO.errorln("Icon not found @ GameWindow.<init>");
		}
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
		// Board
		// ------------------------------
		board = new GraphicBoard();
		mainContainer.add(board);

		// ------------------------------
		// Player Info
		// ------------------------------
		playerInfoContainer = new GraphicContainer();
		playerInfoContainer.setVisible(false);
		Font f = null;
		try {
			f = Font.createFont(Font.TRUETYPE_FONT, new File(PLAYER_NAME_FONT)).deriveFont(180f);
		} catch (FontFormatException e) {
			IO.errorln("Error loading font @ GameWindow.init");
		} catch (IOException e) {
			IO.errorln("Font not found @ GameWindow.init");
		}
		player = new ArrayList<GraphicText>();
		playerTime = new ArrayList<GraphicText>();
		for (int i = 0; i < GAME_COLORS; i++) {
			GraphicText playerText = new GraphicText("Player " + i, f, PLAYER_NAME_NONE, true);
			player.add(playerText);
			playerInfoContainer.add(playerText);
			GraphicText playerTimeText = new GraphicText("0:00", f, PLAYER_TIME_OKAY, true);
			playerTime.add(playerTimeText);
			playerInfoContainer.add(playerTimeText);
		}
		moveTime = new GraphicPlayerTime(750, 150);
		playerInfoContainer.add(moveTime);
		mainContainer.add(playerInfoContainer);

		// ------------------------------
		// Polyomino Bar
		// ------------------------------
		polyominoBar = new GraphicPolyominoBar(2);
		polyominoBar.setVisible(false);
		mainContainer.add(polyominoBar);

		// ------------------------------
		// Graphic Polyomino
		// ------------------------------
		graphicPolyomino = new GraphicPolyominoRotationFlipper();
		graphicPolyomino.setVisible(false);
		mainContainer.add(graphicPolyomino);

		// ------------------------------
		// Text Area
		// ------------------------------
		textContainer = new GraphicContainer();
		graphicTextFont = new Font(Font.DIALOG, Font.BOLD, 100);
		graphicText = new GraphicText("", graphicTextFont, 5, Color.WHITE, Color.BLACK, true);
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
		vsContainer = new GraphicContainer();
		blackOverlay = new GraphicShape(new Rectangle2D.Double(0, 0, getWidth(), getHeight()), Color.BLACK, true);
		blackOverlay.setAlpha(VS_OVERLAY_ALPHA);
		vsContainer.add(blackOverlay);

		// VS Screen
		playerOneVS = new GraphicText("", null, Color.WHITE, true);
		playerTwoVS = new GraphicText("", null, Color.WHITE, true);
		Font fVS = null, fPl = null;
		try {
			fVS = Font.createFont(Font.TRUETYPE_FONT, new File(VS_FONT)).deriveFont(120f);
			fPl = Font.createFont(Font.TRUETYPE_FONT, new File(PLAYER_NAME_FONT)).deriveFont(150f);
		} catch (FontFormatException e) {
			IO.errorln("Error loading font @ GameWindow.init");
		} catch (IOException e) {
			IO.errorln("Font not found @ GameWindow.init");
		}
		vsText = new GraphicText("VS", fVS, new GradientPaint(0, 0, VS_COLOR_1, 100, 50, VS_COLOR_2), true);
		playerOneVS.setFont(fPl);
		playerTwoVS.setFont(fPl);
		playerOneVS.setVisible(false);
		playerTwoVS.setVisible(false);
		vsText.setToTranslation(1024 / 2, 768 / 2);
		vsText.setVisible(false);
		vsContainer.add(vsText);
		vsContainer.add(playerOneVS);
		vsContainer.add(playerTwoVS);
		vsContainer.setVisible(false);
		mainContainer.add(vsContainer);

		// Passe Groesse der Komponenten an
		updateGraphics();

		// Fuege grafische Debug-Ausgabe hinzu
		graphicDebug = new GraphicDebug();
		IO.register(graphicDebug, IO.LEVEL_DEBUG);
		if (!Settings.CFG.is("gui-debug"))
			graphicDebug.setVisible(false);

		// Setze angezeigtes Board zurueck
		reset();
		setPlayerInformation(false);
	}

	/**
	 * Passe Gr&ouml;&szlig;e und Position der Grafikkomponenten an
	 */
	private synchronized void updateGraphics() {
		if (mainContainer == null)
			return;

		double width = getWidth();
		double height = getHeight();

		double boardScale, boardX, boardY;
		if (width < height)
			boardScale = width * MAX_BOARD_WIDTH_HEIGHT_PERCENTAGE / BOARD_WIDTH;
		else
			boardScale = height * MAX_BOARD_WIDTH_HEIGHT_PERCENTAGE / BOARD_HEIGHT;

		// Passe Hintergrundbild an
		background.scale(width / oldWidth, height / oldHeight);

		// Zentriere Board
		if (!playerInfoContainer.isVisible())
			boardX = width / 2 - boardScale * BOARD_WIDTH / 2;
		else
			boardX = Math.min(width / 2 - boardScale * BOARD_WIDTH / 2, height / 2 - boardScale * BOARD_HEIGHT / 2);
		boardY = height / 2 - boardScale * BOARD_HEIGHT / 2;
		board.setToIdentity();
		board.scale(boardScale, boardScale);
		board.setToTranslation(boardX, boardY);
		board.scale(boardScale, boardScale);

		// Player Information
		playerScale = boardScale;
		if (playerInfoTime) {
			int moveTimePos = (playerMap.size() / 2) * 2 + 1;
			int i = 0;
			int pos = 1;
			while (pos < playerMap.size() * 2 + 2) {
				if (pos == moveTimePos) {
					moveTime.setToTranslation((width + BOARD_WIDTH * playerScale) / 2,
							height * (pos++) / (playerMap.size() * 2 + 2));
					moveTime.scale(playerScale, playerScale);
				} else {
					player.get(i).setToTranslation((width + BOARD_WIDTH * playerScale) / 2,
							height * (pos++) / (playerMap.size() * 2 + 2));
					player.get(i).scale(playerScale, playerScale);
					playerTime.get(i).setToTranslation((width + BOARD_WIDTH * playerScale) / 2,
							height * (pos++) / (playerMap.size() * 2 + 2));
					playerTime.get(i).scale(playerScale, playerScale);
					i++;
				}
			}
		} else {
			int pos = 1;
			for (int i = 0; i < playerMap.size(); i++) {
				player.get(i).setToTranslation((width + BOARD_WIDTH * playerScale) / 2,
						height * (pos++) / (playerMap.size() + 1));
				player.get(i).scale(playerScale, playerScale);
			}
		}

		// Polyomino Bar
		polyominoBar.setToTranslation(0, height / 2);
		polyominoBar.scale(width, height / 4);

		// Graphic Polyomino
		double graphicPolyominoScale;
		if (width < height)
			graphicPolyominoScale = width * MAX_BOARD_WIDTH_HEIGHT_PERCENTAGE / GRAPHIC_POLYOMINO_WIDTH
					* GRAPHIC_POLYOMINO_PERCENTAGE;
		else
			graphicPolyominoScale = height * MAX_BOARD_WIDTH_HEIGHT_PERCENTAGE / GRAPHIC_POLYOMINO_HEIGHT
					* GRAPHIC_POLYOMINO_PERCENTAGE;
		graphicPolyomino.setToTranslation(boardX + boardScale * BOARD_WIDTH / 2,
				boardY + boardScale * BOARD_HEIGHT / 2);
		graphicPolyomino.scale(graphicPolyominoScale, graphicPolyominoScale);

		// Text
		graphicText.setToTranslation(width / 2, height / 4);
		graphicText.scale(boardScale, boardScale);
		textBackground.setShape(new Rectangle2D.Double(0, 0, width, height / 4));
		textBackground.setToTranslation(0, height / 8);

		// Skaliere VS
		vsContainer.scale(width / oldWidth, height / oldHeight);

		// Merke Groessenaenderung
		oldWidth = width;
		oldHeight = height;
	}

	// ------------------------------------------------------------

	/**
	 * Setze Grafik zur&uuml;ck
	 */
	public void reset() {
		board.reset();

		mode = MODE.SHOW;
		textsToShow.clear();
		textContainer.setVisible(false);
		textTick = 0;
		textStart = 0;
		playerInfoTick = 0;
		playerInfoFirstPlayer = true;
		for (GraphicText gt : player)
			gt.setTextPaint(PLAYER_NAME_NONE);
		for (GraphicText gt : playerTime)
			gt.setTextPaint(PLAYER_NAME_NONE);
		updateGraphics();
	}

	/**
	 * Beende Input Modus
	 */
	public void disableInput() {
		mode = MODE.SHOW;
	}

	/**
	 * Aktualisiere die Grafikanzeige
	 */
	public void updateGui() {
		// Wenn es keinen Viewer gibt, mache nichts
		if (viewer == null)
			return;

		board.updateBoard(viewer);
		IO.debugln("Gui updated @ GameWindow.updateGui");
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

	/**
	 * Zeige einen VS Screen mit zwei Spielern
	 * 
	 * @param player1
	 *            Name Spieler 1
	 * @param player2
	 *            Name Spieler 2
	 */
	public void vs(String player1, int player1Color1, int player1Color2, String player2, int player2Color1,
			int player2Color2) {
		playerOneVS.setText(player1);
		playerTwoVS.setText(player2);
		playerOneVS.setVisible(true);
		playerTwoVS.setVisible(true);
		playerOneVS.setToTranslation(-600, 768 / 4);
		playerTwoVS.setToTranslation(1024 + 600, 768 * 3 / 4);

		this.player1Color1 = getPlayerColor(player1Color1);
		this.player1Color2 = getPlayerColor(player1Color2);
		this.player2Color1 = getPlayerColor(player2Color1);
		this.player2Color2 = getPlayerColor(player2Color2);

		playerOneVS.setTextPaint(this.player1Color1);
		playerTwoVS.setTextPaint(this.player2Color1);
		vsText.setVisible(false);
		vsTick = 0;
		mode = MODE.VS;
	}

	/**
	 * Schalte Spielerinformationen ein und aus
	 * 
	 * @param value
	 *            Neuer Wert
	 */
	public void setPlayerInformation(boolean value) {
		playerInfoContainer.setVisible(value);
	}

	/**
	 * Schalte Spielerzeitinformationen ein und aus
	 * 
	 * @param value
	 *            Neuer Wert
	 */
	public void setPlayerTimeInformation(boolean value) {
		for (GraphicText gt : playerTime)
			gt.setVisible(value);
		moveTime.setVisible(value);
		playerInfoTime = value;
	}

	/**
	 * Setze Restspielerzeit
	 * 
	 * @param player
	 *            Spieler
	 * @param time
	 *            Zeit in Sekunden
	 */
	public void setPlayerTime(PlayerObject player, long time, boolean critical) {
		String timeString = "" + (time / 60) + ":" + (time % 60 < 10 ? "0" + time % 60 : time % 60);
		GraphicText textObject = null;
		if (playerMap.containsKey(player))
			textObject = this.playerTime.get(playerMap.get(player));

		if (textObject != null) {
			textObject.setText(timeString);
			if (critical)
				textObject.setTextPaint(PLAYER_TIME_CRITICAL);
			else
				textObject.setTextPaint(PLAYER_TIME_OKAY);
		}
	}

	/**
	 * Setze Restzugzeit
	 * 
	 * @param progress
	 *            Fortschritt
	 */
	public void setMoveTime(double progress) {
		moveTime.setProgress(progress);
	}

	/**
	 * Setze Farbe des Spielernamens
	 * 
	 * @param player
	 *            Spieler
	 * @param color
	 *            Farbe
	 */
	public void setPlayerColor(PlayerObject player, int color) {
		if (!playerMap.containsKey(player))
			return;
		playerInfoOldPlayer = playerInfoNewPlayer;
		playerInfoNewPlayer = playerMap.get(player);
		playerInfoOldColor = playerInfoNewColor;
		playerInfoNewColor = getPlayerColor(color);
		playerInfoTick = PLAYER_CHANGE_STEPS;
	}

	/**
	 * &Uuml;bergib der GUI alle spielenden Spieler f&uuml;r die Information
	 * 
	 * @param players
	 *            ArrayListe der Spieler
	 */
	public void setPlayers(ArrayList<PlayerObject> players) {
		if (players.isEmpty() || players.size() > GAME_COLORS)
			throw new RuntimeException("Invalid size of players: " + players.size());

		playerMap.clear();
		for (int i = 0; i < players.size(); i++)
			playerMap.put(players.get(i), i);
		for (int i = 0; i < GAME_COLORS; i++) {
			if (i < players.size()) {
				player.get(i).setText(players.get(i).getName());
				player.get(i).setVisible(true);
				if (playerInfoTime)
					playerTime.get(i).setVisible(true);
				else
					playerTime.get(i).setVisible(false);
			} else {
				player.get(i).setVisible(false);
				playerTime.get(i).setVisible(false);
			}
		}
		updateGraphics();
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
		synchronized (textMonitor) {
			textsToShow.addLast(s);
		}
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

	/**
	 * Gib die Spielerfarbe zur&uuml;ck
	 * 
	 * @param color
	 *            Spieler
	 * @return Farbe
	 */
	private Color getPlayerColor(int color) {
		Color c = null;
		switch (color) {
		case -1:
			c = PLAYER_NAME_NONE;
			break;
		case BLUE:
			c = PLAYER_NAME_BLUE;
			break;
		case YELLOW:
			c = PLAYER_NAME_YELLOW;
			break;
		case RED:
			c = PLAYER_NAME_RED;
			break;
		case GREEN:
			c = PLAYER_NAME_GREEN;
			break;
		}
		return c;
	}

	@Override
	public void tick() {
		mainContainer.tick();

		// Textanzeige
		if (textStart > 0) {
			textContainer.setAlpha(1.0f - 0.1f * textStart);
			textStart--;
		} else if (textTick <= 10 && textTick > 0) {
			textContainer.setAlpha(0.1f * textTick);
			textTick--;
		} else if (textTick > 0) {
			textTick--;
		} else {
			synchronized (textMonitor) {
				if (!textsToShow.isEmpty()) {
					String text = textsToShow.getFirst();
					textsToShow.removeFirst();
					textStart = 10;
					textTick = 5 * text.length();
					graphicText.setText(text);
					textContainer.setVisible(true);
				} else {
					textContainer.setVisible(false);
					// Nur aufwecken, wenn nicht im VS oder HIGHSCORE Modus
					if (mode == MODE.SHOW)
						wakeUpGameWindow();
				}
			}
		}

		// Spielerfarbenwechsel
		if (playerInfoTick > 0) {
			// Bestimme Uebergangsprozent
			float alpha = 1.0f - (float) playerInfoTick / PLAYER_CHANGE_STEPS;

			// Pruefe was gemacht werden muss
			boolean playerOldScale = false;
			boolean playerNewScale = false;
			boolean playerOldScaleDirection = false;
			boolean playerNewScaleDirection = false;
			boolean playerOldColorChange = false;
			boolean playerNewColorChange = false;
			Color playerOld1 = null, playerOld2 = null;
			Color playerNew1 = null, playerNew2 = null;

			int moveTimePos = (playerMap.size() / 2) * 2 + 1;
			int playerOldPos = 1, playerNewPos = 1;
			GraphicText playerOld = player.get(playerInfoOldPlayer);
			GraphicText playerNew = player.get(playerInfoNewPlayer);
			// TODO
			for (int i = 0; i < playerInfoOldPlayer; i++) {
				playerOldPos++;
				if (playerInfoTime)
					playerOldPos++;
				if (playerInfoTime && playerOldPos == moveTimePos)
					playerOldPos++;
			}
			for (int i = 0; i < playerInfoNewPlayer; i++) {
				playerNewPos++;
				if (playerInfoTime)
					playerNewPos++;
				if (playerInfoTime && playerNewPos == moveTimePos)
					playerNewPos++;
			}

			// Wird zwischen Spielern gewechselt
			if (!playerInfoFirstPlayer && playerInfoOldPlayer != playerInfoNewPlayer) {
				playerOldScale = true;
				playerNewScale = true;
				playerNewScaleDirection = true;
				playerOldColorChange = true;
				playerNewColorChange = true;
				playerOld1 = playerInfoOldColor;
				playerOld2 = PLAYER_NAME_NONE;
				playerNew1 = PLAYER_NAME_NONE;
				playerNew2 = playerInfoNewColor;
				// oder bleibt Spieler gleich
			} else {
				playerNewColorChange = true;
				playerNew1 = playerInfoOldColor;
				playerNew2 = playerInfoNewColor;
				if (playerInfoFirstPlayer) {
					playerNew1 = PLAYER_NAME_NONE;
					playerNewScale = true;
					playerNewScaleDirection = true;
				}
			}

			// MACHE ES!!
			if (playerOldScale) {
				playerOld.setToTranslation((getWidth() + BOARD_WIDTH * playerScale) / 2, getHeight() * playerOldPos
						/ (playerInfoTime ? playerMap.size() * 2 + 2 : playerMap.size() + 1));
				if (playerOldScaleDirection)
					playerOld.scale(playerScale + PLAYER_ACTIVE_SCALE * alpha,
							playerScale + PLAYER_ACTIVE_SCALE * alpha);
				else
					playerOld.scale(playerScale + PLAYER_ACTIVE_SCALE * (1.0f - alpha),
							playerScale + PLAYER_ACTIVE_SCALE * (1.0f - alpha));
			}
			if (playerNewScale) {
				playerNew.setToTranslation((getWidth() + BOARD_WIDTH * playerScale) / 2, getHeight() * playerNewPos
						/ (playerInfoTime ? playerMap.size() * 2 + 2 : playerMap.size() + 1));
				if (playerNewScaleDirection)
					playerNew.scale(playerScale + PLAYER_ACTIVE_SCALE * alpha,
							playerScale + PLAYER_ACTIVE_SCALE * alpha);
				else
					playerNew.scale(playerScale + PLAYER_ACTIVE_SCALE * (1.0f - alpha),
							playerScale + PLAYER_ACTIVE_SCALE * (1.0f - alpha));
			}
			if (playerOldColorChange && playerOld1 != null && playerOld2 != null) {
				playerOld.setTextPaint(
						new Color((int) ((1.0f - alpha) * playerOld1.getRed() + alpha * playerOld2.getRed()),
								(int) ((1.0f - alpha) * playerOld1.getGreen() + alpha * playerOld2.getGreen()),
								(int) ((1.0f - alpha) * playerOld1.getBlue() + alpha * playerOld2.getBlue())));
			}
			if (playerNewColorChange && playerNew1 != null && playerNew2 != null) {
				playerNew.setTextPaint(
						new Color((int) ((1.0f - alpha) * playerNew1.getRed() + alpha * playerNew2.getRed()),
								(int) ((1.0f - alpha) * playerNew1.getGreen() + alpha * playerNew2.getGreen()),
								(int) ((1.0f - alpha) * playerNew1.getBlue() + alpha * playerNew2.getBlue())));
			}
			playerInfoTick--;
			if (playerInfoTick == 0)
				playerInfoFirstPlayer = false;
		}

		switch (mode) {
		case SHOW:
			polyominoBar.setVisible(false);
			graphicPolyomino.setVisible(false);
			board.setHighlighting(false);
			break;
		case VS:
			// Ende
			if (vsTick > 500) {
				vsTick = 0;
				vsContainer.setVisible(false);
				mode = MODE.SHOW;
			}
			// Aufhellen
			else if (vsTick > 450) {
				vsContainer.setAlpha(0.7f * (1.0f - 0.02f * (vsTick - 450)));
				vsContainer.setVisible(true);
				playerOneVS.translate(playerOneMove, 0);
				playerTwoVS.translate(playerTwoMove, 0);
				playerOneMove *= 1.2f;
				playerTwoMove *= 1.2f;
			}
			// Text wegfliegen
			else if (vsTick > 400) {
				playerOneVS.translate(playerOneMove, 0);
				playerTwoVS.translate(playerTwoMove, 0);
				playerOneMove *= 1.1f;
				playerTwoMove *= 1.1f;
			}
			// Namen einfliegen
			else if (vsTick > 50) {
				if (vsTick >= 250 && vsTick <= 350) {
					float alpha = (vsTick - 250) * 0.01f;
					playerOneVS.setTextPaint(
							new Color((int) ((1.0f - alpha) * player1Color1.getRed() + alpha * player1Color2.getRed()),
									(int) ((1.0f - alpha) * player1Color1.getGreen()
											+ alpha * player1Color2.getGreen()),
							(int) ((1.0f - alpha) * player1Color1.getBlue() + alpha * player1Color2.getBlue())));
					playerTwoVS.setTextPaint(
							new Color((int) ((1.0f - alpha) * player2Color1.getRed() + alpha * player2Color2.getRed()),
									(int) ((1.0f - alpha) * player2Color1.getGreen()
											+ alpha * player2Color2.getGreen()),
							(int) ((1.0f - alpha) * player2Color1.getBlue() + alpha * player2Color2.getBlue())));
					playerOneVS.translate(playerOneMove, 0);
					playerTwoVS.translate(playerTwoMove, 0);
					// playerOneMove *= 0.9f;
					// playerTwoMove *= 0.9f;
				} else if (vsTick > 150) {
					playerOneVS.translate(playerOneMove, 0);
					playerTwoVS.translate(playerTwoMove, 0);
					playerOneMove *= 0.97f;
					playerTwoMove *= 0.97f;
				}
				// VS einblenden
				else if (vsTick > 100) {
					vsText.setAlpha(0.02f * (vsTick - 100));
					vsText.setVisible(true);
					playerOneMove = 30f;
					playerTwoMove = -30f;
				}
			}
			// Abdimmen
			else {
				vsContainer.setAlpha(0.02f * vsTick);
				vsContainer.setVisible(true);
			}
			vsTick++;
			break;
		case HIGHSCORE:

			break;
		case REQUEST:
			switch (requestMode) {
			case PICK:
				polyominoBar.setVisible(true);
				graphicPolyomino.setVisible(false);
				board.setHighlighting(false);
				break;
			case ROTATE:
				polyominoBar.setVisible(false);
				graphicPolyomino.setVisible(true);
				board.setHighlighting(false);
				break;
			case FLIP:
				polyominoBar.setVisible(false);
				graphicPolyomino.setVisible(true);
				board.setHighlighting(false);
				break;
			case MOVE:
				polyominoBar.setVisible(false);
				graphicPolyomino.setVisible(false);
				board.setHighlighting(true);
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
			g.setColor(Color.BLUE);
			g.fillOval(mouseX - 3, mouseY - 3, 6, 6);
		}

		// Zeichne Debug Ausgabe
		graphicDebug.draw(g);
	}

	// ------------------------------------------------------------

	/**
	 * Beim schlie&szlig;en des Fensters, nur den Thread pausieren
	 * 
	 * @param e
	 */
	public void windowClosing(WindowEvent e) {
		// stopRefresh();
	}

	/**
	 * Tastatureingabe
	 * 
	 * @param e
	 *            KeyEvent
	 */
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);

		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
			if (mode == MODE.REQUEST) {
				textTick = 10;
				movedPolyomino = null;
				moveDone();
			}
			break;
		case KeyEvent.VK_ENTER:
			action(ACTION_OKAY);
			break;
		case KeyEvent.VK_BACK_SPACE:
			action(ACTION_BACK);
			break;
		case KeyEvent.VK_RIGHT:
			action(ACTION_RIGHT);
			break;
		case KeyEvent.VK_LEFT:
			action(ACTION_LEFT);
			break;
		case KeyEvent.VK_UP:
			action(ACTION_UP);
			break;
		case KeyEvent.VK_DOWN:
			action(ACTION_DOWN);
			break;
		}
	}

	/**
	 * Maus wurde bewegt. Zeige Hover-Effekt
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseMoved(MouseEvent e) {
		if (mode != MODE.REQUEST || requestMode != REQUEST_MODE.MOVE)
			return;

		textTick = 10;

		Position oldPos = mousePosition;
		mousePosition = board.getMouseBoardPosition(e.getX(), e.getY());

		if (mousePosition != null && !mousePosition.equals(oldPos)) {
			Polyomino tmp = polyomino.move(mousePosition.getLetter(), mousePosition.getNumber());
			if (Board.isOnBoard(tmp)) {
				movedPolyomino = polyomino.move(mousePosition.getLetter(), mousePosition.getNumber());
				if (isValidMove(movedPolyomino))
					board.highlightPolyomino(movedPolyomino, viewer.turn(), POLYOMINO_MOVING_CORRECT_ALPHA);
				else
					board.highlightPolyomino(movedPolyomino, viewer.turn(), POLYOMINO_MOVING_ALPHA);
			}
		}
	}

	/**
	 * Maus hat geklickt.
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			action(ACTION_OKAY);
		else if (e.getButton() == MouseEvent.BUTTON3)
			action(ACTION_BACK);

		if (Settings.CFG.is("gui-debug")) {
			Position p = board.getMouseBoardPosition(e.getX(), e.getY());
			if (p != null)
				IO.debugln("Mouse clicked field " + p + " @ GameWindow.mouseClicked");
		}
	}

	/**
	 * Mausrad
	 * 
	 * @param e
	 *            MouseWheelEvent
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		int wheel = e.getWheelRotation();
		if (wheel < 0)
			action(ACTION_LEFT);
		else if (wheel > 0)
			action(ACTION_RIGHT);
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

		updateGraphics();
	}

	/**
	 * Es wurde eine Aktion durchgef&uuml;hrt (Maus oder Tastatur)
	 * 
	 * @param action
	 *            Aktion
	 */
	private void action(int action) {
		if (mode == MODE.REQUEST)
			textTick = 10;

		switch (requestMode) {
		case PICK:
			// Linksklick geht weiter
			switch (action) {
			case ACTION_OKAY:
				polyomino = polyominoBar.getPolyomino();
				graphicPolyomino.setPolyomino(polyomino);
				graphicPolyomino.setMode(GraphicPolyominoRotationFlipper.MODE.ROTATE);
				requestMode = REQUEST_MODE.ROTATE;
				break;
			case ACTION_RIGHT:
				polyominoBar.next();
				break;
			case ACTION_LEFT:
				polyominoBar.previous();
				break;
			}
			break;
		case ROTATE:
			switch (action) {
			case ACTION_OKAY:
				graphicPolyomino.setMode(GraphicPolyominoRotationFlipper.MODE.FLIP);
				requestMode = REQUEST_MODE.FLIP;
				break;
			case ACTION_BACK:
				requestMode = REQUEST_MODE.PICK;
				break;
			case ACTION_RIGHT:
				graphicPolyomino.rotateRight();
				break;
			case ACTION_LEFT:
				graphicPolyomino.rotateLeft();
				break;
			}
			break;
		case FLIP:
			switch (action) {
			case ACTION_OKAY:
				polyomino = graphicPolyomino.getPolyomino();
				movedPolyomino = polyomino;
				requestMode = REQUEST_MODE.MOVE;
				break;
			case ACTION_BACK:
				graphicPolyomino.setMode(GraphicPolyominoRotationFlipper.MODE.ROTATE);
				requestMode = REQUEST_MODE.ROTATE;
				break;
			case ACTION_RIGHT:
				graphicPolyomino.flipHorizontal();
				break;
			case ACTION_LEFT:
				graphicPolyomino.flipVertical();
				break;
			}
			break;
		case MOVE:
			switch (action) {
			case ACTION_OKAY:
				System.out.println(isValidMove(movedPolyomino));
				if (isValidMove(movedPolyomino))
					moveDone();
				break;
			case ACTION_BACK:
				graphicPolyomino.setMode(GraphicPolyominoRotationFlipper.MODE.FLIP);
				requestMode = REQUEST_MODE.FLIP;
				break;
			}
			break;
		}
	}

	// ------------------------------------------------------------

	/**
	 * Fordere einen Zug vom Spieler an
	 * 
	 * @return Spielzug
	 */
	@Override
	public Move deliver() throws Exception {
		IO.debugln("Waiting for gui move @ GameWindow.deliver");
		Move res;
		synchronized (requestMonitor) {
			mode = MODE.REQUEST;
			requestMode = REQUEST_MODE.PICK;
			polyominoBar.setAvailablePolyominos(viewer.getAvailablePolyominos());
			polyominoBar.setColor(viewer.turn());
			graphicPolyomino.setColor(viewer.turn());
			board.highlightPositions(viewer.getValidStartPositions(), viewer.turn());
			requestMonitor.wait();
			mode = MODE.SHOW;
			if (movedPolyomino == null)
				res = null;
			else
				res = new Move(movedPolyomino.getPolyomino());
		}
		IO.debugln("Gui made move @ GameWindow.deliver");
		return res;
	}

	/**
	 * Zug wurde von der Gui erstellt
	 */
	private void moveDone() {
		synchronized (requestMonitor) {
			requestMonitor.notify();
		}
	}

	/**
	 * Pr&uuml;ft, ob ein Polyomino ein g&uuml;ltiger Zug ist (wenn er in der
	 * Liste der g&uuml;ltigen Z&uuml;ge des Viewer vorkommt)
	 * 
	 * @param polyomino
	 *            Polyomino
	 * @return Zug ist g&uuml;ltig
	 */
	private boolean isValidMove(Polyomino polyomino) {
		Move checkMove = new Move(polyomino.getPolyomino());
		for (Move move : viewer.getValidMoves())
			if (move.equals(checkMove))
				return true;
		return false;
	}
}
