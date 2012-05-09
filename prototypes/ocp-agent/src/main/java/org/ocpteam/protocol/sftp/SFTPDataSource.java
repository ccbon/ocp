package org.ocpteam.protocol.sftp;

import org.ocpteam.component.DataSource;
import org.ocpteam.component.UserManagement;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IUserManagement;

public class SFTPDataSource extends DataSource {

	public SFTPDataSource() throws Exception {
		super();
		addComponent(IAuthenticable.class, new SFTPClient());
		addComponent(IUserManagement.class, new UserManagement());
	}

	@Override
	public String getProtocolName() {
		return "SFTP";
	}

}
