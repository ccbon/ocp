package org.ocpteam.component;

import java.net.URI;

import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.User;

public class UserManagement extends DSContainer<DataSource> implements
		IUserManagement {

	protected String username;
	
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
					ds().getComponent(IAuthenticable.class).setChallenge(array[1]);
				}
			}
		}
	}

	@Override
	public boolean canAutomaticallyLogin() {
		if (ds().usesComponent(IAuthenticable.class)) {
			IAuthenticable a = ds().getComponent(IAuthenticable.class);
			return !JLG.isNullOrEmpty(this.username) && a.getChallenge() != null;
		}
		return !JLG.isNullOrEmpty(this.username);
	}

	@Override
	public void login() throws Exception {
		if (!canAutomaticallyLogin()) {
			throw new Exception("Not enought information for log in.");
		}

		if (ds().usesComponent(IAuthenticable.class)) {
			IAuthenticable auth = ds().getComponent(IAuthenticable.class);
			auth.authenticate();
		} else {
			User user = new User();
			user.setUsername(getUsername());
			Context c = new Context(user, ds().getComponent(IDataModel.class));
			ds().setContext(c);
		}
	}

	@Override
	public void logout() throws Exception {
		if (ds().usesComponent(IAuthenticable.class)) {
			IAuthenticable auth = ds().getComponent(IAuthenticable.class);
			auth.setChallenge(null);
			auth.unauthenticate();
		}
		this.username = null;
		ds().setContext(null);
	}

}
