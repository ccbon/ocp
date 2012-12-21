package org.ocpteamx.protocol.dropbox;

import org.ocpteam.component.DataSource;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;

public class DropboxDataSource extends DataSource {
	public IUserManagement um;

	public DropboxDataSource() throws Exception {
		super();
		addComponent(IAuthenticable.class, new DropboxClient());
		addComponent(IDataModel.class, new DropboxFileSystem());
		addComponent(IUserManagement.class, new DropboxUserManagement());

	}

	@Override
	public String getProtocolName() {
		return "Dropbox";
	}

}
