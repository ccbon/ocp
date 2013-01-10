package org.ocpteam.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;

public class FTPPersistentFileMap extends DSContainer<DataSource> implements
		IPersistentMap {

	private FTPClient ftp;
	private String hostname;
	private String login;
	private String password;
	private String pathname;
	private URI uri;

	public void setURI(String uri) throws Exception {
		this.uri = new URI(uri);
		JLG.debug("uri = " + uri);
		this.ftp = new FTPClient();
		this.hostname = this.uri.getHost();
		String[] a = this.uri.getUserInfo().split(":");
		this.login = a[0];
		this.password = a[1];
		JLG.debug("login = " + login);
		JLG.debug("passord = " + password);
		this.pathname = this.uri.getPath();
		JLG.debug("pathname = " + pathname);
		checkConnection();
	}

	private void checkConnection() throws Exception {
		if (ftp.isConnected() == false) {

			JLG.debug("Reconnect");
			try {
				ftp.disconnect();
			} catch (Exception e) {
			}
			ftp.connect(hostname);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.login(login, password);
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new Exception("Cannot connect");
			}
		}
		boolean b = ftp.changeWorkingDirectory(pathname);
		JLG.debug("b=" + b);
		JLG.debug("pathname = " + pathname);
		if (b == false) {
			ftp.makeDirectory(pathname);
			b = ftp.changeWorkingDirectory(pathname);
			JLG.debug("b=" + b);
		}
		ftp.enterLocalPassiveMode();
		JLG.debug("check connection passed.");
	}

	@Override
	public void clear() throws Exception {
		checkConnection();
		try {
			ftp.removeDirectory(pathname);
			ftp.mkd(pathname);
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public boolean containsKey(Address address) throws Exception {
		checkConnection();
		for (FTPFile child : ftp.listFiles()) {
			Address a = new Address(JLG.hexToBytes(child.getName()));
			if (a.equals(address)) {
				return true;
			}
		}
		return false;
	}

	private byte[] getBinaryFile(String name) throws Exception {
		checkConnection();
		InputStream f = null;
		byte[] content = null;
		ByteArrayOutputStream baos = null;
		try {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			JLG.debug("name = " + name);
			f = ftp.retrieveFileStream(name);
			if (f == null) {
				throw new Exception("Cannot retrieve the file content of "
						+ name);
			}
			baos = new ByteArrayOutputStream();
			byte[] read = new byte[1024];
			int len = 0;
			while ((len = f.read(read, 0, read.length)) != -1) {
				baos.write(read, 0, len);
			}
			content = baos.toByteArray();
			if (!ftp.completePendingCommand()) {
				ftp.logout();
				ftp.disconnect();
				throw new Exception("FTP transfer not complete");
			}
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (f != null) {
				f.close();
			}
		}
		return content;
	}

	@Override
	public byte[] get(Address key) {
		try {
			return getBinaryFile(key.toString());
		} catch (Exception e) {
			// JLG.error(e);
		}
		return null;
	}

	@Override
	public Set<Address> keySet() throws Exception {
		checkConnection();
		Set<Address> result = new HashSet<Address>();
		try {
			for (FTPFile child : ftp.listFiles()) {
				Address address = new Address(JLG.hexToBytes(child.getName()));
				result.add(address);
			}
		} catch (Exception e) {
			JLG.error(e);
		}

		return result;
	}

	@Override
	public void put(Address address, byte[] value) throws Exception {
		JLG.debug("Start put");
		JLG.debug("Address=" + address);
		checkConnection();
		ByteArrayInputStream bais = new ByteArrayInputStream(value);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		if (!ftp.storeFile(address.toString(), bais)) {
			throw new Exception("Cannot store file");
		}
		bais.close();
		JLG.debug("End put");
	}

	@Override
	public void putAll(IDataStore datastore) throws Exception {
		for (Address address : datastore.keySet()) {
			put(address, datastore.get(address));
		}
	}

	@Override
	public void remove(Address address) {
		try {
			if (containsKey(address)) {
				ftp.deleteFile(address.toString());
			}
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public String getURI() {
		return uri.toString();
	}
}
