package org.ocpteam.protocol.ocp;

import org.ocpteam.misc.JLG;

public class Data extends Content {

	protected byte[] content = null;
	
	public Data(OCPAgent agent, OCPUser user, byte[] content) throws Exception {
		this.content = content;
		this.username = user.getLogin().getBytes();
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

}
