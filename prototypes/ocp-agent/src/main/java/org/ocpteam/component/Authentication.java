package org.ocpteam.component;

import java.net.URI;

import org.ocpteam.entity.User;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.misc.JLG;

public class Authentication extends DataSourceContainer {

	private Object challenge;
	private String login;
	private User user;

	public void initFromURI() {
		URI uri = null;
		try {
			uri = ds().getURI();
			
		} catch (Exception e) {
		}

		if (uri != null && uri.getUserInfo() != null) {
			String[] array = uri.getUserInfo().split(":");
			String login = array[0];
			this.setLogin(login);
			if (array.length > 1) {
				this.setChallenge(array[1]);
			}
		}
	}

	public Authentication(DataSource ds, String username, Object challenge) {
		this.login = username;
		this.challenge = challenge;
	}

	public Authentication() {
	}

	public Object getChallenge() {
		return challenge;
	}

	public String getLogin() {
		return login;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return this.user;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setChallenge(Object challenge) {
		this.challenge = challenge;

	}

	public void reset() {
		this.user = null;
		this.login = null;
		this.challenge = null;
	}

	/**
	 * To be overridden if necessary according protocol rules.
	 * 
	 * @return true if the protocol allow user creation, false otherwise
	 */
	public boolean allowsUserCreation() {
		return false;
	}

	public void login() throws Exception {
		IAuthenticable client = (IAuthenticable) ds().getComponent(Client.class);
		client.login();
	}

	public void logout() throws Exception {
		IAuthenticable client = (IAuthenticable) ds().getComponent(Client.class);
		client.logout();
		reset();
	}

	public boolean canLogin() {
		return !JLG.isNullOrEmpty(this.login) && this.challenge != null;
	}

}
