package org.ocpteamx.protocol.ocp;

import org.ocpteam.misc.ByteUtil;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;

public class Link extends Content {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Key key;
	private Key targetKey;

	public Link() {
	}

	public Link(OCPUser user, OCPAgent agent, Key key, Key targetKey)
			throws Exception {
		this.setKey(key);
		this.targetKey = targetKey;
		this.signature = user.sign(agent, ByteUtil.concat(getContent()));
		this.username = user.getUsername().getBytes();
	}

	public Key getTargetKey() {
		return targetKey;
	}

	@Override
	public Key getKey(OCPAgent agent) {
		return key;
	}

	public Key getKey() {
		return key;
	}

	@Override
	public byte[] getContent() throws Exception {
		return ByteUtil.concat(key.getBytes(), targetKey.getBytes());
	}

	public void setKey(Key key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return JLG.join("|", this.getClass(), new String(username),
				JLG.bytesToHex(signature), key, targetKey);
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		if (targetKey != null) {
			result.setBinField("targetKey", targetKey.getBytes());
		} else {
			result.setBinField("targetKey", null);
		}
		if (key != null) {
			result.setBinField("key", key.getBytes());
		} else {
			result.setBinField("key", null);
		}
		result.setBinField("signature", signature);
		result.setBinField("username", username);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		targetKey = new Key(s.getBinField("targetKey"));
		setKey(new Key(s.getBinField("key")));
		username = s.getBinField("username");
		signature = s.getBinField("signature");
	}
}
