package blokusPP.test;

import blokusPP.net.RMI;

public class Registered {
	public static void main(String[] args) {
		for (String s : RMI.listPlayers("localhost", 1099))
			System.out.println("Registered Player: " + s);
	}
}
