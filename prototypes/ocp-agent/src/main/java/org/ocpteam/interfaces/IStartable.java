package org.ocpteam.interfaces;

public interface IStartable {
	void start() throws Exception;
	void stop() throws Exception;
	boolean isStarted();
}
