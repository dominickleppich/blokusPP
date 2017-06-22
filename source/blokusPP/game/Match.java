package blokusPP.game;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Vector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import blokusPP.Start;
import blokusPP.board.Board;
import blokusPP.graphic.GameWindow;
import blokusPP.graphic.GraphicConstants;
import blokusPP.io.AsciiBoard;
import blokusPP.player.HumanPlayer;
import blokusPP.player.PlayerObject;
import blokusPP.preset.Move;
import blokusPP.preset.Player;
import blokusPP.preset.Setting;
import blokusPP.preset.Status;
import blokusPP.preset.Viewer;
import eu.nepster.toolkit.file.StringFile;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.lang.Language;
import eu.nepster.toolkit.settings.Settings;
import eu.nepster.toolkit.time.StopWatch;

/**
 * <h1>Match</h1>
 * 
 * <p>
 * Diese Klasse l&auml;sst ein Spiel ablaufen. Spiele werden protokolliert und
 * k&ouml;nnen im Anschluss exportiert werden. Es wird ein Objekt der Klasse
 * <code>MatchSetup</code> &uuml;bergeben, in dem alle Einstellungen f&uuml;r
 * das Spiel definiert sind
 * </p>
 * <p>
 * Den Spielern werden dann automatisch ihre Farben zugewiesen und dann
 * resettet.
 * </p>
 * 
 * @author Dominick Leppich
 *
 */
public class Match extends Thread implements Setting, GraphicConstants {
	/**
	 * Dieser Thread kontrolliert, dass die Zeiten von den Spielern eingehalten
	 * werden.
	 * 
	 * @author Dominick Leppich
	 *
	 */
	class TimeControlThread extends Thread {
		private Match match;
		private GameWindow gui;
		private boolean active = false;
		private boolean running = true;

		private long playerTime, moveTime, maxMoveTime;
		private PlayerObject player;

		// ------------------------------

		public TimeControlThread(Match match, GameWindow gui) {
			this.match = match;
			this.gui = gui;
		}

		// ------------------------------

		/**
		 * Beginne Kontrolle
		 * 
		 * @param playerTime
		 *            Spielerzeit
		 * @param moveTime
		 *            Zugzeit
		 * @param player
		 *            Spieler
		 */
		public synchronized void startControl(long playerTime, long moveTime, PlayerObject player) {
			this.playerTime = playerTime;
			this.moveTime = moveTime;
			this.maxMoveTime = moveTime;
			this.player = player;
			active = true;
			notify();
		}

		/**
		 * Beende Kontrolle und gib Restzeit des Spielers zur&uuml;ck
		 * 
		 * @return Restzeit
		 */
		public long stopControl() {
			active = false;
			return playerTime;
		}

		// ------------------------------

		@SuppressWarnings("deprecation")
		public void run() {
			while (running) {
				try {
					if (active) {
						Thread.sleep(100);
						playerTime -= 100;
						moveTime -= 100;
						// TODO Geht nur fuer zwei Spieler
						gui.setPlayerTime(player, (int) (playerTime / 1000), playerTime <= 10000);
						gui.setMoveTime(1.0 - (double) moveTime / maxMoveTime);
						if (playerTime < 0 || moveTime < 0) {
							match.illegalWin(player);
							IO.errorln("Player " + player.getName() + " time over @ Match.TimeControlThread.run");
							if (gui != null) {
								gui.disableInput();
								IO.print("gui", Language.get("game_end_time_over", player.getName()));
							}
							error = true;
							match.stop();
							match.exit();
							running = false;
						}
					} else {
						synchronized (this) {
							wait();
						}
					}
				} catch (InterruptedException e) {
					IO.errorln("Error waiting thread @ Match.TimeControlThread.run");
				}
			}
		}
	}

	// ------------------------------------------------------------

	public static final int FIELD_SIZE = 30;

	// ------------------------------------------------------------

	// GameWindow
	private GameWindow gameWindow;

	// Spiel Einstellungen
	private MatchSetup setup;

	// Spielbrett
	private Board board;
	// Spielbrett Viewer
	private Viewer viewer;

	// Ist es der erste Zug?
	private boolean firstMove;

	// Stoppuhr zum messen der Zugzeit
	private StopWatch stopWatch;

	// running = true: Das Spiel laueft noch, kann aber pausiert sein
	// running = false: Spiel beendet
	// active = true: Spiel wird zur Zeit ausgefuehrt
	private boolean active;

	// Fehler
	private boolean error;

	// Monitor zur Synchronisation des Speicherns
	private Object saveGameMonitor;

	// HTML Spiel
	private String htmlContent;
	private String startHtmlStuff;
	private String endHtmlStuff;

	// Liste aller gemachten Zuege
	private LinkedList<MatchMove> madeMoves;

	// Zugzaehler
	private int moveCounter;

	// Zeiten
	private HashMap<PlayerObject, Long> playerTimes;
	private TimeControlThread tct;

	// ArrayList der Gewinner
	private ArrayList<PlayerObject> winners;

	// ------------------------------------------------------------

	/**
	 * Erzeuge neues Match
	 * 
	 * @param setup
	 *            Einstellungen
	 */
	public Match(MatchSetup setup, GameWindow gameWindow) {
		this.setup = setup;
		this.gameWindow = gameWindow;
		if (Settings.CFG.getLong("max-player-time") > 0 && Settings.CFG.getLong("max-move-time") > 0) {
			tct = new TimeControlThread(this, gameWindow);
			tct.start();
			gameWindow.setPlayerTimeInformation(true);
		} else
			gameWindow.setPlayerTimeInformation(false);
		gameWindow.setPlayerInformation(true);
		error = false;
		firstMove = true;
		stopWatch = new StopWatch(false);
		saveGameMonitor = new Object();
		htmlContent = "";
		madeMoves = new LinkedList<MatchMove>();
		moveCounter = 0;

		playerTimes = new HashMap<PlayerObject, Long>();

		initPlayers();

		// Setze GUI vor Beginn des Spiels zurueck
		if (gameWindow != null) {
			ArrayList<PlayerObject> players = setup.getPlayers();
			gameWindow.reset();
			gameWindow.setPlayers(players);
			gameWindow.updateGui();
			gameWindow.setMoveTime(0.0);
			for (PlayerObject player : players)
				gameWindow.setPlayerTime(player, Settings.CFG.getLong("max-player-time") / 1000, false);
		}

		setBoard(null);

		start();
	}

	/**
	 * Initialisiere die Spieler f&uuml;r das Spiel
	 */
	private void initPlayers() {
		if (!setup.isReady())
			throw new MatchSetupException("Can't create match, MatchSetup not complete!");

		PlayerObject exceptionPlayer = null;
		try {
			for (PlayerObject p : setup.getPlayers()) {
				exceptionPlayer = p;
				p.getPlayer().reset(setup.getPlayerColors(p));
				playerTimes.put(p, Settings.CFG.getLong("max-player-time"));
			}
			return;
		} catch (RemoteException e) {
			IO.errorln("RemoteException with player " + exceptionPlayer.getName() + " @ Match.initPlayers");
			// if (exceptionPlayer != null)
			// illegalWin(exceptionPlayer);
			if (gameWindow != null)
				IO.print("gui", Language.get("game_end_remote_exception", exceptionPlayer.getName()));
			// e.printStackTrace();
		} catch (Exception e) {
			IO.errorln("Exception with player " + exceptionPlayer.getName() + " @ Match.initPlayers");
			// if (exceptionPlayer != null)
			// illegalWin(exceptionPlayer);
			if (gameWindow != null)
				IO.print("gui", Language.get("game_end_exception", exceptionPlayer.getName()));
			e.printStackTrace();
		}
		error = true;
	}

	/**
	 * Zeige VS Screen an (nur bei zwei Spielern)
	 */
	public void showVS() {
		ArrayList<PlayerObject> players = setup.getPlayers();
		// Breche ab, falls nicht zwei Spieler vorhanden
		if (players.size() != 2)
			return;
		if (gameWindow == null)
			return;

		PlayerObject first = players.get(0);
		int[] firstColors = setup.getPlayerColors(first);
		PlayerObject second = players.get(1);
		int[] secondColors = setup.getPlayerColors(second);
		if (firstColors.length == 2 && secondColors.length == 2)
			gameWindow.vs(first.getName(), firstColors[0], firstColors[1], second.getName(), secondColors[0],
					secondColors[1]);
		else
			gameWindow.vs(first.getName(), firstColors[0], firstColors[0], second.getName(), secondColors[0],
					secondColors[0]);
		gameWindow.waitGameWindow();
	}

	// ------------------------------------------------------------

	/**
	 * Setze zu verwendendes Spielbrett (zum Laden erforderlich).
	 * <code>null</code> erzeugt eine neues Spielbrett
	 * 
	 * @param board
	 *            Spielbrett
	 */
	public void setBoard(Board board) {
		if (board == null)
			this.board = new Board();

		viewer = this.board.viewer();

		// Falls GUI vorhanden, uebergib den Viewer
		if (gameWindow != null) {
			gameWindow.setViewer(viewer);
			gameWindow.updateGui();
		}
	}

	// ------------------------------------------------------------

	/**
	 * Startet das Spiel
	 */
	public synchronized void startMatch() {
		IO.debugln("Match started @ Match.startMatch");
		active = true;
		notify();
	}

	/**
	 * H&auml;lt das Spiel an
	 */
	public void pauseMatch() {
		IO.debugln("Match paused @ Match.pauseMatch");
		active = false;
	}

	/**
	 * Beendet das Spiel vorzeitig
	 */
	@SuppressWarnings("deprecation")
	public void stopMatch() {
		IO.debugln("Match stopped @ Match.stopMatch");
		stop();
	}

	/**
	 * Wartet bis das Spiel zu Ende ist
	 */
	public synchronized void waitMatchEnd() {
		try {
			wait();
			IO.debugln("Gui finished! @ Match.waitMatchEnd");
		} catch (InterruptedException e) {
			IO.errorln("InterruptedException @ Match.waitMatchEnd");
			// e.printStackTrace();
		}

	}

	/**
	 * Weckt wartende Threads auf
	 */
	private synchronized void wakeUpWaitingThreads() {
		notify();
	}

	// ------------------------------------------------------------

	/**
	 * Initialisiere das HTML Dokument. Erzeuge Start- und Endteil
	 */
	private void initHtml() {
		startHtmlStuff = "";

		startHtmlStuff += "<!DOCTYPE html>\n";
		startHtmlStuff += "<html lang=\"de\">\n";
		startHtmlStuff += "\t<head>\n";
		startHtmlStuff += "\t\t<title>" + setup.getGameName() + "</title>\n";
		startHtmlStuff += "\t\t<style type=\"text/css\">\n";
		startHtmlStuff += "\t\t\tbody {\n";
		startHtmlStuff += "\t\t\t\twidth: " + (FIELD_SIZE * (Board.SIZE + 1.5)) + "px;\n";
		startHtmlStuff += "\t\t\t\tbackground-color: #EFEFEF;\n";
		startHtmlStuff += "\t\t\t\tcolor: #000000;\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t\t.board {\n";
		startHtmlStuff += "\t\t\t\tposition: relative;\n";
		startHtmlStuff += "\t\t\t\twidth: " + (FIELD_SIZE * (Board.SIZE + 1)) + "px;\n";
		startHtmlStuff += "\t\t\t\theight: " + (FIELD_SIZE * (Board.SIZE + 1)) + "px;\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t\t.field {\n";
		startHtmlStuff += "\t\t\t\tposition: absolute;\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t\t.move_blue {\n";
		startHtmlStuff += "\t\t\t\tborder-radius: 15px;\n";
		startHtmlStuff += "\t\t\t\tmargin-bottom: 10px;\n";
		startHtmlStuff += "\t\t\t\tpadding: 20px;\n";
		startHtmlStuff += "\t\t\t\tbackground-color: " + DIV_BACKGROUND_BLUE + ";\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t\t.move_yellow {\n";
		startHtmlStuff += "\t\t\t\tborder-radius: 15px;\n";
		startHtmlStuff += "\t\t\t\tmargin-bottom: 10px;\n";
		startHtmlStuff += "\t\t\t\tpadding: 20px;\n";
		startHtmlStuff += "\t\t\t\tbackground-color: " + DIV_BACKGROUND_YELLOW + ";\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t\t.move_red {\n";
		startHtmlStuff += "\t\t\t\tborder-radius: 15px;\n";
		startHtmlStuff += "\t\t\t\tmargin-bottom: 10px;\n";
		startHtmlStuff += "\t\t\t\tpadding: 20px;\n";
		startHtmlStuff += "\t\t\t\tbackground-color: " + DIV_BACKGROUND_RED + ";\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t\t.move_green {\n";
		startHtmlStuff += "\t\t\t\tborder-radius: 15px;\n";
		startHtmlStuff += "\t\t\t\tmargin-bottom: 10px;\n";
		startHtmlStuff += "\t\t\t\tpadding: 20px;\n";
		startHtmlStuff += "\t\t\t\tbackground-color: " + DIV_BACKGROUND_GREEN + ";\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t\t.stats {\n";
		startHtmlStuff += "\t\t\t\tborder-radius: 15px;\n";
		startHtmlStuff += "\t\t\t\tmargin-bottom: 10px;\n";
		startHtmlStuff += "\t\t\t\tpadding: 20px;\n";
		startHtmlStuff += "\t\t\t\tbackground-color: " + DIV_BACKGROUND_STASTICS + ";\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t\timg.field {\n";
		startHtmlStuff += "\t\t\t\twidth: " + FIELD_SIZE + "px;\n";
		startHtmlStuff += "\t\t\t\theight: " + FIELD_SIZE + "px;\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t\timg.board {\n";
		startHtmlStuff += "\t\t\t\twidth: " + FIELD_SIZE * Board.SIZE + "px;\n";
		startHtmlStuff += "\t\t\t\theight: " + FIELD_SIZE * Board.SIZE + "px;\n";
		startHtmlStuff += "\t\t\t}\n";
		startHtmlStuff += "\t\t</style>\n";
		startHtmlStuff += "\t</head>\n";
		startHtmlStuff += "\t<body>\n";

		endHtmlStuff = "";
		// TODO Sieger anzeigen
		endHtmlStuff += "\t\t<div class=\"stats\">\n";
		endHtmlStuff += "\t\t\t<table border=\"0\">\n";
		endHtmlStuff += "\t\t\t\t<tr>\n";
		endHtmlStuff += "\t\t\t\t\t<td align=\"right\"><b>Statistik</b></td>\n";
		endHtmlStuff += "\t\t\t\t\t<td>&nbsp;</td>\n";
		endHtmlStuff += "\t\t\t\t</tr>\n";

		for (PlayerObject player : setup.getPlayers()) {
			endHtmlStuff += "\t\t\t\t<tr>\n";
			endHtmlStuff += "\t\t\t\t\t<td colspan=\"2\">&nbsp;</td>\n";
			endHtmlStuff += "\t\t\t\t</tr>\n";
			
			endHtmlStuff += "\t\t\t\t<tr>\n";
			endHtmlStuff += "\t\t\t\t\t<td align=\"right\"><b>" + player.getName() + "</b></td>\n";
			endHtmlStuff += "\t\t\t\t\t<td>&nbsp;</td>\n";
			endHtmlStuff += "\t\t\t\t</tr>\n";
			
			endHtmlStuff += "\t\t\t\t<tr>\n";
			endHtmlStuff += "\t\t\t\t\t<td align=\"right\"><b>Farben:</b></td>\n";
			endHtmlStuff += "\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;";
			// Farben des Spielers berechnen
			int[] colors = setup.getPlayerColors(player);
			for (int i = 0; i < colors.length; i++) {
				String htmlColor = "";
				switch (colors[i]) {
				case BLUE:
					htmlColor = "<font color=\"blue\">" + colorString[BLUE] + "</font>";
					break;
				case YELLOW:
					htmlColor = "<font color=\"yellow\">" + colorString[YELLOW] + "</font>";
					break;
				case RED:
					htmlColor = "<font color=\"red\">" + colorString[RED] + "</font>";
					break;
				case GREEN:
					htmlColor = "<font color=\"green\">" + colorString[GREEN] + "</font>";
					break;
				}
				if (i > 0)
					endHtmlStuff += ", ";
				endHtmlStuff += htmlColor;
			}
			endHtmlStuff += "</td>\n";
			endHtmlStuff += "\t\t\t\t</tr>\n";
			
			endHtmlStuff += "\t\t\t\t<tr>\n";
			endHtmlStuff += "\t\t\t\t\t<td align=\"right\"><b>Punkte:</b></td>\n";
			endHtmlStuff += "\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;";
			// Punkte des Spielers berechnen
			int points = 0;
			for (int i : colors)
				points += board.getScore(i);
			endHtmlStuff += points;
			endHtmlStuff += "</td>\n";
			endHtmlStuff += "\t\t\t\t</tr>\n";
			
//			endHtmlStuff += "\t\t\t\t<tr>\n";
//			endHtmlStuff += "\t\t\t\t\t<td align=\"right\"><b>Gesamte Zugzeit:</b></td>\n";
//			endHtmlStuff += "\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + String.valueOf(redTime / 1000) + "."
//					+ String.valueOf(redTime % 1000) + "s</td>\n";
//			endHtmlStuff += "\t\t\t\t</tr>\n";
		}
		
		endHtmlStuff += "\t\t\t</table>\n";
		endHtmlStuff += "\t\t</div>\n";
		endHtmlStuff += "\t</body>\n";
		endHtmlStuff += "</html>\n";
	}

	/**
	 * F&uuml;ge Html Inhalt hinzu
	 * 
	 * @param s
	 *            Inhalt
	 */
	public void addHtmlContent(String s) {
		htmlContent += s;
	}

	/**
	 * Speichere das Spiel ab (sowohl XML als auch HTML)
	 * 
	 * @param name
	 *            Name
	 */
	public void saveGame(String name) {
		synchronized (saveGameMonitor) {
			// XML
			try {
				/* Wurzelelement erzeugen */
				Element root = new Element("match");
				Document doc = new Document(root);

				Element blueName = new Element("blue");
				blueName.setText(setup.getPlayer(BLUE).getName());
				Element yellowName = new Element("yellow");
				yellowName.setText(setup.getPlayer(YELLOW).getName());
				Element redName = new Element("red");
				redName.setText(setup.getPlayer(RED).getName());
				Element greenName = new Element("green");
				greenName.setText(setup.getPlayer(GREEN).getName());

				root.addContent(blueName);
				root.addContent(yellowName);
				root.addContent(redName);
				root.addContent(greenName);

				Element game = new Element("game");
				game.setText(setup.getGameName());
				root.addContent(game);

				Element moveCount = new Element("move_count");
				moveCount.setText(String.valueOf(madeMoves.size()));
				root.addContent(moveCount);

				for (MatchMove mm : madeMoves) {
					Element e = new Element("move_" + mm.number);
					e.addContent(new Element("player").setText(mm.playerName));
					e.addContent(new Element("color").setText(Integer.toString(mm.color)));
					e.addContent(new Element("move").setText((mm.move != null ? mm.move.toString() : "null")));
					e.addContent(new Element("time").setText(Long.toString(mm.time)));
					e.addContent(new Element("status").setText(mm.status.toString()));
					root.addContent(e);
				}

				/* XML Datei speichern */
				FileOutputStream outStream = new FileOutputStream(new File(
						"save/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + name + ".xml"));
				XMLOutputter outToFile = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("unicode");
				outToFile.setFormat(format);
				outToFile.output(doc, outStream);
				outStream.flush();
				outStream.close();

				IO.debugln("Saving xml game successful @ Match.saveGame");
			} catch (IOException e) {
				IO.errorln("Error saving xml file " + name + ".xml @ Match.saveGame");
			}

			// HTML
			Vector<String> vec = new Vector<String>();
			initHtml();
			vec.add(startHtmlStuff);
			vec.add(htmlContent);
			vec.add(endHtmlStuff);
			StringFile.save(new File(
					"save/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + name + ".html"), vec,
					true, true);
		}

	}

	// ------------------------------------------------------------

	/**
	 * Hauptspielschleife
	 */
	public void run() {
		/* Nur solange das Spiel an ist */
		while (!board.getStatus().isGameOver() && !isInterrupted() && !error) {
			/*
			 * Wenn das Spiel nicht pausiert ist, fuehre alle noetigen Schritte
			 * aus
			 */
			if (active && setup.isReady()) {
				int color = -1;
				PlayerObject playerObject = null, exceptionPlayer = null;
				Player player = null;
				ArrayList<PlayerObject> opponents = null;
				String playerName = "";
				try {
					if (firstMove) {
						/* Zeige Brett an */
						IO.debugln(AsciiBoard.getAsciiBoard(viewer));
						firstMove = false;
					}

					color = board.getActiveColor();
					playerObject = setup.getPlayer(color);
					player = playerObject.getPlayer();
					opponents = setup.getOpponentPlayers(playerObject);
					playerName = playerObject.getName();

					/* Fordere einen Zug an und messe die Zeit */
					IO.println(Language.get("player_move_request", playerName));

					// Naechster Fehler kommt vom Player, auf dem Request
					// aufgerufen wird
					if (gameWindow != null)
						gameWindow.setPlayerColor(playerObject, color);
					exceptionPlayer = playerObject;
					stopWatch.start();
					if (tct != null)
						tct.startControl(playerTimes.get(playerObject), Settings.CFG.getLong("max-move-time"),
								playerObject);
					Move move = player.request();
					if (tct != null)
						playerTimes.put(playerObject, tct.stopControl());
					stopWatch.stop();

					/* Kleiner Timeout falls noetig */
					long waitTime = Settings.CFG.getInt("min-timeout") - stopWatch.getMilliTime();
					if (waitTime > 0) {
						IO.debugln("Timeout for " + waitTime + "ms @ Match.run");
						Thread.sleep(waitTime);
					}

					board.makeMove(move);

					// Aktualisiere GUI falls vorhanden
					if (gameWindow != null)
						gameWindow.updateGui();

					IO.println(Language.get("player_made_move", playerName,
							(move != null ? move.toString() : Language.get("move_pause"))));

					Status status = board.getStatus();
					IO.debugln("Board status is: " + status + " @ Match.run");

					/* Speichere Zug ab */
					synchronized (saveGameMonitor) {
						int scores[] = new int[GAME_COLORS];
						for (int i = 0; i < GAME_COLORS; i++)
							scores[i] = board.getScore(i);
						MatchMove matchMove = new MatchMove(move, ++moveCounter, board.html(), playerName, color,
								stopWatch.getMilliTime(), status, scores);
						madeMoves.addLast(matchMove);
						addHtmlContent(matchMove.html());
					}

					IO.debugln("Needed " + (double) stopWatch.getMilliTime() / 1000 + "s for move @ Match.run");

					if (tct != null)
						tct.startControl(playerTimes.get(playerObject), Settings.CFG.getLong("max-move-time"),
								playerObject);
					player.confirm(status);
					if (tct != null)
						playerTimes.put(playerObject, tct.stopControl());

					// Update alle Gegner
					for (PlayerObject opponent : opponents) {
						// Naechster moeglicher Fehler tritt beim Spieler mit
						// Update auf
						if (tct != null)
							tct.startControl(playerTimes.get(opponent), Settings.CFG.getLong("max-move-time"),
									opponent);
						exceptionPlayer = opponent;
						opponent.getPlayer().update(move, status);
						if (tct != null)
							playerTimes.put(opponent, tct.stopControl());
					}

					// Spielerwechsel wird vom Board automatisch uebernommen

					/* Zeige Brett an */
					IO.debugln(AsciiBoard.getAsciiBoard(viewer));

					// Warte bis GUI Text verschwindet
					if (gameWindow != null && player instanceof HumanPlayer)
						gameWindow.waitGameWindow();
				} catch (InterruptedException e) {
					IO.errorln("InterruptedException in Match @ Match.run");
					e.printStackTrace();
				} catch (RemoteException e) {
					IO.errorln("RemoteException in Match with player " + playerName + " @ Match.run");
					if (exceptionPlayer != null)
						illegalWin(exceptionPlayer);
					if (gameWindow != null)
						IO.print("gui", Language.get("game_end_remote_exception", exceptionPlayer.getName()));
					error = true;
					e.printStackTrace();
				} catch (Exception e) {
					IO.errorln("Exception in Match with player " + playerName + " @ Match.run");
					if (exceptionPlayer != null)
						illegalWin(exceptionPlayer);
					if (gameWindow != null)
						IO.print("gui", Language.get("game_end_exception", exceptionPlayer.getName()));
					e.printStackTrace();
					error = true;
				}
			}
			/* Sonst lege den Thread schlafen */
			else {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						IO.errorln("Exception in game pause @ Match.run");
						e.printStackTrace();
					}
				}
			}
		}
		exit();
	}

	// ------------------------------------------------------------

	/**
	 * Das Spiel wurde durch einen illegalen Zug beendet, alle anderen Spieler
	 * gewinnen
	 * 
	 * @param badPlayer
	 *            Spieler, der den illegalen Zug gemacht hat
	 */
	private void illegalWin(PlayerObject badPlayer) {
		// Alle anderen Spieler haben gewonnen
		winners = setup.getOpponentPlayers(badPlayer);
	}

	/**
	 * Normales Spielende, Punkte werden gez&auml;hlt und Spieler mit der
	 * h&ouml;chsten Punktzahl bestimmt
	 */
	private void normalWin() {
		// Bestimme die Spieler mit maximaler Punktzahl
		ArrayList<PlayerObject> players = setup.getPlayers();
		HashMap<PlayerObject, Integer> playerScore = new HashMap<PlayerObject, Integer>();

		// Rechne fuer jeden Spieler Punkte zusammen
		for (PlayerObject p : players) {
			int score = 0;
			for (int c : setup.getPlayerColors(p))
				score += board.getScore(c);
			playerScore.put(p, score);
		}

		// Bestimme maximale Punktzahl
		int highScore = 0;
		for (int score : playerScore.values())
			if (score > highScore)
				highScore = score;

		// Bestimme die Spieler mit HighScore
		winners = new ArrayList<PlayerObject>();

		// Fuege jeden Spieler mit der Punktzahl des Highscores zu den Gewinnern
		// hinzu
		for (Entry<PlayerObject, Integer> e : playerScore.entrySet())
			if (e.getValue() == highScore)
				winners.add(e.getKey());
	}

	// ------------------------------------------------------------

	/**
	 * Gib die Gewinner zur&uuml;ck
	 * 
	 * @return Liste der Gewinner
	 */
	public ArrayList<PlayerObject> getWinners() {
		return winners;
	}

	/**
	 * Beende ein Match
	 */
	public void exit() {
		// Bei Fehler nicht weitermachen
		if (!error) {
			// Spiel ist vorbei
			Status status = board.getStatus();
			IO.debugln("Game ended with status " + status + " @ Match.run");
			// Zeige Punkte an
			IO.debugln("Points: " + colorString[BLUE] + ": " + board.getScore(BLUE) + ", " + colorString[YELLOW] + ": "
					+ board.getScore(YELLOW) + ", " + colorString[RED] + ": " + board.getScore(RED) + ", "
					+ colorString[GREEN] + ": " + board.getScore(GREEN) + " @ Match.run");

			// Erkenne Grund des Spielendes
			// Alle Farben pausieren
			if (status.getScore(BLUE) == PAUSE && status.getScore(YELLOW) == PAUSE && status.getScore(RED) == PAUSE
					&& status.getScore(GREEN) == PAUSE) {
				IO.debugln("Game over, all colors pausing @ Match.run");
				if (gameWindow != null)
					IO.print("gui", Language.get("game_end_pause"));
				normalWin();
			}
			// Illegaler Zug
			else if (status.getScore(BLUE) == ILLEGAL) {
				IO.debugln("Game over, " + colorString[BLUE] + " made illegal move @ Match.run");
				if (gameWindow != null)
					IO.print("gui", Language.get("game_end_illegal", Language.get("color_blue")));
				illegalWin(setup.getPlayer(BLUE));
			} else if (status.getScore(YELLOW) == ILLEGAL) {
				IO.debugln("Game over, " + colorString[BLUE] + " made illegal move @ Match.run");
				if (gameWindow != null)
					IO.print("gui", Language.get("game_end_illegal", Language.get("color_yellow")));
				illegalWin(setup.getPlayer(YELLOW));
			} else if (status.getScore(RED) == ILLEGAL) {
				IO.debugln("Game over, " + colorString[BLUE] + " made illegal move @ Match.run");
				if (gameWindow != null)
					IO.print("gui", Language.get("game_end_illegal", Language.get("color_red")));
				illegalWin(setup.getPlayer(RED));
			} else if (status.getScore(GREEN) == ILLEGAL) {
				IO.debugln("Game over, " + colorString[BLUE] + " made illegal move @ Match.run");
				if (gameWindow != null)
					IO.print("gui", Language.get("game_end_illegal", Language.get("color_green")));
				illegalWin(setup.getPlayer(GREEN));
			}
			// Eine Farbe hat beendet
			else if (status.getScore(BLUE) == FINISH) {
				IO.debugln("Game over, " + colorString[BLUE] + " placed all polyominos move @ Match.run");
				if (gameWindow != null)
					IO.print("gui", Language.get("game_end_full", Language.get("color_blue")));
				normalWin();
			} else if (status.getScore(YELLOW) == FINISH) {
				IO.debugln("Game over, " + colorString[BLUE] + " placed all polyominos move @ Match.run");
				if (gameWindow != null)
					IO.print("gui", Language.get("game_end_full", Language.get("color_yellow")));
				normalWin();
			} else if (status.getScore(RED) == FINISH) {
				IO.debugln("Game over, " + colorString[BLUE] + " placed all polyominos move @ Match.run");
				if (gameWindow != null)
					IO.print("gui", Language.get("game_end_full", Language.get("color_red")));
				normalWin();
			} else if (status.getScore(GREEN) == FINISH) {
				IO.debugln("Game over, " + colorString[BLUE] + " placed all polyominos move @ Match.run");
				if (gameWindow != null)
					IO.print("gui", Language.get("game_end_full", Language.get("color_green")));
				normalWin();
			}
		}

		if (winners != null) {
			// Zeige Gewinner an
			IO.debug("Winners are: ");
			for (PlayerObject p : winners)
				IO.debug(p.getName() + " ");
			IO.debugln("@ Match.run");
			if (gameWindow != null) {
				if (winners.size() == 1)
					IO.print("gui", Language.get("player_won", winners.get(0).getName()));
				else {
					String winnerNames = winners.get(0).getName();
					for (int i = 1; i < winners.size(); i++)
						winnerNames += " und " + winners.get(i).getName();
					IO.print("gui", Language.get("players_won", winnerNames));
				}
				// gameWindow.waitGameWindow();
			}

			// Speichere Siege und Niederlagen in PlayerObjecten
			for (PlayerObject po : setup.getPlayers()) {
				if (winners.contains(po))
					po.win();
				else
					po.lose();
			}

			// Aktualisere Player Liste im PlayerPanel
			Start.refreshLists();

			// Eventuell speichern
			if (Settings.CFG.is("save"))
				saveGame(setup.getGameName());
		}

		// Warte auf GUI
		if (gameWindow != null)
			gameWindow.waitGameWindow();

		/* Wecke wartende Threads */
		wakeUpWaitingThreads();
	}
}
