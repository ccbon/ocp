package org.ocpteam.layer.rsp;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.ocpteam.misc.JLG;

public abstract class PropertiesDataSource extends DataSource {

	private Properties p;
	
	public void setProperties(Properties p) {
		this.p = p;
	}
	
	public Properties getProperties() {
		if (p == null) {
			try {
				if (getFile() != null && getFile().exists()) {
					p = JLG.loadConfig(this.getFile().getAbsolutePath());
				} else {
					p = new Properties();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return p;
	}
	
	@Override
	public void setURI(URI uri) {
		super.setURI(uri);
		if (p == null) {
			p = new Properties();
			p.setProperty("uri", uri.toString());
		}
	}
	
	@Override
	public void save() throws Exception {

		if (getFile() != null) {
			JLG.storeConfig(this.p, this.getFile().getAbsolutePath());
		} else {
			throw new Exception("cannot save: file not set");
		}
	}
	
	@Override
	public void setFile(File file) {
		super.setFile(file);
		if (p == null) {
			try {
			p = JLG.loadConfig(this.getFile().getAbsolutePath());
			} catch (Exception e) {
				p = new Properties();
			}
		}
	}

}
