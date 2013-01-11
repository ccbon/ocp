package org.ocpteamx.protocol.sftp;

import org.eclipse.swt.widgets.Display;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;

import com.jcraft.jsch.UserInfo;

public class SFTPUserInfo implements UserInfo {

	private String password;

	public SFTPUserInfo() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getPassphrase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		LOG.debug("getPassword");
		return password;
	}

	@Override
	public boolean promptPassphrase(String arg0) {
		LOG.debug("promptPassphrase");
		return false;
	}

	@Override
	public boolean promptPassword(String arg0) {
		LOG.debug("promptPassword: " + arg0);
		return true;
	}

	@Override
	public boolean promptYesNo(String arg0) {
		return QuickMessage.confirm(Display.getCurrent().getActiveShell(), arg0);
	}

	@Override
	public void showMessage(String arg0) {
		QuickMessage.inform(Display.getCurrent().getActiveShell(), arg0);
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
