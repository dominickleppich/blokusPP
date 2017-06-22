package blokusPP.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import blokusPP.Start;
import blokusPP.game.MatchSetup;
import blokusPP.preset.Setting;
import eu.nepster.toolkit.gfx.GraphicTools;

/**
 * Dieses Panel zeigt ausstehende Spiele an
 * 
 * @author Dominick Leppich
 *
 */
public class GamesPanel extends JPanel implements Setting {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	private JTable gamesTable;
	private DefaultTableModel gamesTableModel;
	private MatchmakingDialog matchmakingDialog;

	private JLabel statusLabel;

	// ------------------------------------------------------------

	/**
	 * Default Konstruktor
	 */
	public GamesPanel() {
		matchmakingDialog = new MatchmakingDialog();

		// Zentrieren
		Dimension screen = GraphicTools.getScreenDimension();
		matchmakingDialog.setLocation(screen.width / 2 - getWidth() / 2, screen.height / 2 - getHeight() / 2);

		Box vertBox = Box.createVerticalBox();

		gamesTableModel = new DefaultTableModel();
		gamesTable = new JTable(gamesTableModel);
		JScrollPane scrollPane = new JScrollPane(gamesTable);
		gamesTable.setFillsViewportHeight(true);
		gamesTableModel.addColumn("Name");
		gamesTableModel.addColumn("Blue Player");
		gamesTableModel.addColumn("Yellow Player");
		gamesTableModel.addColumn("Red Player");
		gamesTableModel.addColumn("Green Player");
		gamesTable.getColumnModel().getColumn(0).setPreferredWidth(1000);
		gamesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
		gamesTable.getColumnModel().getColumn(2).setPreferredWidth(200);
		gamesTable.getColumnModel().getColumn(3).setPreferredWidth(200);
		gamesTable.getColumnModel().getColumn(4).setPreferredWidth(200);
		gamesTable.setRowSelectionAllowed(true);
		vertBox.add(scrollPane);

		vertBox.add(Box.createVerticalStrut(5));

		Box listBox = Box.createHorizontalBox();
		JButton startGamesButton = new JButton("Start All");
		startGamesButton.addActionListener(new ActionListener() {
			/**
			 * Starte die Spiele
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Start.startGamesThread();
			}
		});
		listBox.add(startGamesButton);
		listBox.add(Box.createHorizontalStrut(5));
		JButton startGameButton = new JButton("Start");
		startGameButton.addActionListener(new ActionListener() {
			/**
			 * Starte die Spiele
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Start.startSingleGameThread();
			}
		});
		listBox.add(startGameButton);
		listBox.add(Box.createHorizontalStrut(5));
		JButton stopGamesButton = new JButton("Stop");
		stopGamesButton.addActionListener(new ActionListener() {
			/**
			 * Stoppe die Spiele
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Start.stopGamesThread();
			}
		});
		listBox.add(stopGamesButton);
		listBox.add(Box.createHorizontalGlue());
		JButton shuffleButton = new JButton("Shuffle");
		shuffleButton.addActionListener(new ActionListener() {
			/**
			 * F&uuml;ge ein Spiel hinzu
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Collections.shuffle(Start.matches);
				Start.refreshLists();
			}
		});
		listBox.add(shuffleButton);
		listBox.add(Box.createHorizontalGlue());
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			/**
			 * F&uuml;ge ein Spiel hinzu
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				matchmakingDialog.setVisible(true);
			}
		});
		listBox.add(addButton);
		listBox.add(Box.createHorizontalStrut(5));
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			/**
			 * Entferne ein Spiel
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selection = gamesTable.getSelectedRows();
				if (selection.length == 0)
					return;
				for (int i = selection.length - 1; i >= 0; i--)
					Start.removeMatch(selection[i]);
			}
		});
		listBox.add(removeButton);
		vertBox.add(listBox);
		vertBox.add(Box.createVerticalStrut(5));
		Box listBox2 = Box.createHorizontalBox();
		statusLabel = new JLabel("Active Game: --- IDLE ---");
		listBox2.add(statusLabel);
		listBox2.add(Box.createHorizontalGlue());
		vertBox.add(listBox2);

		add(vertBox);

		refreshList();
	}

	// ------------------------------------------------------------

	/**
	 * Zeige einen Status an
	 * 
	 * @param s
	 *            Status
	 */
	public void showStatus(String s) {
		statusLabel.setText(s);
	}

	// ------------------------------------------------------------

	/**
	 * Aktualisiere die Liste
	 */
	public void refreshList() {
		while (gamesTableModel.getRowCount() > 0)
			gamesTableModel.removeRow(0);

		for (MatchSetup matchSetup : Start.matches)
			gamesTableModel.addRow(new Object[] { matchSetup.getGameName(), matchSetup.getPlayer(BLUE).getName(),
					matchSetup.getPlayer(YELLOW).getName(), matchSetup.getPlayer(RED).getName(),
					matchSetup.getPlayer(GREEN).getName() });

		matchmakingDialog.refreshList();
	}
}
