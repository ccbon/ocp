package org.ocpteam.layer.rsp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.design.Designer;
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

	

	private URI uri;
	private Agent agent;
	private File file;

	private boolean bIsTempFile = false;
	
	public Designer<DataSource> designer;
	
	public DataSource() {
		designer = new Designer<DataSource>(this);
	}

	public Agent getAgent() {
		if (agent == null) {
			agent = createAgent();
		}
		return agent;
	}

	protected abstract Agent createAgent();
	
	public abstract String getProtocol();

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		if (isTempFile()) {
			file.delete();
			this.file.renameTo(file);
		}
		this.file = file;
	}
	
	public URI getURI() throws Exception {
		if (this.uri == null) {
			throw new Exception("uri is null");
		}
		return this.uri;
	}
	
	public void setURI(URI uri) {
		this.uri = uri;
	}
	
	public void open() throws Exception {
		getAgent().connect();
	}	

	public void close() {
		// time to disconnect from the datasource
		try {
			getAgent().disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() throws Exception {
	}

	public void setTempFile(boolean b) throws IOException {
		if (b == true) {
			File file = File.createTempFile(
					"temp" + System.currentTimeMillis(), "tmp");
			file.delete();
			file.deleteOnExit();
			this.file = file;
		}
		this.bIsTempFile = b;
	}

	public boolean isTempFile() {
		return bIsTempFile;
	}
	
	public ResourceBundle getResource(String subpackage) throws Exception {
		return DataSource.getResource(getProtocol(), subpackage);
	}

}
