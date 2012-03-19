package org.ocpteam.protocol.ftp;

import java.net.URI;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.component.DataSource;
import org.ocpteam.interfaces.IDataModel;

public class FTPDataSource extends DataSource {

	public FTPDataSource() throws Exception {
		super();
		getDesigner().add(Client.class, new FTPClient());
		getDesigner().add(IDataModel.class, new FTPFileSystem());
		getDesigner().add(Authentication.class);
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
		((FTPClient) getDesigner().get(Client.class)).connect();
	}
	
	@Override
	public void disconnect() throws Exception {
		context = null;
		((FTPClient) getDesigner().get(Client.class)).disconnect();
	}

}
