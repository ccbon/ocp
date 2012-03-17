package org.ocpteam.component;

public interface IStartable {
	void start() throws Exception;
	void stop() throws Exception;
	boolean isStarted();
}
