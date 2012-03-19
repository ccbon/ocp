package org.ocpteam.component;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.core.Container;
import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.misc.JLG;

public class DataSourceFactory extends Container implements IComponent {
	public static ResourceBundle extensionResource = ResourceBundle
			.getBundle("extensions");
	
	protected IContainer parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}
	
	@Override
	public IContainer getParent() {
		return parent;
	}

	public Iterator<DataSource> getDataSourceIterator() {
		List<DataSource> l = new LinkedList<DataSource>();
		Iterator<IComponent> it = getDesigner().iterator();
		while (it.hasNext()) {
			IComponent functionality = it.next();
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
			protocol = extensionResource.getString(extension
					.toLowerCase());
		}
		DataSource ds = getInstance(protocol);
		ds.open(file);
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
