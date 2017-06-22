package blokusPP.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import blokusPP.Start;
import blokusPP.player.PlayerObject;

/**
 * Dieser Dialog erstellt alle gew&uuml;nschten Spiele zwischen den Spielern
 * 
 * @author Dominick Leppich
 *
 */
public class MatchmakingDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	DefaultListModel<String> localPlayers;
	
	// ----------------------------------------------------------
	
	/**
	 * Default Konstruktor
	 */
	public MatchmakingDialog() {
		final MatchmakingDialog selfReference = this;
		Box hBox = Box.createHorizontalBox();
		
		localPlayers = new DefaultListModel<String>();
		final JList<String> list = new JList<String>(localPlayers);
		JScrollPane listPane = new JScrollPane(list);
		listPane.setPreferredSize(new Dimension(100, 300));
		hBox.add(listPane);
		
		hBox.add(Box.createHorizontalStrut(5));
		
		Box vBox = Box.createVerticalBox();
		
		hBox.add(vBox);
		JButton aiTournament = new JButton("AI Tournament Configuration");
		aiTournament.addActionListener(new ActionListener() {
			/**
			 * Jedes Team spielt gegen jedes andere
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selection = list.getSelectedIndices();
				if (selection.length == 0)
					return;
				ArrayList<PlayerObject> selectedPlayers = new ArrayList<PlayerObject>();
				for (int select : selection)
					selectedPlayers.add(Start.players.get(select));
				
				// Jeder gegen jeden 
				for (PlayerObject player1 : selectedPlayers) {
					for (PlayerObject player2 : selectedPlayers) {
						if (player1.equals(player2))
							continue;
						
						Start.addMatch("" + player1.getName() + " vs. " + player2.getName(), player1, player2, player1, player2);
					}
				}
				selfReference.dispose();
			}
		});
		vBox.add(aiTournament);
		
		hBox.add(Box.createHorizontalStrut(5));
		
		add(hBox);
		
		pack();
	}
	
	// ------------------------------------------------------------
	
	/**
	 * Aktualisiere Spielerliste
	 */
	public void refreshList() {
		localPlayers.clear();

		for (PlayerObject player : Start.players)
			localPlayers.addElement(player.getName());
	}
}
