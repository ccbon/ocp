package org.ocpteam.component;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.ocpteam.misc.JLG;

public abstract class PropertiesDataSource extends DataSource {

	private Properties p = new Properties();

	public void setProperties(Properties p) {
		this.p = p;
	}

	public Properties getProperties() {
		return p;
	}

	@Override
	public void newTemp() throws Exception {
		p = new Properties();
		bIsNew = true;
	}

	@Override
	public void open(File file) throws Exception {
		super.open(file);
		try {
			p = JLG.loadConfig(this.getFile().getAbsolutePath());
		} catch (Exception e) {
			p = new Properties();
		}
	}

	@Override
	public void save() throws Exception {
		JLG.debug("saving pds");
		super.save();
		if (this.getFile() != null) {
			JLG.storeConfig(this.p, this.getFile().getAbsolutePath());
		} else {
			throw new Exception("cannot save: file not set.");
		}
	}

	@Override
	public void setURI(URI uri) {
		super.setURI(uri);
		p.setProperty("uri", uri.toString());
	}

}
