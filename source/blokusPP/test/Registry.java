package blokusPP.test;

import blokusPP.net.RMI;

public class Registry {
	public static void main(String[] args) {
		RMI.startRegistry(1099);
		while (true)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
