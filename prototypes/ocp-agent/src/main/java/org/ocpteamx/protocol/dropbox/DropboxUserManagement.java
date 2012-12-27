package org.ocpteamx.protocol.dropbox;

import org.ocpteam.component.UserManagement;
import org.ocpteam.interfaces.IAuthenticable;

public class DropboxUserManagement extends UserManagement {

	@Override
	public String getUsername() throws Exception {
		if (username == null) {
			DropboxClient c = (DropboxClient) ds().getComponent(IAuthenticable.class);
			username = c.mDBApi.accountInfo().displayName;
		}
		return username;
	}

	@Override
	public boolean canAutomaticallyLogin() {
		return false;
	}

	@Override
	public void login() throws Exception {
		DropboxClient c = (DropboxClient) ds().getComponent(
				IAuthenticable.class);
		c.authenticate();
	}
}
