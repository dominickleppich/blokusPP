package blokusPP.preset;

public class Position implements java.io.Serializable {
    public Position(int letter, int number) {
	this.letter = letter;
	this.number = number;
    }

    public Position(Position pos) {
	letter = pos.getLetter();
	number = pos.getNumber();
    }

    //----------------------------------------------------------------
    public int getLetter() { return letter; }
    public int getNumber() { return number; }

    public void setLetter(int l) { letter = l; }
    public void setNumber(int n) { number = n; }

    //----------------------------------------------------------------
    public boolean equals(Object obj) {
	if (obj == this)
            return true;
      
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        Position pos = (Position) obj;
	return (letter == pos.getLetter()) && 
               (number == pos.getNumber());
    }  

    public int hashCode() {
        return letter*alphabet.length()+number;
    }

    public String toString() {
	String s = "";
	int l = letter;
        int base = alphabet.length();

	do {
	    s = alphabet.charAt(l%base) + s;
	    l = (l - l%base)/base;
	} while(l > 0);

	return s + (number+1);
    }

    // static --------------------------------------------------------
    public static String getAlphabet() {
	return alphabet;
    }

    // private -------------------------------------------------------
    private int letter;
    private int number;

    // private static ------------------------------------------------
    private static final long serialVersionUID = 2L;
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
}
