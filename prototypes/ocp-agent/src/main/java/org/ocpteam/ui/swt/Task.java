package org.ocpteam.ui.swt;

public class Task implements Runnable {

	protected String name;
	protected boolean bIsComplete;
	public long startTime;
	private Runnable r;

	public Task(String name, Runnable r) {
		this.name = name;
		this.r = r;
		bIsComplete = false;
		startTime = -1;
	}

	public boolean isComplete() {
		return bIsComplete;
	}

	@Override
	public void run() {
		setStartTime();
		r.run();
		setComplete(true);
	}

	protected void setStartTime() {
		this.startTime = System.currentTimeMillis();
	}

	public boolean hasStarted() {
		return startTime != -1;
	}

	public void setComplete(boolean b) {
		this.bIsComplete = b;
	}

}
