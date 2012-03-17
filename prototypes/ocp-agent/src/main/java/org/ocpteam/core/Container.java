package org.ocpteam.core;


public class Container implements IContainer {
	
	private Designer designer;
	
	public Container() {
		designer = new Designer(this);
	}

	@Override
	public Designer getDesigner() {
		return designer;
	}

}
