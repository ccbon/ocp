package org.ocpteam.functionality;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ResourceBundle;

import org.ocpteam.core.Container;
import org.ocpteam.core.Designer;
import org.ocpteam.core.Functionality;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.misc.JLG;

public abstract class DataSource implements Container, Functionality {

	public Container parent;
	private Designer designer;

	@Override
	public void setParent(Container parent) {
		this.parent = parent;
	}

	@Override
	public Designer getDesigner() {
		return designer;
	};

	public static ResourceBundle extensionResource = ResourceBundle
			.getBundle("extensions");

	private URI uri;
	private File file;

	private boolean bIsTempFile = false;
	protected Context context;

	public DataSource() {
		designer = new Designer(this);
	}

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
		if (getDesigner().uses(Agent.class)) {
			getDesigner().get(Agent.class).connect();
		}
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public void close() throws Exception {
		if (getDesigner().uses(Agent.class)) {
			getDesigner().get(Agent.class).disconnect();
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
		String packageString = this.getClass().getPackage().getName() + "."
				+ subpackage.toLowerCase();
		String resourceClassString = packageString + "."
				+ getProtocol().toUpperCase() + subpackage.toUpperCase()
				+ "Resource";
		JLG.debug("class=" + resourceClassString);
		return (ResourceBundle) Class.forName(resourceClassString)
				.newInstance();
	}

}
