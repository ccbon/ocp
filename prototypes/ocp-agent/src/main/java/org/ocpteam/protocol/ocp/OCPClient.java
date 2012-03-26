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
import org.ocpteam.module.DSPModule;

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
		OCPModule m = (OCPModule) getProtocol().getComponent(DSPModule.class);
		JLG.debug("module class: " + m.getClass());
		byte[] input = getProtocol().getMessageSerializer().serializeInput(
				new InputMessage(m.requestNodeId()));
		Response response = request(input);
		nodeIds = new Id[1];
		nodeIds[0] = (Id) getProtocol().getMessageSerializer().deserializeOutput(response.getBytes());
		return nodeIds;
	}







	public void enrichContact(OCPContact contact) throws Exception {
		byte[] response = request(contact, OCPProtocol.GET_CONTACT.getBytes());
		OCPContact c = (OCPContact) JLG.deserialize(response);
		String host = contact.getUrlList().iterator().next().getHost();
		c.updateHost(host);
		contact.copy(c);
	}






	public Captcha askCaptcha(Queue<Contact> contactQueue) throws Exception {
		Response r = request(contactQueue, OCPProtocol.GENERATE_CAPTCHA.getBytes());
		Captcha captcha = (Captcha) JLG.deserialize(r.getBytes());
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
		send(contact, request);
	}

	public void store(Queue<Contact> contactQueue, Address address,
			Content content) throws Exception {
		// store an object at given address and put the content.
		byte[] request = OCPProtocol.message(OCPProtocol.CREATE_OBJECT,
				address.getBytes(), content);
		Response response = request(contactQueue, request);
		String r = new String(response.getBytes());
		if (!r.equals(new String(OCPProtocol.SUCCESS))) {
			throw new Exception("cannot store");
		}
	}

	public byte[] getUser(Id key) throws Exception {
		Response r = request(agent.makeContactQueue(key),
				OCPProtocol.message(OCPProtocol.GET_USER, key.getBytes()));
		r.checkForError();
		return r.getBytes();
	}

	public Content getFromAddress(Address address) throws Exception {
		Response r = request(agent.makeContactQueue(address),
				OCPProtocol.message(OCPProtocol.GET_ADDRESS, address.getBytes()));
		r.checkForError();
		if (new String(r.getBytes()).equals(new String(
				OCPProtocol.ADDRESS_NOT_FOUND))) {
			return null;
		}
		return (Content) JLG.deserialize(r.getBytes());
	}

	public void remove(Address address, byte[] addressSignature)
			throws Exception {
		Response r = request(agent.makeContactQueue(address), OCPProtocol.message(
				OCPProtocol.REMOVE_ADDRESS, address, addressSignature));
		r.checkForError();
	}

	public void declareSponsor() {
		try {
			if (agent.ds().get("network.type", "private").equalsIgnoreCase(
					"public")) {
				int port = agent.toContact().getUrlList().get(0).getPort();
				XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				// TODO: need a ocp dedicated web server. I use mine for the
				// time being.
				config.setServerURL(new java.net.URL(agent.ds().get(
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
