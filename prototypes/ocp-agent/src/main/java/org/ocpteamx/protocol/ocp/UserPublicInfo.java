package org.ocpteamx.protocol.ocp;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;

public class UserPublicInfo implements IStructurable {

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

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		if (key != null) {
			result.setBinField("key", key.getBytes());
		} else {
			result.setNullField("key", Structure.TYPE_BYTES);
		}
		result.setStringField("login", login);
		if (publicKey != null) {
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
					publicKey.getEncoded());
			result.setBinField("publicKey", x509EncodedKeySpec.getEncoded());
			result.setStringField("publicKeyAlgo", publicKey.getAlgorithm());
		} else {
			result.setNullField("publicKey", Structure.TYPE_BYTES);
		}
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		setKey(new Key(s.getBin("key")));
		setLogin(s.getString("login"));
		if (s.getBin("publicKey") != null) {
			String publicKeyAlgo = s.getString("publicKeyAlgo");
			KeyFactory kf = KeyFactory.getInstance(publicKeyAlgo);
			byte[] publicKeySpec = s.getBin("publicKey");
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
					publicKeySpec);
			setPublicKey(kf.generatePublic(x509EncodedKeySpec));
		}
	}
}
