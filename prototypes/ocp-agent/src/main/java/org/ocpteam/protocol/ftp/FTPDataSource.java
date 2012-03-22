package org.ocpteam.protocol.ftp;

import java.net.URI;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.DataSource;
import org.ocpteam.interfaces.IDataModel;

public class FTPDataSource extends DataSource {

	public FTPDataSource() throws Exception {
		super();
		addComponent(Client.class, new FTPClient());
		addComponent(IDataModel.class, new FTPFileSystem());
		addComponent(Authentication.class);
	}

	public FTPDataSource(URI uri) {
		setURI(uri);
	}

	@Override
	public String getProtocol() {
		return "FTP";
	}
	
	@Override
	public void connect() throws Exception {
		((FTPClient) getComponent(Client.class)).connect();
	}
	
	@Override
	public void disconnect() throws Exception {
		context = null;
		((FTPClient) getComponent(Client.class)).disconnect();
	}

}
