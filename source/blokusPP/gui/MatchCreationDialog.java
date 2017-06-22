package blokusPP.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import blokusPP.Start;
import blokusPP.player.PlayerObject;
import blokusPP.preset.Setting;
import eu.nepster.toolkit.gfx.GraphicTools;

/**
 * Dieser Dialog erstellt alle gew&uuml;nschten Spiele zwischen den Spielern
 * 
 * @author Dominick Leppich
 *
 */
public class MatchCreationDialog extends JDialog implements Setting {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	private JComboBox<String>[] colorSelection;

	// ----------------------------------------------------------

	/**
	 * Default Konstruktor
	 */
	@SuppressWarnings("unchecked")
	public MatchCreationDialog(final ArrayList<PlayerObject> players) {
		final MatchCreationDialog selfReference = this;
		
		colorSelection = new JComboBox[GAME_COLORS];

		ArrayList<String> playerNames = new ArrayList<String>();
		for (PlayerObject player : players)
			playerNames.add(player.getName());
		String[] playerNamesArray = new String[playerNames.size()];
		playerNames.toArray(playerNamesArray);

		Box vBox = Box.createVerticalBox();
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(4, 2, 5, 5));

		// Jede Farbe eine Zeile
		for (int i = 0; i < GAME_COLORS; i++) {
			JLabel colorLabel = new JLabel(colorString[i] + ":");
			p1.add(colorLabel);

			colorSelection[i] = new JComboBox<String>(playerNamesArray);
			colorSelection[i].setSelectedIndex(0);
			p1.add(colorSelection[i]);
		}
		vBox.add(p1);
		vBox.add(Box.createVerticalStrut(5));
		
		Box hBox2 = Box.createHorizontalBox();
		hBox2.add(Box.createHorizontalGlue());
		JButton createButton = new JButton("Create Game");
		createButton.addActionListener(new ActionListener() {
			/**
			 * Spiel erstellen
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<PlayerObject> playersInGame = new ArrayList<PlayerObject>();
				playersInGame.add(players.get(colorSelection[BLUE].getSelectedIndex()));
				for (int i = 1; i < GAME_COLORS; i++)
					if (!playersInGame.contains(players.get(colorSelection[i].getSelectedIndex())))
						playersInGame.add(players.get(colorSelection[i].getSelectedIndex()));
				String gameName = playersInGame.get(0).getName();
				for (int i = 1; i < playersInGame.size(); i++)
					gameName += " vs. " + playersInGame.get(i).getName();
				
				System.out.println(players.size());
				
				Start.addMatch(gameName, players.get(colorSelection[BLUE].getSelectedIndex()),
						players.get(colorSelection[YELLOW].getSelectedIndex()),
						players.get(colorSelection[RED].getSelectedIndex()),
						players.get(colorSelection[GREEN].getSelectedIndex()));
				
				selfReference.dispose();
			}
		});
		hBox2.add(createButton);
		hBox2.add(Box.createHorizontalGlue());
		vBox.add(hBox2);
		
		add(vBox);

		pack();

		Dimension screen = GraphicTools.getScreenDimension();
		setLocation((screen.width + getWidth()) / 2, (screen.height + getHeight()) / 2);
	}
}
