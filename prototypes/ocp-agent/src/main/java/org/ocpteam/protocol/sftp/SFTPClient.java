package org.ocpteam.protocol.sftp;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.entity.Context;
import org.ocpteam.entity.User;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.JLG;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SFTPClient extends Client implements IAuthenticable {

	public SFTPClient() throws Exception {
		super();
	}

	private JSch jsch;
	Session session;
	public ChannelSftp channel;

	@Override
	public void login() throws Exception {
		try {
			jsch = new JSch();
			Authentication a = ds().getComponent(Authentication.class);
			String login = a.getLogin();
			Object challenge = a.getChallenge();
			String[] array = login.split("@");
			String username = array[0];
			String hostname = ds().getURI().getHost();
			int port = 22;
			int index = hostname.indexOf(':');
			if (index != -1) {
				port = Integer.parseInt(hostname.substring(index + 1));
			}
			session = jsch.getSession(username, hostname, port);

			SFTPUserInfo ui = new SFTPUserInfo();
			SSHChallenge c = (SSHChallenge) challenge;
			if (c.getType() == SSHChallenge.PASSWORD) {
				ui.setPassword(c.getPassword());
				session.setUserInfo(ui);
			} else if (c.getType() == SSHChallenge.PRIVATE_KEY) {
				if (c.getPassphrase() == null) {
					JLG.debug("passphrase is null");
					jsch.addIdentity(c.getPrivateKeyFile().getAbsolutePath());
				} else {
					JLG.debug("passphrase is set");
					jsch.addIdentity(c.getPrivateKeyFile().getAbsolutePath(),
							c.getPassphrase());
				}
				session.setUserInfo(ui);
			}

			session.connect();
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			User user = new SFTPUser(login, c);
			IDataModel dm = new SFTPFileSystem(user, this);
			ds().setContext(new Context(dm, "/"));
			a.setUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Cannot login");
		}
	}

	@Override
	public void logout() throws Exception {
		channel.exit();
		session.disconnect();
		jsch = null;
	}

}
