package org.ocpteam.misc;



public class JLGThread extends Thread {
	private Runnable runnable;

	public JLGThread(ThreadGroup tg, Runnable runnable, String name) {
		super(tg, runnable, name);
		this.runnable = runnable;
	}

	public JLGThread(ThreadGroup tg, Runnable runnable) {
		super(tg, runnable);
		this.runnable = runnable;
	}

	public Runnable getRunnable() {
		return runnable;
	}
	
}
