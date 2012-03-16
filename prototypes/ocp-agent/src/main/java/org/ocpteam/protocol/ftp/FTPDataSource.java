package org.ocpteam.protocol.ftp;

import java.net.URI;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.DataModel;
import org.ocpteam.component.DataSource;

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
