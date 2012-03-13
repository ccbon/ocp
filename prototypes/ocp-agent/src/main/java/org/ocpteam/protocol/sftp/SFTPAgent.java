package org.ocpteam.protocol.sftp;

import org.ocpteam.functionality.Agent;
import org.ocpteam.functionality.Authentication;
import org.ocpteam.functionality.DataModel;
import org.ocpteam.layer.rsp.Authenticable;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SFTPAgent extends Agent implements Authenticable {

	private JSch jsch;
	Session session;
	public ChannelSftp channel;

	@Override
	protected void onConnect() throws Exception {
		jsch = new JSch();
	}

	@Override
	protected void onDisconnect() {
		jsch = null;
	}

	@Override
	public void login() throws Exception {
		try {
			Authentication a = ds.getDesigner().get(Authentication.class);
			String login = a.getLogin();
			Object challenge = a.getChallenge();
			String[] array = login.split("@");
			String username = array[0];
			String hostname = ds.getURI().getHost();
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
			DataModel dm = new SFTPFileSystem(user, this);
			ds.setContext(new Context(this, dm, "/"));
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
	}

}
