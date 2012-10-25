package org.ocpteam.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;

public class FTPPersistentFileMap extends DSContainer<DataSource> implements IPersistentMap {

	private FTPClient ftp;
	private String hostname;
	private String login;
	private String password;
	private String pathname;
	private URI uri;

	public static class PersistentMapEntry implements
			Map.Entry<Address, byte[]> {

		private Address address;
		private byte[] content;

		public PersistentMapEntry(Address address, byte[] content) {
			this.address = address;
			this.content = content;
		}

		@Override
		public Address getKey() {
			return address;
		}

		@Override
		public byte[] getValue() {
			return content;
		}

		@Override
		public byte[] setValue(byte[] value) {
			content = value;
			return content;
		}

	}

	public FTPPersistentFileMap() {
	}

	public void setURI(String uri) throws Exception {
		this.uri = new URI(uri);
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

	private void checkConnection() {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void clear() {
		checkConnection();
		try {
			ftp.removeDirectory(pathname);
			ftp.mkd(pathname);
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public boolean containsKey(Object key) {
		try {
			checkConnection();
			for (FTPFile child : ftp.listFiles()) {
				Address address = new Address(JLG.hexToBytes(child.getName()));
				if (address.equals(key)) {
					return true;
				}
			}

		} catch (Exception e) {
			JLG.error(e);
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		try {
			checkConnection();
			for (FTPFile child : ftp.listFiles()) {
				byte[] content = getBinaryFile(child.getName());
				if (content.equals(value)) {
					return true;
				}
			}

		} catch (Exception e) {
			JLG.error(e);
		}

		return false;
	}

	@Override
	public Set<java.util.Map.Entry<Address, byte[]>> entrySet() {
		checkConnection();
		Set<java.util.Map.Entry<Address, byte[]>> result = new HashSet<java.util.Map.Entry<Address, byte[]>>();
		try {
			for (FTPFile child : ftp.listFiles()) {
				Address address = new Address(JLG.hexToBytes(child.getName()));
				byte[] content = getBinaryFile(child.getName());
				PersistentMapEntry entry = new PersistentMapEntry(address,
						content);
				result.add(entry);
			}

		} catch (Exception e) {
			JLG.error(e);
		}

		return result;
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
	public byte[] get(Object key) {
		try {
			return getBinaryFile(key.toString());
		} catch (Exception e) {
//			JLG.error(e);
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Set<Address> keySet() {
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
	public byte[] put(Address key, byte[] value) {
		try {
			checkConnection();
			byte[] a = value;
			ByteArrayInputStream bais = new ByteArrayInputStream(a);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.storeFile(key.toString(), bais);
			bais.close();
		} catch (Exception e) {
			JLG.error(e);
		}
		return value;
	}

	@Override
	public void putAll(Map<? extends Address, ? extends byte[]> m) {
		Iterator<? extends Address> it = m.keySet().iterator();
		while (it.hasNext()) {
			Address address = it.next();
			byte[] content = m.get(address);
			put(address, content);
		}
	}

	@Override
	public byte[] remove(Object key) {
		try {
			if (containsKey(key)) {
				byte[] content = getBinaryFile(key.toString());
				ftp.deleteFile(key.toString());
				return content;
			}
		} catch (Exception e) {
			JLG.error(e);
		}
		return null;
	}

	@Override
	public int size() {
		checkConnection();
		try {
			return ftp.listFiles().length;
		} catch (IOException e) {
			JLG.error(e);
		}
		return 0;
	}

	@Override
	public Collection<byte[]> values() {
		checkConnection();
		Collection<byte[]> result = new HashSet<byte[]>();
		try {
			for (FTPFile child : ftp.listFiles()) {
				byte[] content = getBinaryFile(child.getName());
				result.add(content);
			}
		} catch (Exception e) {
			JLG.error(e);
		}
		return result;
	}
}
