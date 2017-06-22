package blokusPP.test;

import blokusPP.board.Board;
import blokusPP.board.Polyomino;
import blokusPP.preset.Position;

public class PolyominoTest {
	public static void main(String[] args) {
		Polyomino p1 = Polyomino.POLY_L;
//		print(p1);
//		print(p1.rotate(Polyomino.ROTATE_90));
//		print(p1.rotate(Polyomino.ROTATE_180));
//		print(p1.rotate(Polyomino.ROTATE_270));

		Polyomino p2 = p1.move(5, 8);
		print(p2);
		print(p2.rotateRight());
		print(p2.rotateLeft());
	}

	// ------------------------------------------------------------

	/**
	 * Zeichne ein Polyomino auf dem Spielbrett
	 * 
	 * @param p
	 *            Polyomino
	 */
	public static void print(Polyomino p) {
		System.out.println("\n-------------------------------------------");
		try {
			System.out.println(p);
		} catch (RuntimeException e) {
			System.out.println("Can's print Polyomino!");
		}
		System.out.println("Breite = " + p.getWidth() + ", Hoehe = " + p.getHeight() + ", minLetter = "
				+ p.getMinLetter() + ", maxLetter = " + p.getMaxLetter() + ", minNumber = " + p.getMinNumber()
				+ ", maxNumber = " + p.getMaxNumber());

		// Erzeuge Boolean Board
		boolean board[][] = new boolean[Board.SIZE][Board.SIZE];

		// Setze Polyomino
		for (Position pos : p.getPolyomino())
			board[pos.getLetter()][pos.getNumber()] = true;

		// Zeichne das Board mit dem Stein
		String s = "";
		s += "    A B C D E F G H I J K L M N O P Q R S T\n\n";
		for (int number = 0; number < Board.SIZE; number++) {
			s += ((1 + number) < 10 ? " " : "") + (1 + number) + "  ";
			for (int letter = 0; letter < Board.SIZE; letter++) {
				if (board[letter][number])
					s += "X";
				else
					s += ".";
				s += " ";
			}
			s += "\n";
		}
		System.out.print(s);
		System.out.println("-------------------------------------------\n");
	}
}
