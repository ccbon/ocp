package org.ocpteam.layer.rsp;

import org.ocpteam.component.DataModel;

public class Context {

	public DataModel dataModel;

	public Context(DataModel dataModel, String location) {
		this.dataModel = dataModel;
	}

}
