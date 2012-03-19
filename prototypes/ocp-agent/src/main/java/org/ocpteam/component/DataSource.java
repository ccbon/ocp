package org.ocpteam.component;

import java.io.File;
import java.net.URI;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.core.Container;
import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IConnect;
import org.ocpteam.interfaces.IDocument;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.misc.JLG;

public abstract class DataSource extends Container implements IComponent,
		IDocument, IConnect {

	protected IContainer parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}
	
	@Override
	public IContainer getParent() {
		return parent;
	}

	private URI uri;
	private File file;

	protected boolean bIsNew = true;

	protected Context context;

	public abstract String getProtocol();
	
	@Override
	public void open(File file) throws Exception {
		bIsNew = false;
		this.file = file;
		try {
			// try to load properties if the format is ok...
			Properties p = JLG.loadConfig(this.getFile().getAbsolutePath());
			this.p = p;
		} catch (Exception e) {
		}
	}

	@Override
	public void newTemp() throws Exception {
		p = new Properties();
		bIsNew = true;
		
		file = File.createTempFile("temp" + System.currentTimeMillis(),
				"tmp");
		file.delete();
		file.deleteOnExit();
	}
	
	@Override
	public boolean isNew() {
		return bIsNew;
	}

	@Override
	public void save() throws Exception {
		JLG.debug("saving ds");
		if (bIsNew) {
			throw new Exception("Need a filename to save a new datasource");
		}
		
		if (this.getFile() != null) {
			JLG.storeConfig(this.p, this.getFile().getAbsolutePath());
		} else {
			throw new Exception("cannot save: file not set.");
		}

	}

	@Override
	public void saveAs(File file) throws Exception {
		this.file = file;
		bIsNew = false;
		JLG.debug("save");
		save();
	}

	@Override
	public void close() throws Exception {
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public URI getURI() throws Exception {
		return this.uri;
	}

	public void setURI(URI uri) {
		this.uri = uri;
		p.setProperty("uri", uri.toString());
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public void connect() throws Exception {
		if (getDesigner().uses(Client.class)) {
			getDesigner().get(Client.class).connect();
		}
	}

	public void disconnect() throws Exception {
		context = null;
		if (getDesigner().uses(Client.class)) {
			getDesigner().get(Client.class).disconnect();
		}
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
