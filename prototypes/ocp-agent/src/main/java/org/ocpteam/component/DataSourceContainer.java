package org.ocpteam.component;

import org.ocpteam.core.Container;
import org.ocpteam.core.IContainer;

/**
 * A DataSourceComponent is a Component that needs a DataSource to run.
 *
 */
public class DataSourceContainer extends Container {

	public DataSource ds() {
		return (DataSource) getRoot();
	}

	@Override
	public void setParent(IContainer parent) {
		super.setParent(parent);
	}
	
}
