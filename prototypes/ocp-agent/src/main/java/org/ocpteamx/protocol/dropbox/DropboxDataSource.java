package org.ocpteamx.protocol.dropbox;

import org.ocpteam.component.DataSource;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.Application;

public class DropboxDataSource extends DataSource {
	public IUserManagement um;
	public String tokenFilename;

	public DropboxDataSource() throws Exception {
		super();
		addComponent(IAuthenticable.class, new DropboxClient());
		addComponent(IDataModel.class, new DropboxFileSystem());
		addComponent(IUserManagement.class, new DropboxUserManagement());

	}

	@Override
	public void init() throws Exception {
		super.init();
		setTokenFilename();
		((DropboxClient) getComponent(IAuthenticable.class)).setup();
	}

	private void setTokenFilename() throws Exception {
		this.tokenFilename = Application.getAppDir()
				+ "/dropbox_tokens.properties";

	}

	@Override
	public String getProtocolName() {
		return "Dropbox";
	}

}
