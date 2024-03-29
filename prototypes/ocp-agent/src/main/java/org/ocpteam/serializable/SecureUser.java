package org.ocpteam.serializable;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
		return secretKeyAlgo;
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = super.toStructure();
		result.setName(this.getClass());
		result.setStringField("keyPairAlgo", keyPairAlgo);
		result.setStringField("signatureAlgo", signatureAlgo);
		result.setStringField("secretKeyAlgo", secretKeyAlgo);
		if (secretKey != null) {
			result.setBinField("secretKey", secretKey.getEncoded());
		} else {
			result.setBinField("secretKey", null);
		}
		if (keyPair != null) {
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
					keyPair.getPrivate().getEncoded());
			result.setBinField("privateKey",
					pkcs8EncodedKeySpec.getEncoded());
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
					keyPair.getPublic().getEncoded());
			result.setBinField("publicKey",
					x509EncodedKeySpec.getEncoded());
		} else {
			result.setBinField("publicKey", null);
			result.setBinField("privateKey", null);
		}
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		super.fromStructure(s);
		keyPairAlgo = s.getStringField("keyPairAlgo");
		signatureAlgo = s.getStringField("signatureAlgo");
		secretKeyAlgo = s.getStringField("secretKeyAlgo");

		byte[] secretKetEncoded = s.getBinField("secretKey");
		if (secretKetEncoded != null) {
			SecretKey secretKey = new SecretKeySpec(secretKetEncoded,
					secretKeyAlgo);
			setSecretKey(secretKey);
		}

		byte[] publicKeyEncoded = s.getBinField("publicKey");
		byte[] privateKeyEncoded = s.getBinField("privateKey");
		if (publicKeyEncoded != null && privateKeyEncoded != null) {
			KeyFactory keyFactory = KeyFactory.getInstance(keyPairAlgo);
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
					s.getBinField("publicKey"));
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
					s.getBinField("privateKey"));
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			KeyPair keyPair = new KeyPair(publicKey, privateKey);
			setKeyPair(keyPair);
		}
	}
}
