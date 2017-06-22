package blokusPP;

import java.awt.RenderingHints;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import blokusPP.game.Match;
import blokusPP.game.MatchSetup;
import blokusPP.graphic.GameWindow;
import blokusPP.gui.MainWindow;
import blokusPP.player.PlayerObject;
import blokusPP.preset.Player;
import blokusPP.preset.Setting;
import eu.nepster.toolkit.gfx.GraphicLoader;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.io.output.FileLog;
import eu.nepster.toolkit.io.output.SystemOut;
import eu.nepster.toolkit.lang.Language;
import eu.nepster.toolkit.settings.ArgumentParser;
import eu.nepster.toolkit.settings.Settings;

/**
 * <h1>Starklasse f&uuml;r BlokusPP</h1>
 * 
 * <h2>Kommandozeilenparameter</h2>
 * <p>
 * 
 * </p>
 * 
 * @author Dominick Leppich
 */
public class Start implements Setting {
	/**
	 * Thread f&uuml;r Spieleabwicklung
	 * 
	 * @author Dominick Leppich
	 *
	 */
	static class GamesThread extends Thread {
		private boolean running;
		private boolean active;
		private Match currentMatch;
		private boolean singleMatch;

		// ------------------------------

		public GamesThread() {
			running = true;
			active = true;
			singleMatch = false;
			start();
		}

		// ------------------------------

		public synchronized void startGames() {
			active = true;
			notify();
		}

		public void startSingleGame() {
			singleMatch = true;
			startGames();
		}

		@SuppressWarnings("deprecation")
		public void stopGames() {
			active = false;
			currentMatch.stop();
			interrupt();
		}

		public synchronized void exit() {
			running = false;
			notify();
		}

		// ------------------------------

		public void run() {
			while (running) {
				try {
					if (active && !matches.isEmpty()) {
						if (singleMatch) {
							active = false;
							singleMatch = false;
						}
						MatchSetup setup = matches.get(0);
						currentMatch = new Match(setup, gui);
						mainWindow.gamesPanel.showStatus("Active Game: " + setup.getGameName());
						removeMatch(0);
						gui.setVisible(true);
						currentMatch.showVS();
						currentMatch.startMatch();
						currentMatch.waitMatchEnd();
						mainWindow.gamesPanel.showStatus("Active Game: --- IDLE ---");
						int timeout = Settings.CFG.getInt("game-timeout");
						if (active && timeout > 0) {
							IO.debugln("Waiting " + timeout + "ms for next game @ Start.gamesThread.run");
							Thread.sleep(timeout);
						}
					} else {
						synchronized (this) {
							IO.debugln("No more games or not active @ Start.GamesThread.run");
							wait();
						}
					}
				} catch (InterruptedException e) {
					IO.errorln("InterruptedException @ Start.GamesThread.run");
				}
			}
			IO.debugln("GamesThread stopped @ Start.GamesThread.run");
		}
	}

	// ------------------------------------------------------------

	private static FileLog log;
	public static MainWindow mainWindow;
	public static GameWindow gui;
	public static ArrayList<PlayerObject> players;
	public static ArrayList<MatchSetup> matches;

	private static GamesThread gamesThread;

	// ------------------------------------------------------------

	/**
	 * Main Methode
	 * 
	 * @param args
	 *            Kommandozeilenargumente
	 */
	public static void main(String[] args) {
		// Initialisiere alles
		initSettings(args);
		initIO();
		initLanguage();
		initGraphics();

		players = new ArrayList<PlayerObject>();
		matches = new ArrayList<MatchSetup>();
		gui = initGui();
		mainWindow = new MainWindow();

		gamesThread = new GamesThread();
	}

	// ------------------------------------------------------------

	/**
	 * F&uuml;ge einen neuen Spieler hinzu
	 * 
	 * @param player
	 *            Spieler
	 * @param name
	 *            Name
	 */
	public static void addPlayer(Player player, String name) {
		PlayerObject newPlayer = new PlayerObject(player, name);
		if (players.contains(newPlayer)) {
			IO.errorln("Can't add new player " + player + " (" + name + "). Already in list @ Start.addPlayer");
			return;
		}
		players.add(newPlayer);
		refreshLists();
		IO.debugln("New Player " + player + " (" + name + ") added @ Start.addPlayer");
	}

	/**
	 * Entferne Spieler
	 * 
	 * @param index
	 *            Index im Array
	 */
	public static void removePlayer(int index) {
		PlayerObject player = players.get(index);
		IO.debugln("Player " + player + " (" + player.getName() + ") removed @ Start.removePlayer");
		players.remove(index);
		refreshLists();
	}
	
	/**
	 * F&uuml;ge ein neues Match hinzu
	 * 
	 * @param gameName
	 *            Name des Spiels
	 * @param blue
	 *            Blauer Spieler
	 * @param yellow
	 *            Gelber Spieler
	 * @param red
	 *            Roter Spieler
	 * @param green
	 *            Gr&uuml;ner Spieler
	 */
	public static void addMatch(String gameName, PlayerObject blue, PlayerObject yellow, PlayerObject red,
			PlayerObject green) {
		addMatch(matches.size(), gameName, blue, yellow, red, green);
	}

	/**
	 * F&uuml;ge ein neues Match hinzu
	 * 
	 * @param index
	 *            Position in der Liste
	 * @param gameName
	 *            Name des Spiels
	 * @param blue
	 *            Blauer Spieler
	 * @param yellow
	 *            Gelber Spieler
	 * @param red
	 *            Roter Spieler
	 * @param green
	 *            Gr&uuml;ner Spieler
	 */
	public static void addMatch(int index, String gameName, PlayerObject blue, PlayerObject yellow, PlayerObject red,
			PlayerObject green) {
		MatchSetup matchSetup = new MatchSetup();
		matchSetup.setGameName(gameName);

		// Bestimme Anzahl echter unterscheidbarer Spieler
		Vector<PlayerObject> players = new Vector<PlayerObject>();
		players.add(blue);
		if (!players.contains(yellow))
			players.add(yellow);
		if (!players.contains(red))
			players.add(red);
		if (!players.contains(green))
			players.add(green);

		// Bestimme Farben jedes Spielers
		for (PlayerObject player : players) {
			ArrayList<Integer> colors = new ArrayList<Integer>();
			if (player.equals(blue))
				colors.add(BLUE);
			if (player.equals(yellow))
				colors.add(YELLOW);
			if (player.equals(red))
				colors.add(RED);
			if (player.equals(green))
				colors.add(GREEN);
			int[] colorsArray = new int[colors.size()];
			for (int i = 0; i < colors.size(); i++)
				colorsArray[i] = colors.get(i);

			matchSetup.addPlayer(player, colorsArray);
		}

		matches.add(index, matchSetup);
		refreshLists();
		IO.debugln("New Game " + matchSetup.getGameName() + " added @ Start.addMatch");
	}

	/**
	 * Entferne ein Match
	 * 
	 * @param index
	 *            Index im Array
	 */
	public static void removeMatch(int index) {
		MatchSetup matchSetup = matches.get(index);
		IO.debugln("Match " + matchSetup.getGameName() + " removed @ Start.removeMatch");
		matches.remove(index);
		refreshLists();
	}

	/**
	 * Starte die Spiele in der Liste
	 */
	public static void startGamesThread() {
		gamesThread.startGames();
	}

	/**
	 * Starte ein Spiel aus der Liste
	 */
	public static void startSingleGameThread() {
		gamesThread.startSingleGame();
	}

	/**
	 * Stoppe Spieleausf&uuml;hrung
	 */
	public static void stopGamesThread() {
		gamesThread.stopGames();
	}

	/**
	 * Aktualisere alle Listen
	 */
	public static void refreshLists() {
		Collections.sort(players, Collections.reverseOrder());
		mainWindow.playerPanel.refreshList();
		mainWindow.networkPanel.refreshLocalPlayerList();
		mainWindow.gamesPanel.refreshList();
	}

	// ------------------------------------------------------------

	/**
	 * Initialisiere Ein- und Ausgabe
	 */
	public static void initIO() {
		IO.createIOChannel("gui");

		if (!Settings.CFG.is("noout")) {
			IO.register(new SystemOut(), IO.LEVEL_NORMAL, false);
			IO.register(new SystemOut(), IO.LEVEL_ERROR, false);

			if (Settings.CFG.is("debug")) {
				IO.register(new SystemOut(), IO.LEVEL_DEBUG, false);
				log = new FileLog();
				IO.register(log, IO.LEVEL_DEBUG, true);
			}
		}
	}

	/**
	 * Initialisiere Einstellungen
	 * 
	 * @param args
	 *            Kommandozeilenargumente
	 */
	public static void initSettings(String[] args) {
		Settings.CFG.load(new File("res/config.cfg"));

		// Default Settings
		Settings.CFG.setSetNotFoundAction(Settings.CREATE_SETTING);
		Settings.CFG.setAddFoundAction(Settings.NO_ACTION);
		Settings.CFG.add("noout", false);
		Settings.CFG.add("debug", false);
		Settings.CFG.add("gui-debug", false);
		Settings.CFG.add("save", false);
		Settings.CFG.add("min-timeout", 1000);
		Settings.CFG.add("game-timeout", 5000);
		Settings.CFG.add("max-player-time", 300000L);
		Settings.CFG.add("max-move-time", 30000L);

		ArgumentParser.parse(args, Settings.CFG);
	}

	/**
	 * Lade Sprachen
	 */
	public static void initLanguage() {
		Language.load(new File("res/lang"));
	}

	/**
	 * Lade Grafiken
	 */
	public static void initGraphics() {
		GraphicLoader.GFX.showLoading(false);
		GraphicLoader.GFX.loadSynchron(new File("res/gfx"), true, "");
	}

	/**
	 * Erzeuge Gui
	 * 
	 * @return Gui
	 */
	public static GameWindow initGui() {
		GameWindow gui = new GameWindow();

		// Setze Render Qualitaet
		gui.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// gui.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		// RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		// Register GUI as output
		IO.registerOnIOChannel("gui", gui);

		gui.setFps(60);
		gui.startRefresh();

		return gui;
	}

	// ------------------------------------------------------------

	/**
	 * Beende alles
	 */
	public static void exit() {
		// Wenn noetig Log speichern
		if (log != null)
			log.save("save/log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt");

		// GraphicLoader beenden
		GraphicLoader.GFX.exit();

		// Einstellungen speichern
		Settings.CFG.save(new File("res/config.cfg"));

		// MainWindow beenden
		if (mainWindow != null)
			mainWindow.dispose();

		// GamesThread beenden
		gamesThread.exit();

		IO.debugln("Application terminated @ Start.exit");
		System.exit(0);
	}
}
