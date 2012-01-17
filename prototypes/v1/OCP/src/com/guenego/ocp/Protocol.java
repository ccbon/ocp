package com.guenego.ocp;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;

import com.guenego.misc.Id;
import com.guenego.misc.JLG;

public class Protocol {

	public static final String SEPARATOR = ":";
	public static final String PING = "ping";
	public static final String NETWORK_PROPERTIES = "network_properties";
	public static final String ERROR = "ERROR";
	public static final String NODE_ID = "node_id";
	public static final String GET_CONTACT = "get_contact";
	public static final String GENERATE_CAPTCHA = "generate_captcha";
	public static final String CREATE_USER = "create_user";
	public static final String SUCCESS = "0";
	public static final String DECLARE_CONTACT = "declare_contact";
	public static final String CREATE_OBJECT = "create_object";
	public static final String GET_USER = "get_user";
	public static final String GET_ADDRESS = "get_address";
	public static final String ADDRESS_NOT_FOUND = "not_found";
	public static final String REMOVE_ADDRESS = "remove_address";
	private OCPAgent agent;

	public Protocol(OCPAgent agent) {
		this.agent = agent;
	}

	public String process(String request, Socket clientSocket) throws InterruptedException {
		if (request.equalsIgnoreCase(PING)) {
			return SUCCESS;
		}

		if (request.equalsIgnoreCase(NETWORK_PROPERTIES)) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				agent.network.store(out, "");
				String result = out.toString();
				out.close();
				return result;
			} catch (Exception e) {
				return ERROR;
			}

		}

		if (request.equalsIgnoreCase(NODE_ID)) {
			try {
				return agent.generateId().toString();
			} catch (Exception e) {
				return ERROR;
			}

		}

		if (request.equalsIgnoreCase(GET_CONTACT)) {
			try {
				Contact c = agent.toContact();
				
				
				JLG.debug("I start to serialize");
				return JLG.serialize(c);
			} catch (Exception e) {
				JLG.error(e);
				return ERROR;
			}

		}

		if (request.startsWith(GET_USER)) {
			try {
				// Protocol.GET_USER + ":" + key
				String[] al = request.split(":");
				Key key = new Key(new Id(al[1]));
				Content data = agent.get(key);
				if (data == null) {
					throw new Exception("Cannot find user for key = " + key);
				}
				// serialize it.
				return JLG.bytesToHex(data.getContent());
			} catch (Exception e) {
				JLG.error(e);
				return ERROR;
			}
		}

		if (request.startsWith(GET_ADDRESS)) {
			try {
				// Protocol.GET_ADDRESS + ":" + address
				String[] al = request.split(":");
				Address address = new Address(al[1]);
				Content data = agent.get(address);
				if (data == null) {
					return ADDRESS_NOT_FOUND;
				}
				// serialize it.
				return JLG.serialize(data);
			} catch (Exception e) {
				JLG.error(e);
				return ERROR;
			}
		}

		if (request.equalsIgnoreCase(GENERATE_CAPTCHA)) {
			try {
				Captcha captcha = new Captcha(agent);
				// serialize it.
				return JLG.serialize(captcha);
			} catch (Exception e) {
				return ERROR;
			}

		}

		if (request.startsWith(CREATE_USER)) {
			try {
//				String request = Protocol.message(Protocol.CREATE_USER, data, link, captcha,
//						answer);

				// TODO handle when user already exists.

				Iterator<String> it = iterator(request);
				it.next();
				ObjectData data = (ObjectData) JLG.deserialize(it.next());
				Link link = (Link) JLG.deserialize(it.next());
				Captcha captcha = (Captcha) JLG.deserialize(it.next());
				String answer = it.next();
				
				captcha.check(agent, answer);
				
				Key key = link.getKey();
				Key targetKey = link.getTargetKey();
				if (agent.exists(key) || agent.exists(targetKey)) {
					throw new Exception("it seems that the user already exists.");
				}
				
				agent.setWithLink(null, data, link);

				return SUCCESS;
			} catch (Exception e) {
				JLG.error(e);
				return ERROR;
			}

		}

		if (request.startsWith(CREATE_OBJECT)) {
			try {
				// String request = Protocol.CREATE_OBJECT + ":" + address + ":"
				// + JLG.serialize(content);

				Iterator<String> it = iterator(request);
				it.next();
				Address address = new Address(it.next());
				Content content = (Content) JLG.deserialize(it.next());
				agent.store(address, content);
				// serialize it.
				return SUCCESS;
			} catch (Exception e) {
				JLG.error(e);
				return ERROR;
			}

		}

		if (request.startsWith(DECLARE_CONTACT)) {
			try {
				Iterator<String> it = iterator(request);
				it.next();
				Contact contact = (Contact) JLG.deserialize(it.next());
				InetAddress host = clientSocket.getInetAddress();
				contact.updateHost(host.getHostAddress());
				agent.addContact(contact);
				// serialize it.
				return SUCCESS;
			} catch (Exception e) {
				JLG.error(e);
				return ERROR;
			}

		}

		if (request.startsWith(REMOVE_ADDRESS)) {
			try {
				Iterator<String> it = iterator(request);
				it.next();
				Address address = (Address) JLG.deserialize(it.next());
				byte[] addressSignature = JLG.hexToBytes(it.next());
				agent.remove(address, addressSignature);
				// serialize it.
				return SUCCESS;
			} catch (Exception e) {
				JLG.error(e);
				return ERROR;
			}

		}

		return ERROR + ":OCP message not understood";
	}

	private static Iterator<String> iterator(String request) {
		return Arrays.asList(request.split(SEPARATOR)).iterator();
	}

	public static String hasBeenDetached(Contact contact) {
		return "INFORM_DETACH:" + contact.id;
	}

	public static String message(String function, Object... objects)
			throws Exception {
		String result = function;
		for (int i = 0; i < objects.length; i++) {
			result += Protocol.SEPARATOR;
			if (objects[i].getClass() == byte[].class) {
				result += JLG.bytesToHex((byte[]) objects[i]);
			} else if (objects[i].getClass() == String.class) {
				result += objects[i];
			} else {
				result += JLG.serialize((Serializable) objects[i]);
			}

		}
		return result;
	}

}
