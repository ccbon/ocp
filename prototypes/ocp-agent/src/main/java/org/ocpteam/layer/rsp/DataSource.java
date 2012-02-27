package org.ocpteam.layer.rsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.ResourceBundle;

public class DataSource {

	public static ResourceBundle protocolResource = ResourceBundle
			.getBundle("protocols");

	public static ResourceBundle extensionResource = ResourceBundle
			.getBundle("extensions");

	private URI uri;

	private String path;

	private String protocol;

	private File file;

	public DataSource(URI uri) {
		setURI(uri);
	}

	public DataSource() {
	}

	public DataSource(File file) throws Exception {
		setFile(file);
	}

	public void setFile(File file) throws Exception {
		// args can be a archive filename
		// or a .uri file containing a valid uri.
		this.file = file;
		if (file.isDirectory()) {
			throw new Exception("Cannot accept directory.");
		}
		String name = file.getName();
		String extension = name.substring(name.lastIndexOf("."));
		if (file.exists()) {
			if (extension.equalsIgnoreCase(".uri")) {
				Properties p = new Properties();
				InputStream is = new FileInputStream(file);
				p.load(is);
				is.close();
				URI uri = new URI(p.getProperty("uri"));
				setURI(uri);
			}
		} else {
			String protocol = DataSource.extensionResource.getString(extension
					.toLowerCase());
			setProtocol(protocol);
			String path = file.getAbsolutePath();
			setPath(path);
		}
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setProtocol(String protocol) {
		if (isValid(protocol)) {
			this.protocol = protocol;
		}
	}

	public String getProtocol() {
		return protocol;
	}

	public boolean isValid(String protocol) {
		try {
			String agentClassString = protocolResource.getString(protocol.toUpperCase());
			Agent agent = (Agent) Class.forName(agentClassString).newInstance();
			if (agent != null) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static DataSource getInstance(URI uri) {
		return new DataSource(uri);
	}

	public Agent getAgent() throws Exception {
		String agentClassString = protocolResource.getString(protocol.toUpperCase());
		Agent agent = (Agent) Class.forName(agentClassString).newInstance();
		agent.setDataSource(this);
		return agent;
	}

	public void setURI(URI uri) {
		this.uri = uri;
		setProtocol(uri.getScheme().toLowerCase());
		setPath(uri.getPath());
	}
}
