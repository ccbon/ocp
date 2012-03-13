package org.ocpteam.core;


public class Application implements Container {
	
	private Designer designer;
	
	public Application() {
		designer = new Designer(this);
	}

	@Override
	public Designer getDesigner() {
		return designer;
	}

	


}
