package org.ocpteam.ui.swt;

public interface IScenario {

	public void setWindow(DataSourceWindow w);
	
	public void run() throws Exception;

	public boolean succeeded();

}
