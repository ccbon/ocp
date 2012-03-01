package org.ocpteam.layer.rsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.misc.JLG;

public class DataSource {

	public static ResourceBundle protocolResource = ResourceBundle
			.getBundle("protocols");

	public static ResourceBundle extensionResource = ResourceBundle
			.getBundle("extensions");

	private URI uri;

	private String path;

	private String protocol;

	private File file;

	private Agent agent;

	public DataSource() {
	}

	public DataSource(File file) throws Exception {
		setFile(file);
	}
	
	public DataSource(URI uri) throws Exception {
		setURI(uri);
	}
	
	public DataSource(String protocol) {
		setProtocol(protocol);
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
		JLG.debug("extension=" + extension);
		if (file.exists()) {
			if (extension.equalsIgnoreCase(".uri")) {
				Properties p = new Properties();
				InputStream is = new FileInputStream(file);
				p.load(is);
				is.close();
				URI uri = new URI(p.getProperty("uri"));
				setURI(uri);
			} else {
				String protocol = DataSource.extensionResource
						.getString(extension.toLowerCase());
				setProtocol(protocol);
				String path = file.getAbsolutePath();
				setPath(path);

			}
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
			String agentClassString = protocolResource.getString(protocol
					.toUpperCase());
			Agent agent = (Agent) Class.forName(agentClassString).newInstance();
			if (agent != null) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static DataSource getInstance(URI uri) throws Exception {
		return new DataSource(uri);
	}

	public Agent getAgent() throws Exception {
		JLG.debug("protocol=" + protocol);
		if (agent != null) {
			return agent;
		}
		String agentClassString = protocolResource.getString(protocol
				.toUpperCase());
		agent = (Agent) Class.forName(agentClassString).newInstance();
		agent.setDataSource(this);
		return agent;
	}

	public void setURI(URI uri) {
		this.uri = uri;
		setProtocol(uri.getScheme().toLowerCase());
		setPath(uri.getPath());
	}

	public File getFile() {
		return this.file;
	}

	public void close() {
	}
	
	public ResourceBundle getResource(String subpackage) throws Exception {
		return DataSource.getResource(getProtocol(), subpackage);
	}

	public static ResourceBundle getResource(String protocol, String subpackage) throws Exception {
		String agentClassString = protocolResource.getString(protocol
				.toUpperCase());
		String packageString = Class.forName(agentClassString).getPackage().getName() + "." + subpackage.toLowerCase();
		String resourceClassString = packageString + "." + protocol.toUpperCase() + subpackage.toUpperCase() + "Resource";
		JLG.debug("class=" + resourceClassString);
		return (ResourceBundle) Class.forName(resourceClassString).newInstance();
	}

	public URI getURI() {
		return uri;
	}

}
