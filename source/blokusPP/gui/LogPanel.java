package blokusPP.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.io.Outputable;

/**
 * Dieses Panel zeigt die Log-Ausgabe des Programms an
 * 
 * @author Dominick Leppich
 *
 */
public class LogPanel extends JPanel implements Outputable {
	private static final long serialVersionUID = 1L;
	
	// ------------------------------------------------------------
	
	private JTextArea logArea;

	// ------------------------------------------------------------

	/**
	 * Erzeuge ein Log-Panel
	 */
	public LogPanel() {
		logArea = new JTextArea(30, 50);
		JScrollPane scrollPane = new JScrollPane(logArea);
//		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		logArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)logArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
//		logArea.setAutoscrolls(true);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane);
		
		IO.register(this, IO.LEVEL_DEBUG, true);
	}

	// ------------------------------------------------------------

	/**
	 * Mache Ausgabe
	 * 
	 * @param s
	 *            Ausgabe
	 */
	@Override
	public void output(String s) {
		logArea.append(s);
//		logArea.setCaretPosition(logArea.getCaretPosition() + s.length());
	}
}
