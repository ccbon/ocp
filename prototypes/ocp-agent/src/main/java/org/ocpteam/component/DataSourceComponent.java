package org.ocpteam.component;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;

public class DataSourceComponent implements IComponent {

	public DataSource ds;
	@Override
	public void setParent(IContainer parent) {
		ds = (DataSource) parent;
	}

	@Override
	public IContainer getParent() {
		return ds;
	}
	
	public DataSource getDataSource() {
		return ds;
	}

}
