package org.ocpteam.test;

import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class TestSFTP {
	public static void main(String[] args) {
		try {
		JSch jsch = new JSch();

		jsch.addIdentity("C:\\cygwin\\home\\jlouis\\.ssh\\id_rsa");
		Session session = jsch.getSession("jlouis", "127.0.0.1", 22);
		session.setUserInfo(new UserInfo() {
			
			@Override
			public void showMessage(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean promptYesNo(String arg0) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean promptPassword(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean promptPassphrase(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public String getPassword() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPassphrase() {
				// TODO Auto-generated method stub
				return null;
			}
		});

		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();

		ChannelSftp sftpChannel = (ChannelSftp) channel;
		@SuppressWarnings("unchecked")
		Vector<LsEntry> v = sftpChannel.ls("/asdfasdf");
		for (int i = 0; i < v.size(); i++) {
			System.out.println("entry=" + v.get(i).getFilename());
			System.out.println("entry=" + v.get(i).getLongname());
			System.out.println("entry=" + v.get(i).toString());
			System.out.println("entry=" + v.get(i).getAttrs().isDir());
		}
		sftpChannel.get("remote-file", "local-file");
		// OR
		// InputStream in = sftpChannel.get("remote-file");
		// process inputstream as needed

		sftpChannel.exit();
		session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
