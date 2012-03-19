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

}
