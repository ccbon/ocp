package org.ocpteam.ocp;

import java.io.File;
import java.security.KeyPair;
import java.security.Signature;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.ocpteam.misc.JLG;
import org.ocpteam.storage.User;


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

	public OCPUser(OCPAgent agent, String login, int backupNbr)
			throws Exception {
		super(login);
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
		upi.setLogin(login);
		upi.setPublicKey(keyPair.getPublic());
		upi.setKey(new Key(agent.hash(login.getBytes())));
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

	@Override
	public String getDefaultLocalDir() {
		return System.getProperty("user.home") + File.separator + "ocp"
				+ File.separator + getLogin();
	}

	@Override
	public String toString() {
		return "login=" + login + ";public_key="
				+ keyPair.getPublic().getAlgorithm() + "-"
				+ JLG.bytesToHex(keyPair.getPublic().getEncoded());
	}

}
