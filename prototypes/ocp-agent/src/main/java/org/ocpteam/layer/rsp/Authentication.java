package org.ocpteam.layer.rsp;

import java.net.URI;


public class Authentication {

	private Object challenge;
	private String login;
	private User user;

	public Authentication(URI uri) {
		if (uri != null && uri.getUserInfo() != null) {
			String[] array = uri.getUserInfo().split(":");
			String login = array[0];
			this.setLogin(login);
			if (array.length > 1) {
				this.setChallenge(array[1]);
			}
		}
	}

	public Authentication(String username, Object challenge) {
		this.login = username;
		this.challenge = challenge;
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

	public boolean allowsUserCreation() {
		return false;
	}

}
