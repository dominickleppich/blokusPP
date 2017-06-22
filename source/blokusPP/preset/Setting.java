package blokusPP.preset;

public interface Setting {
    // color =========================================================
    // game ----------------------------------------------------------
    int GAME_COLORS = 4;

    int BLUE        = 0;
    int YELLOW      = 1;
    int RED         = 2;
    int GREEN       = 3;

    // extra ---------------------------------------------------------
    int NONE        = 4;   // empty square
    int WHITE       = 5;   // for special duty
    int GRAY        = 6;   // for special duty
    int BLACK       = 7;   // for special duty

    String[] colorString = {
	"BLUE", "YELLOW", "RED", "GREEN",
	"NONE",  
	"WHITE", "GRAY", "BLACK"
    };

    // status ========================================================
    int OK        = 0;
    int PAUSE     = 1;
    int FINISH    = 2;
    int ILLEGAL   = 3;
    int ERROR     = 4;
    int UNDEFINED = 99;

    public static final String[] scoreString = {
	"ok", "pause", "finish", "illegal", "error"
    };
}
