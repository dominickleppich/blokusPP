package blokusPP.graphic;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import eu.nepster.toolkit.gfx.objects.GraphicObject;

public class GraphicPlayerTime extends GraphicObject implements GraphicConstants {
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------

	private int width, height;
	private double progress;

	// ------------------------------------------------------------

	/**
	 * Erzeuge neue Spieler Zeit Anzeige
	 */
	public GraphicPlayerTime(int width, int height) {
		this.width = width;
		this.height = height;
		progress = 0.0;
	}

	// ------------------------------------------------------------

	/**
	 * Setze den Fortschritt
	 * 
	 * @param progress
	 */
	public void setProgress(double progress) {
		this.progress = progress;
	}

	// ------------------------------------------------------------

	/**
	 * Es gibt nichts zu aktualisieren
	 */
	@Override
	public void tick() {

	}

	@Override
	public void paint(Graphics2D g) {
		g.setColor(PLAYER_FULL_TIME_BAR_BORDER);
		g.drawRect(-width / 2 - 2, - height / 2 - 2, width + 3, height + 3);
		Shape s = new Rectangle2D.Double(-width / 2, -height / 2, width * progress, height);
		g.setPaint(new GradientPaint(0, 0, PLAYER_FULL_TIME_BAR_1, 0, height, PLAYER_FULL_TIME_BAR_2));
		g.fill(s);
	}
}
