package org.ocpteam.entity;

import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUser;

public class Context {

	private IDataModel dataModel;
	private IUser user;

	public Context(IDataModel dataModel, String location) {
		this.dataModel = dataModel;
	}

	public Context(IDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public Context(IUser user, IDataModel dataModel, String string) {
		this.user = user;
		this.dataModel = dataModel;
	}

	public Context(IUser user, IDataModel dataModel) {
		this.user = user;
		this.dataModel = dataModel;
	}

	public IDataModel getDataModel() {
		return dataModel;
	}
	
	public IUser getUser() {
		return user;
	}

}
