package org.ocpteam.core;

import org.ocpteam.design.Designer;

public class Application {
	
	public Application() {
		designer = new Designer<Application>(this);
	}

	public Designer<Application> designer;


}
