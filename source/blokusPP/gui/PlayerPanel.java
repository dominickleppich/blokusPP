package blokusPP.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import blokusPP.Start;
import blokusPP.player.HumanPlayer;
import blokusPP.player.PlayerObject;
import blokusPP.player.ai.IdleRandomAI;
import blokusPP.player.ai.IllegalAI;
import blokusPP.player.ai.PauseAI;
import blokusPP.player.ai.RandomAI;
import blokusPP.preset.Player;
import eu.nepster.toolkit.io.IO;

/**
 * Dieses Panel verwaltet alle erstellten Spieler. Es k&ouml;nnen beliebige
 * Spieler erzeugt und entfernt werden.
 * 
 * @author Dominick Leppich
 *
 */
public class PlayerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	private JTable playerTable;
	private DefaultTableModel playerTableModel;

	// ------------------------------------------------------------

	/**
	 * Default Konstruktor
	 */
	public PlayerPanel() {
		Box vertBox = Box.createVerticalBox();

		playerTableModel = new DefaultTableModel();
		playerTable = new JTable(playerTableModel);
		JScrollPane scrollPane = new JScrollPane(playerTable);
		playerTable.setFillsViewportHeight(true);
		playerTableModel.addColumn("Name");
		playerTableModel.addColumn("Played");
		playerTableModel.addColumn("Won");
		playerTableModel.addColumn("Lost");
		playerTable.getColumnModel().getColumn(0).setPreferredWidth(250);
		playerTable.getColumnModel().getColumn(1).setPreferredWidth(30);
		playerTable.getColumnModel().getColumn(2).setPreferredWidth(30);
		playerTable.getColumnModel().getColumn(3).setPreferredWidth(30);
		playerTable.setRowSelectionAllowed(true);
		playerTableModel.addTableModelListener(new TableModelListener() {
			/**
			 * Spielername ge&auml;ndert
			 */
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
					for (int i = 0; i < playerTableModel.getRowCount(); i++)
						Start.players.get(i).setName((String) playerTableModel.getValueAt(i, 0));
					Start.refreshLists();
				}
			}
		});
		vertBox.add(scrollPane);

		vertBox.add(Box.createVerticalStrut(5));

		Box listBox = Box.createHorizontalBox();
		JButton createGameButton = new JButton("Create Game");
		createGameButton.addActionListener(new ActionListener() {
			/**
			 * Erzeuge Spiel
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selection = playerTable.getSelectedRows();
				if (selection.length == 0)
					return;
				ArrayList<PlayerObject> players = new ArrayList<PlayerObject>();
				for (int i = 0; i < selection.length; i++)
					players.add(Start.players.get(selection[i]));
				if (players.size() > 1)
					new MatchCreationDialog(players).setVisible(true);
			}
		});
		listBox.add(createGameButton);
		listBox.add(Box.createHorizontalGlue());
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			/**
			 * Entferne Spieler
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selection = playerTable.getSelectedRows();
				if (selection.length == 0)
					return;
				for (int i = selection.length - 1; i >= 0; i--)
					Start.removePlayer(selection[i]);
			}
		});
		listBox.add(removeButton);
		vertBox.add(listBox);

		vertBox.add(Box.createVerticalStrut(5));

		Box newPlayerBox = Box.createHorizontalBox();
		JLabel addPlayerLabel = new JLabel("Add Player:");
		newPlayerBox.add(addPlayerLabel);
		newPlayerBox.add(Box.createHorizontalStrut(5));
		final JTextField playerName = new JTextField(30);
		newPlayerBox.add(playerName);
		newPlayerBox.add(Box.createHorizontalStrut(5));
		String[] playerTypes = { "Human", "Random AI", "Idle Random AI", "Pause AI", "Illegal AI" };
		final JComboBox<String> playerType = new JComboBox<String>(playerTypes);
		newPlayerBox.add(playerType);
		newPlayerBox.add(Box.createHorizontalStrut(5));
		JButton addPlayerButton = new JButton("Add");
		newPlayerBox.add(addPlayerButton);
		addPlayerButton.addActionListener(new ActionListener() {
			/**
			 * F&uuml;ge neuen Spieler hinzu
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (playerName.getText().equals("")) {
					IO.errorln("Player name can't be empty @ PlayerPanel");
					return;
				}
				Player player = null;
				switch (playerType.getSelectedIndex()) {
				case 0:
					player = new HumanPlayer(Start.gui);
					break;
				case 1:
					player = new RandomAI();
					break;
				case 2:
					String s1 = (String) JOptionPane.showInputDialog("Min Timeout: ");
					String s2 = (String) JOptionPane.showInputDialog("Max Timeout: ");
					player = new IdleRandomAI(Integer.parseInt(s1), Integer.parseInt(s2));
					break;
				case 3:
					player = new PauseAI();
					break;
				case 4:
					player = new IllegalAI();
					break;
				}
				Start.addPlayer(player, playerName.getText());
				playerName.setText("");
			}

		});
		vertBox.add(newPlayerBox);

		add(vertBox);

		refreshList();
	}

	// ------------------------------------------------------------

	/**
	 * Aktualisiere die Liste
	 */
	public void refreshList() {
		while (playerTableModel.getRowCount() > 0)
			playerTableModel.removeRow(0);

		for (PlayerObject player : Start.players)
			playerTableModel.addRow(new Object[] { player.getName(), player.getGamesPlayed(), player.getGamesWon(),
					player.getGamesLost() });
	}
}
