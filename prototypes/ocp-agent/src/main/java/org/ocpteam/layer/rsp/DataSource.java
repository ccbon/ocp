package org.ocpteam.layer.rsp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.misc.JLG;

public abstract class DataSource {

	public static ResourceBundle protocolResource = ResourceBundle
			.getBundle("protocols");
	public static ResourceBundle extensionResource = ResourceBundle
			.getBundle("extensions");

	private static ResourceBundle getResource(String protocol, String subpackage)
			throws Exception {
		String agentClassString = protocolResource.getString(protocol
				.toUpperCase());
		String packageString = Class.forName(agentClassString).getPackage()
				.getName()
				+ "." + subpackage.toLowerCase();
		String resourceClassString = packageString + "."
				+ protocol.toUpperCase() + subpackage.toUpperCase()
				+ "Resource";
		JLG.debug("class=" + resourceClassString);
		return (ResourceBundle) Class.forName(resourceClassString)
				.newInstance();
	}

	public ResourceBundle getResource(String subpackage) throws Exception {
		return DataSource.getResource(getProtocol(), subpackage);
	}

	public abstract String getProtocol();

	private URI uri;
	private Agent agent;
	private File file;

	private Properties p;

	private boolean bIsTempFile = false;
	private File tempfile;

	public Agent getAgent() {
		if (agent == null) {
			agent = createAgent();
		}
		return agent;
	}

	protected abstract Agent createAgent();

	public URI getURI() throws Exception {
		if (this.uri == null) {
			throw new Exception("uri is null");
		}
		return this.uri;
	}

	public void close() {
		// time to disconnect from the datasource
		try {
			getAgent().disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DataSource getInstance(File file) throws Exception {

		if (file.isDirectory()) {
			throw new Exception("Cannot accept directory.");
		}

		String name = file.getName();
		String extension = name.substring(name.lastIndexOf("."));
		JLG.debug("extension=" + extension);
		String protocol = null;
		URI uri = null;
		if (file.exists() && extension.equalsIgnoreCase(".uri")) {
			Properties p = JLG.loadConfig(file.getAbsolutePath());
			uri = new URI(p.getProperty("uri"));
			protocol = uri.getScheme();
		} else {
			protocol = DataSource.extensionResource.getString(extension
					.toLowerCase());
		}
		DataSource ds = getInstance(protocol);
		ds.setFile(file);
		if (uri != null) {
			ds.setURI(uri);
		}
		return ds;
	}

	public static DataSource getInstance(String protocol) throws Exception {
		String datasourceClass = DataSource.protocolResource.getString(protocol
				.toUpperCase());
		return (DataSource) Class.forName(datasourceClass).newInstance();
	}

	public void setURI(URI uri) {
		this.uri = uri;
		if (p == null) {
			p = new Properties();
			p.setProperty("uri", uri.toString());
		}
	}

	public void setFile(File file) {
		if (isTempFile()) {
			this.tempfile = this.file;
		}
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setTempFile(boolean b) throws IOException {
		if (b == true) {
			File file = File.createTempFile(
					"temp" + System.currentTimeMillis(), "zip");
			file.delete();
			file.deleteOnExit();
			this.file = file;
		}
		this.bIsTempFile = b;
	}

	public boolean usesAuthentication() {
		return getAuthentication() != null;
	}

	public abstract Authentication getAuthentication();

	public void setProperties(Properties p) {
		this.p = p;
	}

	public Properties getProperties() {
		if (p == null) {
			try {
				if (file != null && file.exists()) {
					p = JLG.loadConfig(this.file.getAbsolutePath());
				} else {
					p = new Properties();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return p;
	}

	public void save() throws Exception {

		if (file != null) {
			JLG.storeConfig(this.p, this.file.getAbsolutePath());
		} else {
			throw new Exception("cannot save: file not set");
		}
	}

	protected void saveTempFile() throws IOException {
		Files.copy(Paths.get(tempfile.getAbsolutePath()),
				Paths.get(file.getAbsolutePath()),
				StandardCopyOption.REPLACE_EXISTING);

	}

	public boolean isTempFile() {
		return bIsTempFile;
	}
}
