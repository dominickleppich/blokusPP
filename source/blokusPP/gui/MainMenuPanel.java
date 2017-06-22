package blokusPP.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import blokusPP.Start;
import blokusPP.player.HumanPlayer;
import blokusPP.player.PlayerObject;
import blokusPP.player.ai.RandomAI;

/**
 * Dieses Panel ist das Hauptmen&uuml; des Spiels.
 * 
 * @author Dominick Leppich
 *
 */
public class MainMenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	/**
	 * Default Konstruktor
	 */
	public MainMenuPanel() {
		Box menuBox = Box.createVerticalBox();
		menuBox.add(Box.createVerticalStrut(30));
		JButton humanVsHuman = new JButton("Human vs. Human");
		humanVsHuman.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerObject p1 = new PlayerObject(new HumanPlayer(Start.gui), "Human I");
				PlayerObject p2 = new PlayerObject(new HumanPlayer(Start.gui), "Human II");
				Start.addMatch(0, "Human vs. Human", p1, p2, p1, p2);
				Start.startSingleGameThread();
			}
		});
		menuBox.add(humanVsHuman);
		menuBox.add(Box.createVerticalStrut(5));
		JButton humanVsRandom = new JButton("Human vs. Random");
		humanVsRandom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerObject p1 = new PlayerObject(new HumanPlayer(Start.gui), "Human");
				PlayerObject p2 = new PlayerObject(new RandomAI(), "Random");
				Start.addMatch(0, "Human vs. Random", p1, p2, p1, p2);
				Start.startSingleGameThread();
			}
		});
		menuBox.add(humanVsRandom);
		menuBox.add(Box.createVerticalStrut(5));
		JButton randomVsRandom = new JButton("Random vs. Random");
		randomVsRandom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerObject p1 = new PlayerObject(new RandomAI(), "Random I");
				PlayerObject p2 = new PlayerObject(new RandomAI(), "Random II");
				Start.addMatch(0, "Random vs. Random", p1, p2, p1, p2);
				Start.startSingleGameThread();
			}
		});
		menuBox.add(randomVsRandom);

		menuBox.add(Box.createVerticalStrut(20));

		JButton showGameWindow = new JButton("Show GameWindow");
		showGameWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Start.gui.setVisible(true);
			}
		});
		menuBox.add(showGameWindow);
		menuBox.add(Box.createVerticalStrut(5));

		JButton resetGameWindow = new JButton("Reset GameWindow");
		resetGameWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Start.gui.setPlayerInformation(false);
				Start.gui.reset();
			}
		});
		menuBox.add(resetGameWindow);
		add(menuBox);
	}
}
