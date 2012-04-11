package org.ocpteam.protocol.ocp;

import java.util.Queue;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.Client;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Context;
import org.ocpteam.entity.InputMessage;
import org.ocpteam.entity.Response;
import org.ocpteam.entity.User;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

public class OCPClient extends Client implements IAuthenticable {

	public OCPClient() throws Exception {
		super();
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		agent = (OCPAgent) ds().getComponent(Agent.class);
	}

	private OCPAgent agent;

	public Id[] requestNodeId() throws Exception {
		Id[] nodeIds = null;
		// at this time we ask to the network to give us one node_id.
		JLG.debug("request node id");
		OCPModule m = ds().getComponent(OCPModule.class);
		JLG.debug("module class: " + m.getClass());
		Response response = request(new InputMessage(m.requestNodeId()));
		nodeIds = new Id[1];
		nodeIds[0] = (Id) response.getObject();
		return nodeIds;
	}

	public Captcha askCaptcha(Queue<Contact> contactQueue) throws Exception {
		Response r = request(contactQueue, OCPProtocol.GENERATE_CAPTCHA.getBytes());
		Captcha captcha = (Captcha) JLG.deserialize((byte[]) r.getObject());
		JLG.debug("captcha content = " + captcha);
		// if (!captcha.checkSignature(r.getContact())) {
		// throw new Exception("captcha signature not consistant");
		// }

		return captcha;
	}

	public void createUser(Contact contact, ObjectData data, Link link,
			Captcha captcha, String answer) throws Exception {
		byte[] request = OCPProtocol.message(OCPProtocol.CREATE_USER, data, link,
				captcha, answer);
		request(contact, request);
	}

	public void store(Queue<Contact> contactQueue, Address address,
			Content content) throws Exception {
		// store an object at given address and put the content.
		byte[] request = OCPProtocol.message(OCPProtocol.CREATE_OBJECT,
				address.getBytes(), content);
		Response response = request(contactQueue, request);
		response.checkForError();
	}

	public byte[] getUser(Id key) throws Exception {
		Response r = request(agent.makeContactQueue(key),
				OCPProtocol.message(OCPProtocol.GET_USER, key.getBytes()));
		r.checkForError();
		return (byte[]) r.getObject();
	}

	public Content getFromAddress(Address address) throws Exception {
		Response r = request(agent.makeContactQueue(address),
				OCPProtocol.message(OCPProtocol.GET_ADDRESS, address.getBytes()));
		r.checkForError();
		if (new String((byte[]) r.getObject()).equals(new String(
				OCPProtocol.ADDRESS_NOT_FOUND))) {
			return null;
		}
		return (Content) JLG.deserialize((byte[]) r.getObject());
	}

	public void remove(Address address, byte[] addressSignature)
			throws Exception {
		Response r = request(agent.makeContactQueue(address), OCPProtocol.message(
				OCPProtocol.REMOVE_ADDRESS, address, addressSignature));
		r.checkForError();
	}

	public void declareSponsor() {
		try {
			if (agent.ds().getProperty("network.type", "private").equalsIgnoreCase(
					"public")) {
				int port = agent.toContact().getTcpPort();
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
				JLG.debug("result = " + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void login() throws Exception {
		try {
			Authentication a = ds().getComponent(Authentication.class);
			String password = (String) a.getChallenge();
			String login = a.getLogin();
			Id key = agent.hash(agent.ucrypt(password,
					(login + password).getBytes()));
			byte[] content = null;
			try {
				content = getUser(key);
			} catch (Exception e) {
			}
			if (content == null) {
				throw new Exception("user unknown");
			}
			User user = (User) JLG.deserialize(agent
					.udecrypt(password, content));
			if (user == null) {
				throw new Exception("user unknown");
			}
			IDataModel dm = new OCPFileSystem((OCPUser) user, agent);
			ds().setContext(new Context(dm, "/"));
			a.setUser(user);
		} catch (Exception e) {
			JLG.error(e);
			throw e;
		}
	}

	@Override
	public void logout() throws Exception {
		JLG.debug("ocp logout (nothing to do).");
	}

	public void setAgent(OCPAgent agent) {
		this.agent = agent;
	}

	
}
