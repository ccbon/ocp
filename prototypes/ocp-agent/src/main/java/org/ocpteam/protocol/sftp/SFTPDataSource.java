package org.ocpteam.protocol.sftp;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.DataSource;

public class SFTPDataSource extends DataSource {

	public SFTPDataSource() throws Exception {
		super();
		getDesigner().add(Client.class, new SFTPClient());
		getDesigner().add(Authentication.class);
	}

	@Override
	public String getProtocol() {
		return "SFTP";
	}

}
