package org.ocpteam.layer.rsp;

import org.ocpteam.interfaces.IDataModel;

public class Context {

	public IDataModel dataModel;

	public Context(IDataModel dataModel, String location) {
		this.dataModel = dataModel;
	}

}
