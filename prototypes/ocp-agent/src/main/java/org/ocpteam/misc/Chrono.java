package org.ocpteam.misc;

public class Chrono {

	private int startTime;

	public void top() {
		this.startTime = (int) System.currentTimeMillis();
	}

	public int read() {
		return (int) (System.currentTimeMillis() - startTime);
	}

}
