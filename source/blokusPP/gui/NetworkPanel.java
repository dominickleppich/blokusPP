package blokusPP.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import blokusPP.Start;
import blokusPP.net.RMI;
import blokusPP.player.NetworkPlayer;
import blokusPP.player.PlayerObject;
import eu.nepster.toolkit.io.IO;

/**
 * Dieses Panel ist f&uuml;r die Netzwerkfunktionalit&auml;t verantwortlich
 * 
 * @author Dominick Leppich
 *
 */
public class NetworkPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	DefaultListModel<String> localPlayers;

	// ------------------------------------------------------------

	/**
	 * Default Konstruktor
	 */
	public NetworkPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel serverPanel = new JPanel();
		serverPanel.setBorder(BorderFactory.createTitledBorder("RMI Server"));
		Box serverBox = Box.createHorizontalBox();
		JLabel hostNameLabel = new JLabel("Host: ");
		serverBox.add(hostNameLabel);
		serverBox.add(Box.createHorizontalStrut(5));
		final JTextField hostName = new JTextField(20);
		hostName.setText("localhost");
		serverBox.add(hostName);
		serverBox.add(Box.createHorizontalStrut(5));
		JLabel hostPortLabel = new JLabel("Port: ");
		serverBox.add(hostPortLabel);
		serverBox.add(Box.createHorizontalStrut(5));
		final JTextField hostPort = new JTextField(5);
		hostPort.setText("1099");
		serverBox.add(hostPort);
		serverBox.add(Box.createHorizontalStrut(5));
		JButton startLocalRMIButton = new JButton("Start Local RMI");
		serverBox.add(startLocalRMIButton);
		serverPanel.add(serverBox);
		add(serverPanel);

		JPanel findPlayer = new JPanel();
		findPlayer.setBorder(BorderFactory.createTitledBorder("Find players"));

		Box vertBox = Box.createVerticalBox();
		vertBox.add(Box.createVerticalStrut(5));

		final DefaultListModel<String> listModel = new DefaultListModel<String>();
		final JList<String> list = new JList<String>(listModel);
		JScrollPane listPane = new JScrollPane(list);
		listPane.setPreferredSize(new Dimension(450, 150));
		vertBox.add(listPane);

		vertBox.add(Box.createVerticalStrut(5));

		Box bot = Box.createHorizontalBox();
		final JLabel playerCount = new JLabel("Players found: 0");
		bot.add(playerCount);
		bot.add(Box.createHorizontalGlue());
		bot.add(Box.createHorizontalStrut(5));
		final JButton hostSearchButton = new JButton("Scan");
		bot.add(hostSearchButton);
		bot.add(Box.createHorizontalStrut(5));
		final JButton addPlayerButton = new JButton("Add");
		bot.add(addPlayerButton);
		bot.add(Box.createHorizontalStrut(5));
		JButton unbindButton = new JButton("Remove");
		bot.add(unbindButton);
		vertBox.add(bot);

		findPlayer.add(vertBox);

		startLocalRMIButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RMI.startRegistry(Integer.parseInt(hostPort.getText()));
			}
		});

		hostSearchButton.addActionListener(new ActionListener() {
			// Finde Spieler
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] players = RMI.listPlayers(hostName.getText(), Integer.parseInt(hostPort.getText()));
				listModel.clear();
				for (String s : players)
					listModel.addElement(s);
				playerCount.setText("Players found: " + listModel.getSize());
			}
		});

		addPlayerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selection = list.getSelectedIndices();
				if (selection.length == 0)
					return;
				for (int i = 0; i < selection.length; i++) {
					Start.addPlayer(RMI.getPlayer(hostName.getText(), Integer.parseInt(hostPort.getText()),
							listModel.getElementAt(selection[i])), listModel.getElementAt(selection[i]));
				}
			}

		});

		unbindButton.addActionListener(new ActionListener() {
			// Entferne Spieler aus der RMI
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selection = list.getSelectedIndices();
				if (selection.length == 0)
					return;
				for (int i = 0; i < selection.length; i++)
					try {
						RMI.unregisterPlayer(hostName.getText(), Integer.parseInt(hostPort.getText()),
								listModel.getElementAt(selection[i]));
					} catch (NumberFormatException e1) {
						IO.errorln("Port " + hostPort.getText() + " is not a valid port number @ NetworkPanel");
						// e1.printStackTrace();
					}
				hostSearchButton.doClick();
			}
		});

		add(findPlayer);

		JPanel offerPlayer = new JPanel();
		offerPlayer.setBorder(BorderFactory.createTitledBorder("Offer players"));

		Box vertBox2 = Box.createVerticalBox();
		localPlayers = new DefaultListModel<String>();
		final JList<String> list2 = new JList<String>(localPlayers);
		JScrollPane listPane2 = new JScrollPane(list2);
		listPane2.setPreferredSize(new Dimension(450, 150));
		vertBox2.add(listPane2);

		vertBox2.add(Box.createVerticalStrut(5));

		Box bot2 = Box.createHorizontalBox();
		JButton playerOfferButton = new JButton("Offer");
		bot2.add(playerOfferButton);
		bot2.add(Box.createGlue());
		vertBox2.add(bot2);

		offerPlayer.add(vertBox2);

		playerOfferButton.addActionListener(new ActionListener() {
			// Finde Spieler
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selection = list2.getSelectedIndices();
				if (selection.length == 0)
					return;
				for (int i = 0; i < selection.length; i++)
					try {
						RMI.registerPlayer(hostName.getText(), Integer.parseInt(hostPort.getText()),
								new NetworkPlayer(Start.players.get(selection[i]).getPlayer()),
								Start.players.get(selection[i]).getName());
					} catch (NumberFormatException e1) {
						IO.errorln("Port " + hostPort.getText() + " is not a valid port number @ NetworkPanel");
						// e1.printStackTrace();
					} catch (RemoteException e1) {
						IO.errorln("Error offering player @ NetworkPanel");
						// e1.printStackTrace();
					}
				hostSearchButton.doClick();
			}
		});

		add(offerPlayer);
	}

	// ------------------------------------------------------------

	/**
	 * Aktualisiere lokale Spieler
	 */
	public void refreshLocalPlayerList() {
		localPlayers.clear();

		for (PlayerObject player : Start.players)
			localPlayers.addElement(player.getName());
	}
}
