package org.ocpteam.protocol.dht1;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.ocpteam.component.DataSourceContainer;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.InputFlow;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Response;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.JLG;

public class DHT1DataModel extends DataSourceContainer implements IMapDataModel {

	@Override
	public DHT1DataSource ds() {
		return (DHT1DataSource) super.ds();
	}

	@Override
	public void set(String key, String value) throws Exception {
		// strategy: find the contacts responsible for the key then send the set
		// order to the right contact
		if (ds().isResponsible(key)) {
			ds().store(key, value);
		}
		Queue<Contact> contactQueue = ds().getContactQueue(key);
		DHT1Module m = ds().getComponent(DHT1Module.class);
		ds().client.requestByPriority(contactQueue, new InputMessage(m.store(), key, value));
	}

	@Override
	public String get(String key) throws Exception {
		// strategy: if responsible look locally else ask to the right contact
		if (ds().isResponsible(key)) {
			ds().retrieve(key);
		}
		Queue<Contact> contactQueue = ds().getContactQueue(key);
		DHT1Module m = ds().getComponent(DHT1Module.class);
		Response r = ds().client.requestByPriority(contactQueue, new InputMessage(m.retrieve(), key));
		return (String) r.getObject();
	}

	@Override
	public void remove(String key) throws Exception {
		// strategy: send to all node the remove request.
		ds().destroy(key);
		DHT1Module m = ds().getComponent(DHT1Module.class);
		ds().client.sendAll(new InputMessage(m.remove(), key));
		ds().client.waitForCompletion();

	}

	@Override
	public Set<String> keySet() throws Exception {
		// strategy: merge each keyset for each node.
		Set<String> set = new HashSet<String>(ds().keySet());
		DHT1Module m = ds().getComponent(DHT1Module.class);
		InputFlow message = new InputFlow(m.keySet());
		for (Contact c : ds().contactMap.getOtherContacts()) {
			Socket socket = null;
			try {
				int retry = 0;
				while (true) {
					try {
						socket = ds().contactMap.getTcpClient(c).borrowSocket(
								message);
						DataInputStream in = new DataInputStream(
								socket.getInputStream());
						while (true) {
							Serializable serializable = ds().protocol
									.getStreamSerializer().readObject(in);
							if (serializable instanceof EOMObject) {
								break;
							}
							String s = (String) serializable;
							JLG.debug("s=" + s);
							set.add(s);
						}
						ds().contactMap.getTcpClient(c).returnSocket(socket);
						break;
					} catch (StreamCorruptedException e) {
						if (socket != null) {
							socket.close();
							socket = null;
						}
						retry++;
						if (retry > 3) {
							throw e;
						}
					}
				}
			} catch (SocketException e) {
			} catch (EOFException e) {
			} catch (SocketTimeoutException e) {
			} catch (NotAvailableContactException e) {
			} finally {
				if (socket != null) {
					socket.close();
					socket = null;
				}
			}

		}
		return set;
	}
}
