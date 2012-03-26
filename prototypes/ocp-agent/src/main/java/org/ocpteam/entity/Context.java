package org.ocpteam.entity;

import org.ocpteam.interfaces.IDataModel;

public class Context {

	private IDataModel dataModel;

	public Context(IDataModel dataModel, String location) {
		this.dataModel = dataModel;
	}

	public Context(IDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public IDataModel getDataModel() {
		return dataModel;
	}

}
