package org.ocpteam.serializable;

import java.security.KeyPair;

import javax.crypto.SecretKey;


public class SecureUser extends AddressUser {

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
	
}
