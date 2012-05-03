package org.ocpteam.protocol.dht5;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.entity.Context;
import org.ocpteam.fs.BFSDataModel;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.JLG;

/**
 * DHT5 is a distributed hashtable based on AddressDataSource. The data model is
 * file system based.
 * 
 * 
 * 
 */
public class DHT5DataSource extends AddressDataSource {

	public IDataModel dm;

	public DHT5DataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new BFSDataModel());
		addComponent(IPersistentMap.class, new PersistentFileMap());
	}

	@Override
	public void init() throws Exception {
		super.init();
		dm = getComponent(IDataModel.class);
	}

	@Override
	public String getProtocolName() {
		return "DHT5";
	}

	@Override
	public synchronized void connect() throws Exception {
		JLG.debug("connect " + getName());
		super.connect();
		Context c = new Context(dm);
		setContext(c);
	}

}
