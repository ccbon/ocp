package org.ocpteamx.protocol.sftp;

import org.ocpteam.component.DSContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IClient;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.User;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SFTPClient extends DSContainer<SFTPDataSource> implements
		IAuthenticable, IClient {

	public SFTPClient() throws Exception {
		super();
	}

	private JSch jsch;
	Session session;
	public ChannelSftp channel;
	private Object challenge;

	@Override
	public void authenticate() throws Exception {
		try {
			jsch = new JSch();
			IUserManagement a = ds().getComponent(IUserManagement.class);
			String login = a.getUsername();
			Object challenge = getChallenge();
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
					LOG.debug("passphrase is null");
					jsch.addIdentity(c.getPrivateKeyFile().getAbsolutePath());
				} else {
					LOG.debug("passphrase is set");
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
			LOG.debug("setting context");
			ds().setContext(new Context(user, dm, "/"));
			LOG.debug("dm=" + ds().getContext().getDataModel().getClass());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Cannot login");
		}
	}
	
	@Override
	public void unauthenticate() throws Exception {
		channel.exit();
		session.disconnect();
		jsch = null;
	}

	@Override
	public void setChallenge(Object challenge) {
		this.challenge = challenge;
		
	}

	@Override
	public Object getChallenge() {
		return challenge;
	}

}
