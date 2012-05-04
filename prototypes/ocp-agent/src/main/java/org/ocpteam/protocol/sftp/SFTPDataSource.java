package org.ocpteam.protocol.sftp;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.UserIdentification;
import org.ocpteam.interfaces.IAuthenticable;

public class SFTPDataSource extends DataSource {

	public SFTPDataSource() throws Exception {
		super();
		addComponent(IAuthenticable.class, new SFTPClient());
		addComponent(UserIdentification.class, new Authentication());
	}

	@Override
	public String getProtocolName() {
		return "SFTP";
	}

}
