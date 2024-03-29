package org.ocpteamx.protocol.ocp;

import java.util.Queue;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.ocpteam.component.Client;
import org.ocpteam.component.Protocol;
import org.ocpteam.entity.Context;
import org.ocpteam.entity.Response;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.InputMessage;

public class OCPClient extends Client implements IAuthenticable {

	public OCPClient() throws Exception {
		super();
	}

	@Override
	public void init() throws Exception {
		super.init();
		agent = ds().getComponent(OCPAgent.class);
		protocol = (OCPProtocol) ds().getComponent(Protocol.class);
	}

	@Override
	public OCPDataSource ds() {
		return (OCPDataSource) super.ds();
	}

	private OCPAgent agent;
	private OCPProtocol protocol;
	private String password;

	public Id[] requestNodeId() throws Exception {
		Id[] nodeIds = null;
		// at this time we ask to the network to give us one node_id.
		LOG.info("request node id");
		OCPModule m = ds().getComponent(OCPModule.class);
		LOG.info("module class: " + m.getClass());
		Response response = request(new InputMessage(m.requestNodeId()));
		nodeIds = new Id[1];
		nodeIds[0] = (Id) response.getObject();
		return nodeIds;
	}

	public Captcha askCaptcha(Queue<Contact> contactQueue) throws Exception {
		Response r = requestByPriority(contactQueue,
				OCPProtocol.GENERATE_CAPTCHA.getBytes());
		Captcha captcha = (Captcha) ds().serializer.deserialize((byte[]) r
				.getObject());
		LOG.info("captcha content = " + captcha);
		// if (!captcha.checkSignature(r.getContact())) {
		// throw new Exception("captcha signature not consistant");
		// }

		return captcha;
	}

	public void createUser(Contact contact, Data data, Link link,
			Captcha captcha, String answer) throws Exception {
		byte[] request = protocol.message(OCPProtocol.CREATE_USER, data, link,
				captcha, answer);
		request(contact, request);
	}

	public void store(Queue<Contact> contactQueue, Address address,
			Content content) throws Exception {
		// store an object at given address and put the content.
		byte[] request = protocol.message(OCPProtocol.CREATE_OBJECT,
				address.getBytes(), content);
		Response response = requestByPriority(contactQueue, request);
		response.checkForError();
	}

	public byte[] getUser(Id key) throws Exception {
		Response r = requestByPriority(agent.makeContactQueue(key),
				protocol.message(OCPProtocol.GET_USER, key.getBytes()));
		r.checkForError();
		return (byte[]) r.getObject();
	}

	public Content getFromAddress(Address address) throws Exception {
		Response r = requestByPriority(agent.makeContactQueue(address),
				protocol.message(OCPProtocol.GET_ADDRESS, address.getBytes()));
		r.checkForError();
		if (new String((byte[]) r.getObject()).equals(new String(
				OCPProtocol.ADDRESS_NOT_FOUND))) {
			return null;
		}
		return (Content) ds().serializer.deserialize((byte[]) r.getObject());
	}

	public void remove(Address address, byte[] addressSignature)
			throws Exception {
		Response r = requestByPriority(agent.makeContactQueue(address),
				protocol.message(OCPProtocol.REMOVE_ADDRESS, address,
						addressSignature));
		r.checkForError();
	}

	public void declareSponsor() {
		try {
			if (ds().getProperty("network.type", "private").equalsIgnoreCase(
					"public")) {
				int port = ds().toContact().getTcpPort();
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				// TODO: need a ocp dedicated web server. I use mine for the
				// time being.
				config.setServerURL(new java.net.URL(agent.ds().getProperty(
						"network.sponsor.url",
						OCPAgent.DEFAULT_SPONSOR_SERVER_URL)));
				XmlRpcClient client = new XmlRpcClient();
				client.setConfig(config);
				String result = (String) client.execute("add",
						new Object[] { "" + port });
				LOG.info("result = " + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void authenticate() throws Exception {
		try {
			IUserManagement a = ds().um;
			String password = (String) getChallenge();
			String login = a.getUsername();
			Id key = agent.hash(agent.ucrypt(password,
					(login + password).getBytes()));
			byte[] content = null;
			try {
				content = getUser(key);
			} catch (Exception e) {
			}
			if (content == null || new String(content).equals("ERROR")) {
				throw new Exception("user unknown");
			}
			OCPUser user = (OCPUser) ds().serializer.deserialize(agent
					.udecrypt(password, content));
			if (user == null) {
				throw new Exception("user unknown");
			}
			IDataModel dm = new OCPFileSystem((OCPUser) user, agent);
			ds().setContext(new Context(user, dm, "/"));
		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}

	public void setAgent(OCPAgent agent) {
		this.agent = agent;
	}

	@Override
	public void setChallenge(Object challenge) {
		this.password = (String) challenge;
	}

	@Override
	public Object getChallenge() {
		return password;
	}

	@Override
	public void unauthenticate() throws Exception {
		setChallenge(null);
	}
}
