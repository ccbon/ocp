package org.ocpteam.component;

import java.net.URI;

import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IUser;

public class Authentication extends UserIdentification {

	private Object challenge;
	private IUser user;

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
			if (array.length > 1) {
				this.setChallenge(array[1]);
			}
		}
	}

	public Object getChallenge() {
		return challenge;
	}
	
	public void setUser(IUser user) {
		this.user = user;
	}

	public IUser getUser() {
		return this.user;
	}

	public void setChallenge(Object challenge) {
		this.challenge = challenge;

	}

	public void reset() {
		this.user = null;
		this.username = null;
		this.challenge = null;
	}

	@Override
	public void login() throws Exception {
		IAuthenticable client = getRoot().getComponent(IAuthenticable.class);
		client.login();
	}

	@Override
	public void logout() throws Exception {
		IAuthenticable client = getRoot().getComponent(IAuthenticable.class);
		client.logout();
		reset();
	}

	@Override
	public boolean canLogin() {
		return super.canLogin() && this.challenge != null;
	}

}
