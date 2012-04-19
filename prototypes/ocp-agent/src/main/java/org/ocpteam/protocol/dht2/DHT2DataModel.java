package org.ocpteam.protocol.dht2;

import java.util.Queue;
import java.util.Set;

import org.ocpteam.component.DataSourceContainer;
import org.ocpteam.component.NodeMap;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

public class DHT2DataModel extends DataSourceContainer implements IMapDataModel {

	@Override
	public DHT2DataSource ds() {
		return (DHT2DataSource) super.ds();
	}

	@Override
	public void set(String key, String value) throws Exception {
		// Stragegy: store on all rings the pair key->value.
		for (int i = 0; i < ds().ringNodeMap.getRingNbr(); i++) {
			set(i, key, value);
		}
	}

	public void set(int ring, String key, String value) throws Exception {
		NodeMap nodeMap = ds().ringNodeMap.getNodeMaps()[ring];
		Id address = getAddress(key);
		if (nodeMap.isResponsible(address)) {
			ds().store(key, value);
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		JLG.debug("contactQueue=" + contactQueue);
		
		DHT2Module m = ds().getComponent(DHT2Module.class);
		ds().client.requestByPriority(contactQueue, new InputMessage(m.store(), ring, key, value));
	}

	private Id getAddress(String key) throws Exception {
		return ds().hash(key.getBytes());
	}

	@Override
	public String get(String key) throws Exception {
		return null;
//		// strategy: if responsible look locally else ask to the right contact
//		Id address = getAddress(key);
//		if (ds().nodeMap.isResponsible(address)) {
//			return ds().retrieve(key);
//		}
//		Queue<Contact> contactQueue = ds().nodeMap.getContactQueue(address);
//		DHT2Module m = ds().getComponent(DHT2Module.class);
//		Response r = ds().client.requestByPriority(contactQueue, new InputMessage(m.retrieve(), key));
//		return (String) r.getObject();
	}

	@Override
	public void remove(String key) throws Exception {
//		// strategy: send to all node the remove request.
//		Id address = getAddress(key);
//		if (ds().nodeMap.isResponsible(address)) {
//			ds().destroy(key);
//			return;
//		}
//
//		DHT2Module m = ds().getComponent(DHT2Module.class);
//		ds().client.sendAll(new InputMessage(m.remove(), key));
//		ds().client.waitForCompletion();
//
	}

	@Override
	public Set<String> keySet() throws Exception {
		return null;
//		// strategy: merge the keyset of all contacts
//		Set<String> set = new HashSet<String>(ds().keySet());
//		DHT2Module m = ds().getComponent(DHT2Module.class);
//		InputFlow message = new InputFlow(m.keySet());
//		for (Contact c : ds().contactMap.getOtherContacts()) {
//			Socket socket = null;
//			try {
//				int retry = 0;
//				while (true) {
//					try {
//						socket = ds().contactMap.getTcpClient(c).borrowSocket(
//								message);
//						DataInputStream in = new DataInputStream(
//								socket.getInputStream());
//						while (true) {
//							Serializable serializable = ds().protocol
//									.getStreamSerializer().readObject(in);
//							if (serializable instanceof EOMObject) {
//								break;
//							}
//							String s = (String) serializable;
//							JLG.debug("s=" + s);
//							set.add(s);
//						}
//						ds().contactMap.getTcpClient(c).returnSocket(socket);
//						break;
//					} catch (StreamCorruptedException e) {
//						if (socket != null) {
//							socket.close();
//							socket = null;
//						}
//						retry++;
//						if (retry > 3) {
//							throw e;
//						}
//					}
//				}
//			} catch (SocketException e) {
//			} catch (EOFException e) {
//			} catch (SocketTimeoutException e) {
//			} catch (NotAvailableContactException e) {
//			} finally {
//				if (socket != null) {
//					socket.close();
//					socket = null;
//				}
//			}
//
//		}
//		return set;
	}
}
