package org.ocpteam.protocol.dht;

import java.util.Set;

import org.ocpteam.component.DataSourceContainer;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.interfaces.IMapDataModel;

public class DHTDataModel extends DataSourceContainer implements IMapDataModel {

	@Override
	public DHTDataSource ds() {
		return (DHTDataSource) super.ds();
	}

	@Override
	public void set(String key, String value) throws Exception {
		// strategy: save the pair everywhere
		ds().store(key, value);
		DHTModule m = ds().getComponent(DHTModule.class);
		byte[] message = ds().client.getProtocol().getMessageSerializer()
				.serializeInput(new InputMessage(m.store(), key, value));
		ds().client.sendAll(message);
	}

	@Override
	public String get(String key) throws Exception {
		String value = ds().retrieve(key);
		if (value == null) {
			// try to find a node with contains the key.
			DHTModule m = ds().getComponent(DHTModule.class);
			byte[] message = ds().client.getProtocol().getMessageSerializer()
					.serializeInput(new InputMessage(m.retrieve(), key, value));
			for (Contact c : ds().contactMap.getArray()) {
				byte[] response = ds().client.request(c, message);
				if (response != null) {
					value = (String) ds().client.getProtocol().getMessageSerializer().deserializeOutput(response);
				}
			}
		}
		return value;
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
