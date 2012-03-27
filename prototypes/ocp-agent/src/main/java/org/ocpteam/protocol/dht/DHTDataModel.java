package org.ocpteam.protocol.dht;

import java.util.HashSet;
import java.util.Set;

import org.ocpteam.component.DataSourceContainer;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.JLG;

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
		// strategy: retrieve the first one available (locally first)
		String value = ds().retrieve(key);
		if (value == null) {
			// try to find a node with contains the key.
			DHTModule m = ds().getComponent(DHTModule.class);
			byte[] message = ds().client.getProtocol().getMessageSerializer()
					.serializeInput(new InputMessage(m.retrieve(), key, value));
			for (Contact c : ds().contactMap.getArray()) {
				try {
					byte[] response = ds().client.request(c, message);
					if (response != null) {
						value = (String) ds().client.getProtocol()
								.getMessageSerializer()
								.deserializeOutput(response);
					}
				} catch (NotAvailableContactException e) {
					ds().client.detach(c);
				}
			}
		}
		return value;
	}

	@Override
	public void remove(String key) throws Exception {
		// strategy: send to all node the remove request.
		ds().remove(key);
		DHTModule m = ds().getComponent(DHTModule.class);
		byte[] message = ds().client.getProtocol().getMessageSerializer()
				.serializeInput(new InputMessage(m.remove(), key));

		ds().client.sendAll(message);

	}

	@Override
	public Set<String> keySet() throws Exception {
		// strategy: merge each keyset for each node.
		Set<String> set = new HashSet<String>(ds().keySet());
		DHTModule m = ds().getComponent(DHTModule.class);
		byte[] message = ds().client.getProtocol().getMessageSerializer()
				.serializeInput(new InputMessage(m.keySet()));
		for (Contact c : ds().contactMap.getArray()) {
			try {
				byte[] response = ds().client.request(c, message);
				String[] array = (String[]) ds().client.getProtocol()
						.getMessageSerializer().deserializeOutput(response);
				for (String s : array) {
					JLG.debug("s=" + s);
					set.add(s);
				}
			} catch (NotAvailableContactException e) {
				ds().client.detach(c);
			}
		}
		return set;
	}

}
