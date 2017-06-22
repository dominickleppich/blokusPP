package blokusPP.preset;

public class Status implements Setting, java.io.Serializable {

    public Status(int s) { 
	score = new int[GAME_COLORS];
	setScoreAll(s);
    }

    //----------------------------------------------------------------
    public Status(Status stat) {
	score = new int[GAME_COLORS];
	for(int i = 0; i < GAME_COLORS; i++)
	    score[i] = stat.score[i];
    }

    //----------------------------------------------------------------
    public int getScore(int color) {
	if (color >= 0 && color < GAME_COLORS)
	    return score[color];

	return UNDEFINED;
    }

    public void setScore(int color, int s) {
	if (color >= 0 && color < GAME_COLORS)
	    score[color] = s;
    }

    public void setScoreAll(int s) {
	for(int i = 0; i < GAME_COLORS; i++)
	    score[i] = s;
    }

    public boolean isGameOver() {
	boolean noneOK = true;
	for(int i = 0; i < GAME_COLORS; i++) {
	    if (score[i] == OK)
		noneOK = false;
	    else if (score[i] != PAUSE)
		return true;
	}
	return noneOK;
    }

    //----------------------------------------------------------------
    public boolean equals(Object obj) {
	if (obj == this)
            return true;
      
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        Status stat = (Status) obj;

	boolean b = true;
	for(int i = 0; i < GAME_COLORS; i++)
	    b = b && (score[i] == stat.getScore(i));
	    
	return b;
    }

    public int hashCode() {
	int hash = 0;
	for(int i = 0; i < GAME_COLORS; i++)
	    hash = hash * scoreString.length + score[i];

	return hash;
    }

    public String toString() {
	String s = "";

	for(int i = 0; i < GAME_COLORS; i++) {
	    if (s.length() != 0)
		s += " ";
	    s += colorString[i] + ":";
	    if (score[i] < scoreString.length)
		s += scoreString[score[i]];
	    else	    
		s += "undefined("+ score[i] +")";
	}
	return s;
    }

    // private -------------------------------------------------------
    private int[] score;

    // private static ------------------------------------------------
    private static final long serialVersionUID = 1L;
}
