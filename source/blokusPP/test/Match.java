package blokusPP.test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import blokusPP.board.Board;
import blokusPP.game.MatchSetup;
import blokusPP.game.MatchSetupException;
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
			e.printStackTrace();
		} catch (Exception e) {
			IO.errorln("Exception with player " + exceptionPlayer.getName() + " @ Match.initPlayers");
			e.printStackTrace();
		}
		error = true;
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
		}

		// Warte auf GUI
		if (gameWindow != null)
			gameWindow.waitGameWindow();

		/* Wecke wartende Threads */
		wakeUpWaitingThreads();
	}
}
