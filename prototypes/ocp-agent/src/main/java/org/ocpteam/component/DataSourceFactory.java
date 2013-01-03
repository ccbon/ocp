package org.ocpteam.component;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.core.Container;
import org.ocpteam.core.IContainer;
import org.ocpteam.misc.JLG;

public class DataSourceFactory extends Container<IContainer> {
	public static ResourceBundle extensionResource = ResourceBundle
			.getBundle("extensions");

	public Iterator<DataSource> getDataSourceIterator() {
		return getDataSourceList().iterator();
	}

	public DataSource getInstance(File file) throws Exception {
		if (!file.exists()) {
			throw new Exception("File not found.");
		}
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
			if (extensionResource.containsKey(extension.toLowerCase())) {
				protocol = extensionResource.getString(extension.toLowerCase());
			} else {
				protocol = extension.substring(1);
			}
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
			String p = ds.getProtocolName();
			if (p.equalsIgnoreCase(protocol)) {
				DataSource result = ds.getClass().newInstance();
				result.init();
				return result;
			}
		}
		throw new Exception("protocol not understood: " + protocol);
	}

	public ResourceBundle getResource(String protocol, String string)
			throws Exception {
		for (DataSource ds : getDataSourceList()) {
			String p = ds.getProtocolName();
			if (p.equalsIgnoreCase(protocol)) {
				return ds.getResource(string);
			}
		}
		throw new Exception("protocol not understood: " + protocol);
	}

	public List<DataSource> getDataSourceList() {
		List<DataSource> l = new LinkedList<DataSource>();
		Iterator<Object> it = iteratorComponent();
		while (it.hasNext()) {
			Object functionality = it.next();
			if (functionality instanceof DataSource) {
				l.add((DataSource) functionality);
			}
		}
		return l;
	}

	public List<DataSource> getDataSourceOrderedList() {
		List<DataSource> list = getDataSourceList();
		DataSource[] array = list.toArray(new DataSource[list.size()]);
		Arrays.sort(array, new Comparator<DataSource>() {
			@Override
			public int compare(DataSource ds1, DataSource ds2) {
				return ds1.getProtocolName().compareTo(ds2.getProtocolName());
			}
		});
		return Arrays.asList(array);
	}

}
