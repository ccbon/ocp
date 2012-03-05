package org.ocpteam.layer.rsp;

import java.io.File;
import java.net.URI;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.misc.JLG;

public abstract class DataSource {

	public static ResourceBundle protocolResource = ResourceBundle
			.getBundle("protocols");
	public static ResourceBundle extensionResource = ResourceBundle
			.getBundle("extensions");

	public static ResourceBundle getResource(String protocol, String subpackage)
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

	protected URI uri;
	private Agent agent;
	private File file;

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
		getAgent().disconnect();
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
		String datasourceClass = DataSource.protocolResource.getString(protocol
				.toUpperCase());
		DataSource ds = (DataSource) Class.forName(datasourceClass)
				.newInstance();
		ds.setFile(file);
		ds.setURI(uri);
		return ds;

	}

	public void setURI(URI uri) {
		this.uri = uri;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() throws Exception {
		if (file == null) {
			throw new Exception("file not set");
		}
		return file;
	}

	public abstract boolean usesAuthentication();
	
	public abstract Authentication getAuthentication() throws Exception;

}
