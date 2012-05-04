package org.ocpteam.protocol.ftp;

import java.net.URI;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.UserIdentification;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;

public class FTPDataSource extends DataSource {

	public Authentication authentication;

	public FTPDataSource() throws Exception {
		super();
		addComponent(IAuthenticable.class, new FTPClient());
		addComponent(IDataModel.class, new FTPFileSystem());
		addComponent(UserIdentification.class, new Authentication());
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		authentication = (Authentication) getComponent(UserIdentification.class);
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
