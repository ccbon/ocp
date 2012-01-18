package com.guenego.ocp;

import java.io.Serializable;
import java.security.PublicKey;
import java.security.Signature;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;

public class UserPublicInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PublicKey publicKey;
	private String login;
	private Key key;

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public String toString() {
		return "UPI:login=" + login + ";public_key="
				+ JLG.bytesToHex(publicKey.getEncoded());
	}

	public String getLogin() {
		return login;
	}

	public PublicKey getPubliKey() {
		return publicKey;
	}

	public boolean verify(Agent agent, byte[] cleartext, byte[] signature)
			throws Exception {
		Signature s = Signature.getInstance(agent.signatureAlgorithm);
		s.initVerify(publicKey);
		s.update(cleartext);
		return s.verify(signature);
	}

	public static Key getKey(Agent agent, String login) throws Exception {
		return new Key(agent.hash(login.getBytes()));
	}

	public Key getKey() {
		return this.key;
	}

	public void setKey(Key key) {
		this.key = key;
		
	}
}
