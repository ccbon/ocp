package com.guenego.ocp;

import com.guenego.misc.JLG;

public class Data extends Content {

	protected byte[] content = null;
	
	public Data(Agent agent, User user, byte[] content) throws Exception {
		this.content = content;
		this.username = user.getLogin().getBytes();
		this.signature = user.sign(agent, content);
	}
	
	@Override
	public String toString() {
		return JLG.join("|", this.getClass() , new String(username),
				JLG.bytesToHex(signature), JLG.bytesToHex(content));
	}

	public byte[] getContent() throws Exception {
		return content;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Key getKey(Agent agent) throws Exception {
		return new Key(agent.hash(content));
	}

}
