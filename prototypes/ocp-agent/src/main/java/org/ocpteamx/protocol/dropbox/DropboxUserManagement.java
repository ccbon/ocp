package org.ocpteamx.protocol.dropbox;

import java.io.File;

import org.ocpteam.component.UserManagement;
import org.ocpteam.interfaces.IAuthenticable;

public class DropboxUserManagement extends UserManagement {
	

	@Override
	public boolean canAutomaticallyLogin() {
		File tokens = new File(DropboxClient.TOKEN_FILE);
		return tokens.exists();
	}

	@Override
	public void login() throws Exception {
		DropboxClient c = (DropboxClient) ds().getComponent(
				IAuthenticable.class);
		c.login();
	}
}
