package org.ocpteam.core;


public class Container implements IContainer {
	
	private Designer<IContainer> designer;
	
	public Container() {
		designer = new Designer<IContainer>(this);
	}

	@Override
	public Designer<IContainer> getDesigner() {
		return designer;
	}

	


}
