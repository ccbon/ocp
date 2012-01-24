package org.guenego.ocp;

import org.guenego.misc.ByteUtil;
import org.guenego.misc.JLG;


public class Link extends Content {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Key key;
	private Key targetKey;

	public Link(OCPUser user, OCPAgent agent, Key key, Key targetKey) throws Exception {
		this.setKey(key);
		this.targetKey = targetKey;
		this.signature = user.sign(agent, ByteUtil.concat(getContent()));
		this.username = user.getLogin().getBytes();
	}
	
	public Key getTargetKey() {
		return targetKey;
	}

	public Key getKey(OCPAgent agent) {
		return key;
	}
	
	public Key getKey() {
		return key;
	}
	
	public byte[] getContent() throws Exception {
		return ByteUtil.concat(key.getBytes(), targetKey.getBytes());
	}

	public void setKey(Key key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return JLG.join("|", this.getClass() , new String(username),
				JLG.bytesToHex(signature), key, targetKey);
	}
}
