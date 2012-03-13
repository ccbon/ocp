package org.ocpteam.functionality;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.design.Container;
import org.ocpteam.design.Designer;
import org.ocpteam.design.Functionality;
import org.ocpteam.misc.JLG;

public class DataSourceFactory implements Container, Functionality {

	protected Container parent;
	private Designer designer;
	
	public DataSourceFactory() {
		this.designer = new Designer(this);
	}

	@Override
	public Designer getDesigner() {
		return designer;
	}

	@Override
	public void setParent(Container parent) {
		this.parent = parent;
	}

	public Iterator<DataSource> getDataSourceIterator() {
		List<DataSource> l = new LinkedList<DataSource>();
		Iterator<Functionality> it = designer.iterator();
		while (it.hasNext()) {
			Functionality functionality = it.next();
			if (functionality instanceof DataSource) {
				l.add((DataSource) functionality);
			}
		}
		return l.iterator();
	}
	
	public DataSource getInstance(File file) throws Exception {

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
	
	public DataSource getInstance(String protocol) throws Exception {
		Iterator<DataSource> it = getDataSourceIterator();
		while (it.hasNext()) {
			DataSource ds = it.next();
			String p = ds.getProtocol();
			if (p.equalsIgnoreCase(protocol)) {
				return ds.getClass().newInstance();
			}
		}
		throw new Exception("protocol not understood: " + protocol);
	}

	public ResourceBundle getResource(String protocol, String string) throws Exception {
		Iterator<DataSource> it = getDataSourceIterator();
		while (it.hasNext()) {
			DataSource ds = it.next();
			String p = ds.getProtocol();
			if (p.equalsIgnoreCase(protocol)) {
				return ds.getResource(string);
			}
		}
		throw new Exception("protocol not understood: " + protocol);
	}

}
