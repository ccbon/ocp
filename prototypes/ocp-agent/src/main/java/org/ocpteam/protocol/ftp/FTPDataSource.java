package org.ocpteam.protocol.ftp;

import java.net.URI;

import org.ocpteam.functionality.Agent;
import org.ocpteam.functionality.Authentication;
import org.ocpteam.functionality.DataModel;
import org.ocpteam.functionality.DataSource;

public class FTPDataSource extends DataSource {

	public FTPDataSource() throws Exception {
		super();
		getDesigner().add(Agent.class, new FTPAgent());
		getDesigner().add(DataModel.class, new FTPFileSystem());
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
