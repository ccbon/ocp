package org.ocpteam.protocol.dht;

import java.util.Set;

import org.ocpteam.component.DataSourceContainer;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.interfaces.IMapDataModel;

public class DHTDataModel extends DataSourceContainer implements IMapDataModel {

	@Override
	public DHTDataSource ds() {
		return (DHTDataSource) super.ds();
	}

	@Override
	public void set(String key, String value) throws Exception {
		ds().store(key, value);
		DHTModule m = ds().getComponent(DHTModule.class);
		byte[] message = ds().client.getProtocol().getMessageSerializer()
				.serializeInput(new InputMessage(m.store(), key, value));
		ds().client.sendAll(message);
	}

	@Override
	public String get(String key) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(String key) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> keySet() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
