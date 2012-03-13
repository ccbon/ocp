package org.ocpteam.functionality;

import java.net.URI;

import org.ocpteam.core.Container;
import org.ocpteam.core.Functionality;
import org.ocpteam.layer.rsp.Authenticable;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

public class Authentication implements Functionality {

	private Object challenge;
	private String login;
	private User user;
	private DataSource ds;

	public Authentication() {
	}

	private void initFromURI() {
		URI uri = null;
		try {
			uri = ds.getURI();
			
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
		Authenticable agent = (Authenticable) ds.getDesigner().get(Agent.class);
		agent.login();
	}

	public void logout() throws Exception {
		Authenticable agent = (Authenticable) ds.getDesigner().get(Agent.class);
		agent.logout();
		reset();
	}

	public boolean canLogin() {
		return !JLG.isNullOrEmpty(this.login) && this.challenge != null;
	}

	@Override
	public void setParent(Container parent) {
		this.ds = (DataSource) parent;
		initFromURI();
	}


}
