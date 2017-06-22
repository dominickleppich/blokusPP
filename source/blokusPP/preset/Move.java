package blokusPP.preset;

import java.util.HashSet;

public class Move implements java.io.Serializable {

    public Move() {
	polyomino = new HashSet<Position>();
    }

    public Move(HashSet<Position> poly) {
	this();
	addPolyomino(poly);
    }

    //----------------------------------------------------------------
    public HashSet<Position> getPolyomino() {
	return polyomino;
    }

    public void addPosition(Position pos) {
	polyomino.add(new Position(pos));	
    }

    public void addPolyomino(HashSet<Position> poly) {
	if (poly == null)
	    return;

	for(Position pos : poly)
	    polyomino.add(new Position(pos));	
    }

    //----------------------------------------------------------------
    public boolean equals(Object obj) {
	if (obj == this)
            return true;
      
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        Move mov = (Move) obj;

	return polyomino.equals(mov.polyomino);
    }

    public int hashCode() {
	return polyomino.hashCode();
    }
    
    public String toString() {
	String s = "";
	  
	for(Position pos : polyomino)
	    s += pos;

	return s;
    }

    // private -------------------------------------------------------
    private HashSet<Position> polyomino;

    // private static ------------------------------------------------
    private static final long serialVersionUID = 1L;
    
}
