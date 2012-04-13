package org.ocpteam.protocol.sftp;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.DataSource;

public class SFTPDataSource extends DataSource {

	public SFTPDataSource() throws Exception {
		super();
		addComponent(Client.class, new SFTPClient());
		addComponent(Authentication.class);
	}

	@Override
	public String getProtocolName() {
		return "SFTP";
	}

}
