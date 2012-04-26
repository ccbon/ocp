package org.ocpteam.protocol.dht2;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.ocpteam.component.DSContainer;
import org.ocpteam.component.NodeMap;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.EOMObject;
import org.ocpteam.entity.InputFlow;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Response;
import org.ocpteam.exception.NotAvailableContactException;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

public class DHT2DataModel extends DSContainer<DHT2DataSource> implements IMapDataModel {

	@Override
	public void set(String key, String value) throws Exception {
		// Stragegy: store on all rings the pair key->value.
		for (int i = 0; i < ds().ringNodeMap.getRingNbr(); i++) {
			set(i, key, value);
		}
	}

	public void set(int ring, String key, String value) throws Exception {
		NodeMap nodeMap = ds().ringNodeMap.getNodeMaps()[ring];
		if (nodeMap.isEmpty()) {
			return;
		}
		Id address = ds().getAddress(key);
		if (nodeMap.isResponsible(address)) {
			ds().store(key, value);
			return;
		}
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		JLG.debug("contactQueue=" + contactQueue);

		DHT2Module m = ds().getComponent(DHT2Module.class);
		ds().client.requestByPriority(contactQueue, new InputMessage(m.store(),
				ring, key, value));
	}

	

	@Override
	public String get(String key) throws Exception {
		// strategy : look at the first one you get.
		Id address = ds().getAddress(key);
		if (ds().ringNodeMap.isResponsible(address)) {
			return ds().retrieve(key);
		}
		// not found locally so look at every ring.
		String value = null;
		for (int i = 0; i < ds().ringNodeMap.getRingNbr(); i++) {
			value = get(i, key);
			if (value != null) {
				break;
			}
		}
		return value;

	}

	private String get(int i, String key) throws Exception {
		Id address = ds().getAddress(key);
		NodeMap nodeMap = ds().ringNodeMap.getNodeMaps()[i];
		Queue<Contact> contactQueue = nodeMap.getContactQueue(address);
		DHT2Module m = ds().getComponent(DHT2Module.class);
		Response r = ds().client.requestByPriority(contactQueue,
				new InputMessage(m.retrieve(), key));
		return (String) r.getObject();
	}

	@Override
	public void remove(String key) throws Exception {
		// // strategy: send to all node the remove request.
		// Id address = getAddress(key);
		// if (ds().nodeMap.isResponsible(address)) {
		// ds().destroy(key);
		// return;
		// }
		//
		// DHT2Module m = ds().getComponent(DHT2Module.class);
		// ds().client.sendAll(new InputMessage(m.remove(), key));
		// ds().client.waitForCompletion();
		//
	}

	@Override
	public Set<String> keySet() throws Exception {
		return null;
		// // strategy: merge the keyset of all contacts
		// Set<String> set = new HashSet<String>(ds().keySet());
		// DHT2Module m = ds().getComponent(DHT2Module.class);
		// InputFlow message = new InputFlow(m.keySet());
		// for (Contact c : ds().contactMap.getOtherContacts()) {
		// Socket socket = null;
		// try {
		// int retry = 0;
		// while (true) {
		// try {
		// socket = ds().contactMap.getTcpClient(c).borrowSocket(
		// message);
		// DataInputStream in = new DataInputStream(
		// socket.getInputStream());
		// while (true) {
		// Serializable serializable = ds().protocol
		// .getStreamSerializer().readObject(in);
		// if (serializable instanceof EOMObject) {
		// break;
		// }
		// String s = (String) serializable;
		// JLG.debug("s=" + s);
		// set.add(s);
		// }
		// ds().contactMap.getTcpClient(c).returnSocket(socket);
		// break;
		// } catch (StreamCorruptedException e) {
		// if (socket != null) {
		// socket.close();
		// socket = null;
		// }
		// retry++;
		// if (retry > 3) {
		// throw e;
		// }
		// }
		// }
		// } catch (SocketException e) {
		// } catch (EOFException e) {
		// } catch (SocketTimeoutException e) {
		// } catch (NotAvailableContactException e) {
		// } finally {
		// if (socket != null) {
		// socket.close();
		// socket = null;
		// }
		// }
		//
		// }
		// return set;
	}

	public Map<String, String> localMap(Contact c) throws Exception {
		if (c.isMyself()) {
			return ds().getMap();
		}
		Map<String, String> localmap = new HashMap<String, String>();
		DHT2Module m = ds().getComponent(DHT2Module.class);
		InputFlow message = new InputFlow(m.getLocalMap());

		Socket socket = null;
		try {

			socket = ds().contactMap.getTcpClient(c).borrowSocket(message);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			while (true) {
				Serializable serializable = ds().protocol.getStreamSerializer()
						.readObject(in);
				if (serializable instanceof EOMObject) {
					break;
				}
				String key = (String) serializable;
				String value = (String) ds().protocol.getStreamSerializer()
						.readObject(in);

				localmap.put(key, value);
			}
			ds().contactMap.getTcpClient(c).returnSocket(socket);
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

		return localmap;
	}
}
