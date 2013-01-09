package org.ocpteamx.protocol.gdrive;

import org.ocpteam.component.DataSource;
import org.ocpteam.component.UserManagement;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;

public class GDriveDataSource extends DataSource {
	public IUserManagement um;

	public GDriveDataSource() throws Exception {
		super();
		addComponent(IAuthenticable.class, new GDriveClient());
		addComponent(IDataModel.class, new GDriveFileSystem());
		addComponent(IUserManagement.class, new UserManagement());

	}

	@Override
	public String getProtocolName() {
		return "GDrive";
	}
}
