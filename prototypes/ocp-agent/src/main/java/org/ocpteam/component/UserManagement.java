package org.ocpteam.component;

import java.net.URI;

import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.JLG;

public class UserManagement extends DSContainer<DataSource> implements
		IUserManagement {

	protected String username;
	private Object challenge;

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getUsername() throws Exception {
		if (username == null) {
			throw new Exception("user not identified. Please login...");
		}
		return username;
	}

	@Override
	public void initFromURI() {
		URI uri = null;
		try {
			uri = ds().getURI();

		} catch (Exception e) {
		}

		if (uri != null && uri.getUserInfo() != null) {
			String[] array = uri.getUserInfo().split(":");
			this.username = array[0];
			if (ds().usesComponent(IAuthenticable.class)) {
				if (array.length > 1) {
					this.setChallenge(array[1]);
				}
			}
		}
	}

	@Override
	public boolean canLogin() {
		if (ds().usesComponent(IAuthenticable.class)) {
			return !JLG.isNullOrEmpty(this.username) && this.challenge != null;
		}
		return !JLG.isNullOrEmpty(this.username);
	}

	@Override
	public void login() throws Exception {
		if (!canLogin()) {
			throw new Exception("username not provided.");
		}

		if (ds().usesComponent(IAuthenticable.class)) {
			IAuthenticable auth = ds().getComponent(IAuthenticable.class);
			auth.login();
		} else {
			Context c = new Context(ds().getComponent(IDataModel.class));
			ds().setContext(c);
		}
	}

	@Override
	public void logout() throws Exception {
		if (ds().usesComponent(IAuthenticable.class)) {
			IAuthenticable auth = ds().getComponent(IAuthenticable.class);
			auth.logout();
		}
		this.username = null;
		this.challenge = null;
	}

	@Override
	public void setChallenge(Object challenge) {
		this.challenge = challenge;
	}

	@Override
	public Object getChallenge() {
		return challenge;
	}

}
