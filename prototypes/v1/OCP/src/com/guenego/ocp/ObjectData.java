package com.guenego.ocp;

import java.io.Serializable;

import com.guenego.misc.JLG;

public class ObjectData extends Data {

	public ObjectData(OCPAgent agent, User user, Serializable serializable)
			throws Exception {
		super(agent, user, JLG.serialize(serializable).getBytes());
	}

	public Serializable getObject() throws Exception {
		return JLG.deserialize(new String(content));
	}

	@Override
	public String toString() {
		try {
			Object obj = getObject();
			return JLG.join("|", this.getClass(), new String(username),
					JLG.bytesToHex(signature),
					obj.toString());
		} catch (Exception e) {
			return super.toString();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
