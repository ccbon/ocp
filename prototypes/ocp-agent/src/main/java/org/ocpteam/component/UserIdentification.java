package org.ocpteam.component;

import java.net.URI;

import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.JLG;

/**
 * Component used to include user functionality to a storage protocol.
 * This do not include authentication.
 *
 */
public class UserIdentification extends DSContainer<DataSource> {

	protected String username;

	public String getUsername() throws Exception {
		if (username == null) {
			throw new Exception("user not identified. Please login...");
		}
		return username;
	}

	public void initFromURI() {
		URI uri = null;
		try {
			uri = ds().getURI();

		} catch (Exception e) {
		}

		if (uri != null && uri.getUserInfo() != null) {
			String[] array = uri.getUserInfo().split(":");
			this.username = array[0];
		}
	}

	public boolean canLogin() {
		return !JLG.isNullOrEmpty(this.username);
	}

	public void login() throws Exception {
		if (!canLogin()) {
			throw new Exception("username not provided.");
		}
		
		// if login succeed, then attach a context to the ds.
		Context c = new Context(ds().getComponent(IDataModel.class));
		ds().setContext(c);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void logout() throws Exception {
		this.username = null;
	}

}
