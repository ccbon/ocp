package org.ocpteamx.protocol.ocp;

import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;

public class Data extends Content {

	protected byte[] content = null;
	
	public Data() {
	}
	
	public Data(OCPAgent agent, OCPUser user, byte[] content) throws Exception {
		this.content = content;
		this.username = user.getUsername().getBytes();
		this.signature = user.sign(agent, content);
	}
	
	@Override
	public String toString() {
		return JLG.join("|", this.getClass() , new String(username),
				JLG.bytesToHex(signature), JLG.bytesToHex(content));
	}

	@Override
	public byte[] getContent() throws Exception {
		return content;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Key getKey(OCPAgent agent) throws Exception {
		return new Key(agent.hash(content));
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setBinField("content", getContent());
		result.setBinField("signature", signature);
		result.setBinField("username", username);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		content = s.getBin("content");
		signature = s.getBin("signature");
		username = s.getBin("username");
	}

}
