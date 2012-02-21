package org.ocpteam.protocol.ocp;

import java.io.Serializable;

import org.ocpteam.misc.JLG;


public class ObjectData extends Data {

	public ObjectData(OCPAgent agent, OCPUser user, Serializable serializable)
			throws Exception {
		super(agent, user, JLG.serialize(serializable));
	}

	public Serializable getObject() throws Exception {
		return JLG.deserialize(content);
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
