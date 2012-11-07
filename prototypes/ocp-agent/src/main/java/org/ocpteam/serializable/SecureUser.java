package org.ocpteam.serializable;

import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;


public class SecureUser extends AddressUser implements IStructurable {

	private static final long serialVersionUID = 1L;
	private KeyPair keyPair;
	private SecretKey secretKey;
	
	private String keyPairAlgo = "DSA";
	private String signatureAlgo;
	private String secretKeyAlgo = "AES";
	
	public KeyPair getKeyPair() {
		return keyPair;
	}
	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}
	public SecretKey getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}
	public String getSignatureAlgo() {
		return signatureAlgo;
	}
	public void setSignatureAlgo(String signatureAlgo) {
		this.signatureAlgo = signatureAlgo;
	}
	public String getKeyPairAlgo() {
		return keyPairAlgo;
	}
	public String getSecretKeyAlgo() {
		return secretKeyAlgo ;
	}
	
	@Override
	public Structure toStructure() throws Exception {
		Structure result = super.toStructure();
		result.rename(this.getClass());
//		result.setField("keyPair", "substruct", keyPair);
		result.setStringField("keyPairAlgo", keyPairAlgo);
		result.setStringField("signatureAlgo", signatureAlgo);
		result.setStringField("secretKeyAlgo", secretKeyAlgo);
		return result;
	}
	
	@Override
	public void fromStructure(Structure s) throws Exception {
		super.fromStructure(s);
		//setKeyPair(s.getSubstruct("keyPair").toObject());
		keyPairAlgo = s.getString("keyPairAlgo");
		signatureAlgo = s.getString("signatureAlgo");
		secretKeyAlgo = s.getString("secretKeyAlgo");
	}
	
}
