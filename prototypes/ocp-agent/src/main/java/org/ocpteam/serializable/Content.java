package org.ocpteam.serializable;

import java.io.Serializable;

public class Content implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private byte[] value;
	private byte[] signature;

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

}
