package org.ocpteam.protocol.dht1;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.ocpteam.component.DataSourceContainer;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.InputFlow;
import org.ocpteam.entity.InputMessage;
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
		Queue<Contact> contacts = getContactQueue(key);
		DHT1Module m = ds().getComponent(DHT1Module.class);
		ds().client.requestByPriority(contacts, new InputMessage(m.store(), key, value));
	}

	private Queue<Contact> getContactQueue(String key) {
		return null;
	}

	@Override
	public String get(String key) throws Exception {
		// strategy: retrieve the first one available (locally first)
		String value = ds().retrieve(key);
		if (value != null) {
			return value;
		}
		// try to find a node with contains the key.
		DHT1Module m = ds().getComponent(DHT1Module.class);
		final InputMessage message = new InputMessage(m.retrieve(), key, value);
		ExecutorService exe = ds().client.getExecutor();
		Collection<Callable<String>> tasks = new LinkedList<Callable<String>>();
		for (final Contact c : ds().contactMap.getOtherContacts()) {
			tasks.add(new Callable<String>() {

				@Override
				public String call() throws Exception {
					try {
						JLG.debug("request");
						String value = (String) ds().client.request(c, message);
						JLG.debug("request end");
						if (value != null) {
							return value;
						}
					} catch (NotAvailableContactException e) {
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
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		} catch (NullPointerException e) {
		}
		return value;

	}

	@Override
	public void remove(String key) throws Exception {
		// strategy: send to all node the remove request.
		ds().remove(key);
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
