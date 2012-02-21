package org.ocpteam.protocol.ocp;

import java.io.Serializable;
import java.security.PublicKey;
import java.security.Signature;

import org.ocpteam.misc.JLG;


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

	public boolean verify(OCPAgent agent, byte[] cleartext, byte[] signature)
			throws Exception {
		Signature s = Signature.getInstance(agent.signatureAlgorithm);
		s.initVerify(publicKey);
		s.update(cleartext);
		return s.verify(signature);
	}

	public static Key getKey(OCPAgent agent, String login) throws Exception {
		return new Key(agent.hash(login.getBytes()));
	}

	public Key getKey() {
		return this.key;
	}

	public void setKey(Key key) {
		this.key = key;
		
	}
}
