package blokusPP.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.settings.Settings;

/**
 * Dieses Panel zeigt die Spieleinstellungen
 * 
 * @author Dominick
 *
 */
public class SettingsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	/**
	 * Default-Konstruktor
	 */
	public SettingsPanel() {
		Box vBox = Box.createVerticalBox();

		Box hBox1 = Box.createHorizontalBox();
		final JCheckBox saveGameCheckBox = new JCheckBox("Save games", Settings.CFG.is("save"));
		saveGameCheckBox.addActionListener(new ActionListener() {
			/**
			 * Klick auf Checkbox
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.CFG.set("save", saveGameCheckBox.isSelected());
				IO.print("main-window", "Game save setting changed");
			}
		});
		hBox1.add(saveGameCheckBox);
		vBox.add(hBox1);

		Box hBox3 = Box.createHorizontalBox();
		JLabel label3 = new JLabel("Min Timeout:");
		hBox3.add(label3);
		hBox3.add(Box.createHorizontalStrut(5));
		final JTextField minTimeoutField = new JTextField(30);
		minTimeoutField.setText(Integer.toString(Settings.CFG.getInt("min-timeout")));
		minTimeoutField.addActionListener(new ActionListener() {
			/**
			 * Klick auf Checkbox
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Settings.CFG.set("min-timeout", Integer.parseInt(minTimeoutField.getText()));
					IO.print("main-window", "New min timeout set to " + Integer.parseInt(minTimeoutField.getText()));
				} catch (NumberFormatException e2) {
				}
			}
		});
		hBox3.add(minTimeoutField);
		vBox.add(hBox3);
		
		Box hBox4 = Box.createHorizontalBox();
		JLabel label4 = new JLabel("Game Timeout:");
		hBox4.add(label4);
		hBox4.add(Box.createHorizontalStrut(5));
		final JTextField gameTimeoutField = new JTextField(30);
		gameTimeoutField.setText(Integer.toString(Settings.CFG.getInt("game-timeout")));
		gameTimeoutField.addActionListener(new ActionListener() {
			/**
			 * Klick auf Checkbox
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Settings.CFG.set("game-timeout", Integer.parseInt(gameTimeoutField.getText()));
					IO.print("main-window", "New game timeout set to " + Integer.parseInt(gameTimeoutField.getText()));
				} catch (NumberFormatException e2) {
				}
			}
		});
		hBox4.add(gameTimeoutField);
		vBox.add(hBox4);
		
		Box hBox5 = Box.createHorizontalBox();
		JLabel label5 = new JLabel("Max player time:");
		hBox5.add(label5);
		hBox5.add(Box.createHorizontalStrut(5));
		final JTextField maxPlayerTime = new JTextField(30);
		maxPlayerTime.setText(Long.toString(Settings.CFG.getLong("max-player-time")));
		maxPlayerTime.addActionListener(new ActionListener() {
			/**
			 * Klick auf Checkbox
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Settings.CFG.set("max-player-time", Long.parseLong(maxPlayerTime.getText()));
					IO.print("main-window", "New max player time set to " + Long.parseLong(maxPlayerTime.getText()));
				} catch (NumberFormatException e2) {
				}
			}
		});
		hBox5.add(maxPlayerTime);
		vBox.add(hBox5);
		
		Box hBox6 = Box.createHorizontalBox();
		JLabel label6 = new JLabel("Max move time:");
		hBox6.add(label6);
		hBox6.add(Box.createHorizontalStrut(5));
		final JTextField maxMoveTime = new JTextField(30);
		maxMoveTime.setText(Long.toString(Settings.CFG.getLong("max-move-time")));
		maxMoveTime.addActionListener(new ActionListener() {
			/**
			 * Klick auf Checkbox
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Settings.CFG.set("max-move-time", Long.parseLong(maxMoveTime.getText()));
					IO.print("main-window", "New max move time set to " + Long.parseLong(maxMoveTime.getText()));
				} catch (NumberFormatException e2) {
				}
			}
		});
		hBox6.add(maxMoveTime);
		vBox.add(hBox6);

		add(vBox);
	}
}
