package org.ocpteam.test;

import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class TestSFTP {
	public static void main(String[] args) {
		try {
		JSch jsch = new JSch();

		String knownHostsFilename = "known_hosts";
		jsch.setKnownHosts(knownHostsFilename);

		Session session = jsch.getSession("jlouis", "127.0.0.1", 22);
		{
			// OR non-interactive version. Relies in host key being in
			// known-hosts file
			session.setPassword("jlouis");
		}

		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();

		ChannelSftp sftpChannel = (ChannelSftp) channel;

		sftpChannel.get("remote-file", "local-file");
		// OR
		InputStream in = sftpChannel.get("remote-file");
		// process inputstream as needed

		sftpChannel.exit();
		session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
