package org.ocpteam.protocol.dht;

import java.io.StreamCorruptedException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

		ds().client.sendAllAsync(message);
	}

	@Override
	public String get(String key) throws Exception {
		// strategy: retrieve the first one available (locally first)
		String value = ds().retrieve(key);
		if (value != null) {
			return value;
		}
		// try to find a node with contains the key.
		DHTModule m = ds().getComponent(DHTModule.class);
		final byte[] message = ds().client.getProtocol().getMessageSerializer()
				.serializeInput(new InputMessage(m.retrieve(), key, value));
		ExecutorService exe = Executors.newCachedThreadPool();
		Collection<Callable<String>> tasks = new LinkedList<Callable<String>>();
		for (final Contact c : ds().contactMap.getOtherContacts()) {
			tasks.add(new Callable<String>() {

				@Override
				public String call() throws Exception {
					try {
						JLG.debug("request");
						byte[] response = ds().client.request(c, message);
						JLG.debug("request end");
						if (response != null) {
							String value = (String) ds().client.getProtocol()
									.getMessageSerializer()
									.deserializeOutput(response);
							return value;
						}
					} catch (NotAvailableContactException e) {
						JLG.debug("detach");
						ds().client.detach(c);
					}
					throw new Exception();
				}
			});
		}
		if (tasks.isEmpty()) {
			return null;
		}
		try {
			value = exe.invokeAny(tasks);
		} catch (ExecutionException e) {
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

		ds().client.sendAllAsync(message);

	}

	@Override
	public Set<String> keySet() throws Exception {
		// strategy: merge each keyset for each node.
		Set<String> set = new HashSet<String>(ds().keySet());
		DHTModule m = ds().getComponent(DHTModule.class);
		byte[] message = ds().client.getProtocol().getMessageSerializer()
				.serializeInput(new InputMessage(m.keySet()));
		for (Contact c : ds().contactMap.getOtherContacts()) {
			try {
				int retry = 0;
				while (true) {
					try {
						byte[] response = ds().client.request(c, message);

						String[] array = (String[]) ds().client.getProtocol()
								.getMessageSerializer()
								.deserializeOutput(response);
						for (String s : array) {
							JLG.debug("s=" + s);
							set.add(s);
						}
						break;
					} catch (StreamCorruptedException e) {
						retry++;
						if (retry > 3) {
							throw e;
						}
					}
				}
			} catch (NotAvailableContactException e) {
				ds().client.detach(c);
			}
		}
		return set;
	}
}
