package blokusPP.graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;

import eu.nepster.toolkit.gfx.objects.GraphicObject;
import eu.nepster.toolkit.io.Outputable;

/**
 * Grafische Debug-Ausgabe
 * 
 * @author Dominick Leppich
 *
 */
public class GraphicDebug extends GraphicObject implements Outputable {
	private static final long serialVersionUID = 1L;
	private static final int DEBUG_MAX_LINES = 50;
	private static final int DEBUG_FONT_SIZE = 12;
	private static final Font DEBUG_FONT = new Font(Font.MONOSPACED, Font.PLAIN, DEBUG_FONT_SIZE);
	private static final Color DEBUG_COLOR = Color.YELLOW;
	private static final int DEBUG_TICKS_TO_SHOW = 120;
	private static final float DEBUG_START_X = 5.0f;
	private static final float DEBUG_START_Y = 15.0f;

	private LinkedList<String> debugLog;
	private int tickCount = 0;

	// ------------------------------------------------------------

	public GraphicDebug() {
		debugLog = new LinkedList<String>();
	}

	// ------------------------------------------------------------

	/**
	 * Schreibe die Debug-Ausgabe
	 * 
	 * @param g
	 *            Graphics2D Kontext
	 */
	@Override
	public synchronized void paint(Graphics2D g) {
		if (tickCount <= 0)
			return;

		g.setFont(DEBUG_FONT);
		g.setColor(DEBUG_COLOR);

		int count = 0;
		for (String s : debugLog)
			g.drawString(s, DEBUG_START_X, DEBUG_START_Y + count++ * DEBUG_FONT_SIZE);
	}

	/**
	 * K&uuml;rze Zeit, die die Debug-Ausgabe angezeigt wird
	 */
	@Override
	public void tick() {
		if (tickCount > 0)
			tickCount--;
	}

	// ------------------------------------------------------------

	/**
	 * Mache neue Debug-Ausgabe
	 * 
	 * @param s
	 *            Debug String
	 */
	@Override
	public synchronized void output(String s) {
		if (s.contains("\n")) {
			for (String s2 : s.split("\n"))
				addLine(s2);
		} else
			addLine(s);
		tickCount = DEBUG_TICKS_TO_SHOW;
	}

	/**
	 * F&uuml;ge eine Zeile Debug-Ausgabe hinzu
	 * 
	 * @param line
	 *            Zeile
	 */
	private void addLine(String line) {
		debugLog.addLast(line);
		if (debugLog.size() > DEBUG_MAX_LINES)
			debugLog.removeFirst();
	}
}
