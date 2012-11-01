package org.ocpteam.serializable;

import java.io.Serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

public class Content implements Serializable, IStructurable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private byte[] value;
	private byte[] signature;
	
	public Content() {
	}

	public Content(String username, byte[] value, byte[] signature) {
		this.username = username;
		this.value = value;
		this.signature = signature;
	}

	public byte[] getValue() {
		return value;
	}

	public byte[] getSignature() {
		return signature;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(Content.class);
		result.setField("username", "string", getUsername());
		result.setField("value", "bytes", getValue());
		result.setField("signature", "bytes", getSignature());
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		username = s.getString("username");
		signature = s.getByteArray("signature");
		value = s.getByteArray("value");
	}

}
