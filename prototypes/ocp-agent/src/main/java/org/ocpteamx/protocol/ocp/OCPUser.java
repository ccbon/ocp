package org.ocpteamx.protocol.ocp;

import java.io.File;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.Pointer;
import org.ocpteam.serializable.User;

public class OCPUser extends User {

	private Key indexKey;
	private Key rootKey;

	public KeyPair keyPair;
	private int backupNbr = 1;

	private SecretKey secretKey;
	private String cipherAlgo = "AES";
	private int keySize = 128;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OCPUser(OCPAgent agent, String username, int backupNbr)
			throws Exception {
		this.username = username;
		this.backupNbr = backupNbr;
		this.indexKey = new Key(agent.generateId()); // refer to the list of
														// pointer
		this.rootKey = new Key(agent.generateId()); // refer to the root file
													// system

		// generate a key pair for public key encryption
		this.keyPair = agent.generateKeyPair();

		// generate a private key for symmetric encryption
		KeyGenerator keyGen = KeyGenerator.getInstance(cipherAlgo);
		keyGen.init(keySize);
		secretKey = keyGen.generateKey();

	}

	public UserPublicInfo getPublicInfo(OCPAgent agent) throws Exception {
		UserPublicInfo upi = new UserPublicInfo();
		upi.setLogin(username);
		upi.setPublicKey(keyPair.getPublic());
		upi.setKey(new Key(agent.hash(username.getBytes())));
		return upi;
	}

	public byte[] sign(OCPAgent agent, byte[] content) throws Exception {
		Signature s = Signature.getInstance(agent.signatureAlgorithm);
		s.initSign(keyPair.getPrivate());
		s.update(content);
		return s.sign();
	}

	public int getBackupNbr() {
		return this.backupNbr;
	}

	public byte[] crypt(byte[] cleartext) throws Exception, BadPaddingException {
		Cipher cipher = Cipher.getInstance(cipherAlgo);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(cleartext);
	}

	public byte[] decrypt(byte[] ciphertext) throws Exception,
			BadPaddingException {
		Cipher cipher = Cipher.getInstance(cipherAlgo);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(ciphertext);
	}

	public void add(OCPAgent agent, Pointer pointer) throws Exception {
		UserIndex userIndex = getUserIndex(agent);
		userIndex.add(pointer);
		Data data = new Data(agent, this, crypt(JLG.serialize(userIndex)));
		Link link = new Link(this, agent, indexKey, data.getKey(agent));
		agent.setWithLink(this, data, link);
	}

	public UserIndex getUserIndex(OCPAgent agent) throws Exception {
		UserIndex userIndex = null;
		Data userIndexData = (Data) agent.get(indexKey);
		if (userIndexData == null) {
			userIndex = new UserIndex();
		} else {
			byte[] content = decrypt(userIndexData.getContent());
			userIndex = (UserIndex) JLG.deserialize(content);
		}

		return userIndex;
	}

	public void remove(OCPAgent agent, Pointer pointer) throws Exception {
		UserIndex userIndex = getUserIndex(agent);
		userIndex.remove(pointer);
		Data data = new Data(agent, this, crypt(JLG.serialize(userIndex)));
		Link link = new Link(this, agent, indexKey, data.getKey(agent));
		agent.setWithLink(this, data, link);

	}

	public Pointer getRootPointer(OCPAgent agent) throws Exception {
		Pointer pointer = null;
		Data pointerData = (Data) agent.get(rootKey);
		if (pointerData == null) {
			return null;
		} else {
			byte[] content = decrypt(pointerData.getContent());
			pointer = (Pointer) JLG.deserialize(content);
		}

		return pointer;
	}

	public void setRootPointer(OCPAgent agent, Pointer p) throws Exception {

		Data data = new Data(agent, this, crypt(JLG.serialize(p)));
		Link link = new Link(this, agent, rootKey, data.getKey(agent));
		agent.setWithLink(this, data, link);

	}

	public String getDefaultLocalDir() {
		return System.getProperty("user.home") + File.separator + "ocp"
				+ File.separator + getUsername();
	}

	@Override
	public String toString() {
		return "login=" + getUsername() + ";public_key="
				+ keyPair.getPublic().getAlgorithm() + "-"
				+ JLG.bytesToHex(keyPair.getPublic().getEncoded());
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = super.toStructure();
		result.setName(getClass());
		result.setIntField("backupNbr", backupNbr);
		result.setIntField("keySize", keySize);
		result.setStringField("cipherAlgo", cipherAlgo);

		if (indexKey != null) {
			result.setBinField("indexKey", indexKey.getBytes());
		} else {
			result.setBinField("indexKey", null);
		}

		if (rootKey != null) {
			result.setBinField("rootKey", rootKey.getBytes());
		} else {
			result.setBinField("rootKey", null);
		}

		if (secretKey != null) {
			result.setBinField("secretKey", secretKey.getEncoded());
		} else {
			result.setBinField("secretKey", null);
		}

		if (keyPair != null) {
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
					keyPair.getPrivate().getEncoded());
			result.setBinField("privateKey", pkcs8EncodedKeySpec.getEncoded());
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
					keyPair.getPublic().getEncoded());
			result.setBinField("publicKey", x509EncodedKeySpec.getEncoded());
		} else {
			result.setBinField("publicKey", null);
			result.setBinField("privateKey", null);
		}
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		super.fromStructure(s);
		backupNbr = s.getIntField("backupNbr");
		keySize = s.getIntField("keySize");
		cipherAlgo = s.getStringField("cipherAlgo");
		
		KeyGenerator keyGen = KeyGenerator.getInstance(cipherAlgo);
		keyGen.init(keySize);
		secretKey = keyGen.generateKey();
		
		indexKey = new Key(s.getBinField("indexKey"));
		rootKey = new Key(s.getBinField("rootKey"));

		byte[] publicKeyEncoded = s.getBinField("publicKey");
		byte[] privateKeyEncoded = s.getBinField("privateKey");
		if (publicKeyEncoded != null && privateKeyEncoded != null) {
			KeyFactory keyFactory = KeyFactory.getInstance(cipherAlgo);
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
					s.getBinField("publicKey"));
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
					s.getBinField("privateKey"));
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			keyPair = new KeyPair(publicKey, privateKey);
		}
	}

}
