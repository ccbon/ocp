package org.ocpteamx.protocol.ocp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import org.ocpteam.component.Protocol;
import org.ocpteam.entity.Session;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.InputMessage;

public class OCPProtocol extends Protocol {

	public OCPProtocol() throws Exception {
		super();
	}
	
	@Override
	public OCPDataSource ds() {
		return (OCPDataSource) super.ds();
	}

	public static final int SEPARATOR = 0;

	public static final String NODE_ID = "node_id";
	public static final String GENERATE_CAPTCHA = "generate_captcha";
	public static final String CREATE_USER = "create_user";
	public static final String CREATE_OBJECT = "create_object";
	public static final String GET_USER = "get_user";
	public static final String GET_ADDRESS = "get_address";
	public static final String REMOVE_ADDRESS = "remove_address";

	public static final byte[] SUCCESS = "0".getBytes();
	public static final byte[] ERROR = "ERROR".getBytes();
	public static final byte[] ADDRESS_NOT_FOUND = "not_found".getBytes();

	private OCPAgent agent;


	public OCPAgent getAgent() {
		if (this.agent == null) {
			this.agent = ds().getComponent(OCPAgent.class);
		}
		return this.agent;
	}
	
	@Override
	public void process(Socket clientSocket) throws Exception {
		LOG.debug("about to read object");
		Serializable o = getStreamSerializer().readObject(clientSocket);
		if (o instanceof InputMessage) {
			InputMessage inputMessage = (InputMessage) o;
			Session session = new Session(ds(), clientSocket);
			LOG.debug("inputMessage.transid=" + inputMessage.transid);
			inputMessage.transaction = getMap().get(inputMessage.transid);
			if (inputMessage.transaction == null) {
				throw new Exception("transaction unknown: " + inputMessage.transid);
			}
			Serializable s = inputMessage.transaction.run(session,
					inputMessage.objects);
			getStreamSerializer().writeObject(clientSocket, s);
		}
		if (o instanceof byte[]) {
			byte[] input = (byte[]) o;
			byte[] output = oldprocess(input, clientSocket);
			getStreamSerializer().writeObject(clientSocket, output);
		}
	}

	public byte[] oldprocess(byte[] input, Socket clientSocket) throws Exception {
		getAgent();
		String request = new String(input);
		Iterator<byte[]> it = iterator(input);

		if (request.equalsIgnoreCase(NODE_ID)) {
			try {
				return agent.generateId().getBytes();
			} catch (Exception e) {
				return ERROR;
			}

		}

		if (request.startsWith(GET_USER)) {
			try {
				// Protocol.GET_USER + ":" + key
				Key key = new Key(new Id(it.next()));
				Content data = agent.get(key);
				if (data == null) {
					throw new Exception("Cannot find user for key = " + key);
				}
				// serialize it.
				return data.getContent();
			} catch (Exception e) {
				LOG.error(e);
				return ERROR;
			}
		}

		if (request.startsWith(GET_ADDRESS)) {
			try {
				// Protocol.GET_ADDRESS + ":" + address
				Address address = new Address(it.next());
				Content data = agent.get(address);
				if (data == null) {
					return ADDRESS_NOT_FOUND;
				}
				// serialize it.
				return ds().serializer.serialize(data);
			} catch (Exception e) {
				LOG.error(e);
				return ERROR;
			}
		}

		if (request.equalsIgnoreCase(GENERATE_CAPTCHA)) {
			try {
				Captcha captcha = new Captcha(agent);
				// serialize it.
				return ds().serializer.serialize(captcha);
			} catch (Exception e) {
				return ERROR;
			}

		}

		if (request.startsWith(CREATE_USER)) {
			try {
				// String request = Protocol.message(Protocol.CREATE_USER, data,
				// link, captcha,
				// answer);

				// TODO handle when user already exists.

				Data data = (Data) ds().serializer.deserialize(it.next());
				Link link = (Link) ds().serializer.deserialize(it.next());
				Captcha captcha = (Captcha) ds().serializer.deserialize(it.next());
				String answer = new String(it.next());

				captcha.check(agent, answer);

				Key key = link.getKey();
				Key targetKey = link.getTargetKey();
				if (agent.exists(key) || agent.exists(targetKey)) {
					throw new Exception(
							"it seems that the user already exists.");
				}

				agent.setWithLink(null, data, link);

				return SUCCESS;
			} catch (Exception e) {
				LOG.error(e);
				return ERROR;
			}

		}

		if (request.startsWith(CREATE_OBJECT)) {
			try {
				// String request = Protocol.CREATE_OBJECT + ":" + address + ":"
				// + serialize(content);

				Address address = new Address(it.next());
				Content content = (Content) JLG.deserialize(it.next());
				agent.store(address, content);
				// serialize it.
				return SUCCESS;
			} catch (Exception e) {
				LOG.error(e);
				return ERROR;
			}

		}

		if (request.startsWith(REMOVE_ADDRESS)) {
			try {
				Address address = (Address) JLG.deserialize(it.next());
				byte[] addressSignature = it.next();
				agent.remove(address, addressSignature);
				// serialize it.
				return SUCCESS;
			} catch (Exception e) {
				LOG.error(e);
				return ERROR;
			}

		}
		throw new Exception();
		//return "ERROR:OCP message not understood".getBytes();
	}

	public static Iterator<byte[]> iterator(byte[] input) {
		LinkedList<byte[]> list = new LinkedList<byte[]>();
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(input);
			DataInputStream dis = new DataInputStream(bis);
			int b = -2;
			while ((b != SEPARATOR) && (b != -1)) {
				b = dis.read();
			}
			while (true) {
				try {
					int length = dis.readInt();
					byte[] serialized = new byte[length];
					dis.read(serialized, 0, length);
					list.addLast(serialized);
				} catch (EOFException e) {
					break;
				}
			}
			dis.close();
			bis.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list.iterator();
	}

	public static byte[] hasBeenDetached(OCPContact contact) {
		return ("INFORM_DETACH:" + contact.getName()).getBytes();
	}

	public byte[] message(String function, Object... objects)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		dos.write(function.getBytes());
		dos.write(SEPARATOR);
		for (int i = 0; i < objects.length; i++) {
			byte[] serialized = null;

			if (objects[i].getClass() == byte[].class) {

				serialized = (byte[]) objects[i];
			} else if (objects[i].getClass() == String.class) {
				serialized = ((String) objects[i]).getBytes();
			} else {
				serialized = ds().serializer.serialize((Serializable) objects[i]);
			}
			dos.writeInt(serialized.length);
			dos.write(serialized);
		}
		dos.flush();

		byte[] result = baos.toByteArray();
		dos.close();
		baos.close();
		return result;
	}

}
