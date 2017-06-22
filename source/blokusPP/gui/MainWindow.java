package blokusPP.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import blokusPP.Start;
import eu.nepster.toolkit.gfx.GraphicTools;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.io.Outputable;

/**
 * Hauptfenster des Spiels. Hier werden alle Einstellungen vorgenommen sowie die
 * Spiele erstellt.
 * 
 * @author Dominick Leppich
 *
 */
public class MainWindow extends JFrame implements WindowListener, Outputable {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	// Panel
	public MainMenuPanel mainMenuPanel;
	public PlayerPanel playerPanel;
	public GamesPanel gamesPanel;
	public NetworkPanel networkPanel;
	public LogPanel logPanel;
	public SettingsPanel settingsPanel;

	private Timer statusIdleTimer;
	private long lastIdle;

	private JLabel statusLabel;

	// ------------------------------------------------------------

	/**
	 * Default Konstruktor
	 */
	public MainWindow() {
		super("blokusPP");

		statusIdleTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((System.currentTimeMillis() - lastIdle) / 1000) > 3)
					statusLabel.setText("--- IDLE ---");
			}
		});
		statusIdleTimer.start();
		lastIdle = System.currentTimeMillis();

		addWindowListener(this);

		// Setze Fenster Icon
		try {
			setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
			IO.errorln("Icon not found @ GameWindow.<init>");
		}

		Box vertBox = Box.createVerticalBox();
		JTabbedPane tabbedPane = new JTabbedPane();

		mainMenuPanel = new MainMenuPanel();
		tabbedPane.addTab("Main Menu", mainMenuPanel);

		playerPanel = new PlayerPanel();
		tabbedPane.addTab("Players", playerPanel);

		gamesPanel = new GamesPanel();
		tabbedPane.addTab("Games", gamesPanel);

		networkPanel = new NetworkPanel();
		tabbedPane.addTab("Network", networkPanel);

		logPanel = new LogPanel();
		tabbedPane.addTab("Log", logPanel);

		settingsPanel = new SettingsPanel();
		tabbedPane.addTab("Settings", settingsPanel);

		vertBox.add(tabbedPane);
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setLayout(new FlowLayout());
		statusLabel = new JLabel("--- IDLE ---");
		IO.register(this, IO.LEVEL_NORMAL, false);
		IO.register(this, IO.LEVEL_ERROR, false);
		IO.createIOChannel("main-window");
		IO.registerOnIOChannel("main-window", this);
		statusPanel.add(statusLabel);
		vertBox.add(statusPanel);
		add(vertBox);

		pack();

		setResizable(false);

		// Zentrieren
		Dimension screen = GraphicTools.getScreenDimension();
		setLocation(screen.width / 2 - getWidth() / 2, screen.height / 2 - getHeight() / 2);

		setVisible(true);
	}

	// ------------------------------------------------------------

	@Override
	public void windowOpened(WindowEvent e) {
		IO.debugln("Main window opened @ MainWindow.windowOpened");
	}

	@Override
	public void windowClosing(WindowEvent e) {
		IO.debugln("Main window closing. Closing game @ MainWindow.windowClosing");
		Start.exit();
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void output(String s) {
		statusLabel.setText(s);
		lastIdle = System.currentTimeMillis();
	}
}
