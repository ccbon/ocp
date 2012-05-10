package org.ocpteamx.protocol.ftp;

import java.net.URI;

import org.ocpteam.component.DataSource;
import org.ocpteam.component.UserManagement;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;

public class FTPDataSource extends DataSource {

	public IUserManagement um;

	public FTPDataSource() throws Exception {
		super();
		addComponent(IAuthenticable.class, new FTPClient());
		addComponent(IDataModel.class, new FTPFileSystem());
		addComponent(IUserManagement.class, new UserManagement());
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		um = getComponent(IUserManagement.class);
	}

	public FTPDataSource(URI uri) {
		setURI(uri);
	}

	@Override
	public String getProtocolName() {
		return "FTP";
	}
	
	@Override
	public void connect() throws Exception {
		super.connect();
		((FTPClient) getComponent(IAuthenticable.class)).connect();
	}
	
	@Override
	public void disconnect() throws Exception {
		super.disconnect();
		((FTPClient) getComponent(IAuthenticable.class)).disconnect();
	}

}
