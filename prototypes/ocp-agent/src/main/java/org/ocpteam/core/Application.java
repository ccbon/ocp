package org.ocpteam.core;

import org.ocpteam.design.Container;
import org.ocpteam.design.Designer;

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
