package blokusPP.graphic;

import java.awt.Color;

import eu.nepster.toolkit.gfx.GraphicLoader;

/**
 * Konstanten f&uuml;r die Grafik
 * 
 * @author Dominick Leppich
 *
 */
public interface GraphicConstants {
	// Wie viel Platz darf das Board maximal in Breite und Hoehe einnehmen
	public static final double MAX_BOARD_WIDTH_HEIGHT_PERCENTAGE = 0.9;

	// Board
	public static final int FIELD_WIDTH = GraphicLoader.GFX.get("field.none")[0].getWidth();
	public static final int FIELD_HEIGHT = GraphicLoader.GFX.get("field.none")[0].getHeight();
	public static final int BORDER_WIDTH = GraphicLoader.GFX.get("border.left")[0].getWidth();
	public static final int BORDER_HEIGHT = GraphicLoader.GFX.get("border.top")[0].getHeight();
	public static final int BOARD_WIDTH = 2 * BORDER_WIDTH + 20 * FIELD_WIDTH;
	public static final int BOARD_HEIGHT = 2 * BORDER_HEIGHT + 20 * FIELD_HEIGHT;
	
	// GraphicPolyomino
	public static final float GRAPHIC_POLYOMINO_OVERLAY_ALPHA = 0.8f;
	public static final int GRAPHIC_POLYOMINO_WIDTH = 5 * FIELD_WIDTH;
	public static final int GRAPHIC_POLYOMINO_HEIGHT = 5 * FIELD_HEIGHT;
	public static final double GRAPHIC_POLYOMINO_PERCENTAGE = 0.6;
	public static final int HIGHLIGHT_STEP = 40;
	public static final int FLIP_STEP = 20;
	public static final int ROTATE_STEP = 15;
	
	// GraphicPolyominoBar
	public static final float POLYOMINO_BAR_OVERLAY_ALPHA = 0.8f;
	public static final int MAX_NEIGHBORS = 1;
	public static final float MAX_NEIGHBOR_ALPHA = 0.5f;
	public static final float NEIGHBOR_ALPHA_LOSE = 0.15f;
	public static final int MAX_POLY_WIDTH = 6 * FIELD_WIDTH;
	public static final int MAX_POLY_HEIGHT = 6 * FIELD_HEIGHT;
	public static final int SLIDE_STEP = 15;
	
	// Polyomino Move on Board
	public static final float MAX_POLYOMINO_START_POSITION_ALPHA = 0.75f;
	public static final float POLYOMINO_MOVING_ALPHA = 0.6f;
	public static final float POLYOMINO_MOVING_CORRECT_ALPHA = 0.9f;
	
	public static final float VS_OVERLAY_ALPHA = 0.9f;

	public static final Color BORDER_COLOR = new Color(191, 125, 11);
	public static final Color VS_COLOR_1 = new Color(120, 120, 120);
	public static final Color VS_COLOR_2 = new Color(180, 180, 180);
	public static final Color PLAYER_NAME_NONE = Color.GRAY;
	public static final Color PLAYER_NAME_BLUE = new Color(68, 112, 150);
	public static final Color PLAYER_NAME_YELLOW = new Color(221, 130, 10);
	public static final Color PLAYER_NAME_RED = new Color(137, 29, 5);
	public static final Color PLAYER_NAME_GREEN = new Color(140, 161, 64);
	public static final Color PLAYER_FULL_TIME_BAR_BORDER = Color.WHITE;
	public static final Color PLAYER_FULL_TIME_BAR_1 = Color.WHITE;
	public static final Color PLAYER_FULL_TIME_BAR_2 = Color.GRAY;
	public static final Color PLAYER_TIME_OKAY = Color.GRAY;
	public static final Color PLAYER_TIME_CRITICAL = new Color(137, 29, 5);
	
	public static final float PLAYER_ACTIVE_SCALE = 0.1f;
	public static final int PLAYER_CHANGE_STEPS = 20;
	
	public static final String PLAYER_NAME_FONT = "res/font/Augusta.ttf";
	public static final String VS_FONT = "res/font/stonecross.ttf";
	
	// HTML
	public static final String DIV_BACKGROUND_STASTICS = "#DDDDDD";
	public static final String DIV_BACKGROUND_BLUE = "#CCFFFF";
	public static final String DIV_BACKGROUND_YELLOW = "#FFFFCC";
	public static final String DIV_BACKGROUND_RED = "#FFCCCC";
	public static final String DIV_BACKGROUND_GREEN = "#CCFFCC";
}
