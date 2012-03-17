package org.ocpteam.protocol.sftp;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.PropertiesDataSource;

public class SFTPDataSource extends PropertiesDataSource {

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
