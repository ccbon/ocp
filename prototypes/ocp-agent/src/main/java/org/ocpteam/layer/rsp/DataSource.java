package org.ocpteam.layer.rsp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ResourceBundle;

import org.ocpteam.design.Designer;
import org.ocpteam.design.Functionality;
import org.ocpteam.functionality.DataSourceFactory;
import org.ocpteam.misc.JLG;

public abstract class DataSource implements Functionality<DataSourceFactory> {

	public DataSourceFactory dsf;

	@Override
	public void setParent(DataSourceFactory parent) {
		this.dsf = parent;
	}

	public static ResourceBundle protocolResource = ResourceBundle
			.getBundle("protocols");
	public static ResourceBundle extensionResource = ResourceBundle
			.getBundle("extensions");

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
		String packageString = this.getClass().getPackage()
				.getName()
				+ "." + subpackage.toLowerCase();
		String resourceClassString = packageString + "."
				+ getProtocol().toUpperCase() + subpackage.toUpperCase()
				+ "Resource";
		JLG.debug("class=" + resourceClassString);
		return (ResourceBundle) Class.forName(resourceClassString)
				.newInstance();
	}
}
