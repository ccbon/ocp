package com.guenego.ocp;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.Queue;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.guenego.misc.Id;
import com.guenego.misc.JLG;

public abstract class Agent {

	public String name;

	public KeyPair keyPair;
	protected SecretKey secretKey;
	protected Cipher cipher;
	public String signatureAlgorithm;

	public Storage storage;

	public Properties p;
	public Properties network;

	public Client client;
	public Server server;

	// these two attributes are corelated
	// all access to them must be synchronized
	protected Map<Id, Contact> contactMap; // contactid->contact
	protected NavigableMap<Id, Contact> nodeMap; // nodeid->contact

	protected SecretKeyFactory userSecretKeyFactory;
	protected Cipher userCipher;
	protected PBEParameterSpec userParamSpec;
	protected byte backupNbr;


	public Agent() {
		contactMap = new HashMap<Id, Contact>();
		nodeMap = new TreeMap<Id, Contact>();
	}

	public void loadConfig() throws Exception {
		if (!isConfigFilePresent()) {
			throw new Exception("Config file is not found. Expected Path: " + getConfigFile().getAbsolutePath());
		}
		p = new Properties();
		p.load(new FileInputStream(getConfigFile()));
		readConfig();
	}

	public abstract File getConfigFile();

	public void loadConfig(Properties properties) throws Exception {
		p = properties;
		readConfig();
	}
	
	protected abstract void readConfig() throws Exception;
	
	public abstract void start() throws Exception;
	protected void attach() throws Exception {
		storage.attach();
	}

	public Id generateId() throws Exception {
		MessageDigest md = MessageDigest.getInstance(network.getProperty(
				"hash", "SHA-1"));
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] input = new byte[200];
		random.nextBytes(input);
		return new Id(md.digest(input));
	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(network
				.getProperty("PKAlgo", "DSA"));
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		keyGen.initialize(1024, random);
		return keyGen.generateKeyPair();
	}

	public boolean isFirstAgent() {
		if (p == null) {
			JLG.debug("p is null");
		}
		String s = p.getProperty("server", "no");
		return s.equalsIgnoreCase("yes")
				&& p.getProperty("server.isFirstAgent", "no").equalsIgnoreCase(
						"yes");
	}

	public void stop() {
		if (server != null) {
			server.stop();
		}

	}

	public void setNetworkProperties(Properties network) {
		this.network = network;
	}

	public Queue<Contact> makeContactQueue(Id key)
			throws Exception {
		Queue<Contact> contactQueue = new LinkedList<Contact>();
		NavigableMap<Id, Contact> nodeMap = new TreeMap<Id, Contact>(this.nodeMap);
		if (nodeMap.containsKey(key)) {
			contactQueue.offer(nodeMap.get(key));
		}

		NavigableMap<Id, Contact> s = nodeMap.headMap(key, false);
		Iterator<Id> it = s.navigableKeySet().descendingIterator();
		while (it.hasNext()) {
			Id nodeId = it.next();
			Contact contact = s.get(nodeId);
			if (!contactQueue.contains(contact)) {
				contactQueue.offer(contact);
			}
		}
		s = nodeMap.tailMap(key, false);
		it = s.navigableKeySet().descendingIterator();
		while (it.hasNext()) {
			Id nodeId = it.next();
			Contact contact = s.get(nodeId);
			if (!contactQueue.contains(contact)) {
				contactQueue.offer(contact);
			}
		}
		return contactQueue;
	}

	public Queue<Contact> makeContactQueue() throws Exception {
		return makeContactQueue(new Id("0"));
	}

	public Id hash(byte[] input) throws Exception {
		MessageDigest md = MessageDigest.getInstance(network.getProperty(
				"hash", "SHA-1"));
		return new Id(md.digest(input));
	}

	public synchronized void addContact(Contact contact) throws Exception {

		contactMap.put(contact.id, contact);
		if (contact.nodeIdSet.size() == 0) {
			throw new Exception("contact without node.");
		}
		Iterator<Id> it = contact.nodeIdSet.iterator();
		while (it.hasNext()) {
			Id id = (Id) it.next();
			JLG.debug("adding node to nodeMap");
			nodeMap.put(id, contact);
		}
	}

	public Iterator<Contact> getContactIterator() {
		// we return a snapshot and not the modifiable contact list
		LinkedList<Contact> linkedList = new LinkedList<Contact>(contactMap.values());
		return linkedList.iterator();
	}

	public synchronized void removeContact(Contact contact) {
		contactMap.remove(contact.id);

	}

	public synchronized boolean hasNoContact() {
		return contactMap.size() == 0;
	}

	public synchronized boolean hasContact(Contact contact) {
		return contactMap.containsValue(contact);
	}

	public byte[] crypt(String string) throws Exception, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(string.getBytes());
	}

	public String decrypt(byte[] ciphertext) throws Exception,
			BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(ciphertext));
	}

	public byte[] ucrypt(String password, String string) throws Exception,
			BadPaddingException {
		SecretKey secretKey = generateSecretKey(password);

		userCipher.init(Cipher.ENCRYPT_MODE, secretKey, userParamSpec);
		return userCipher.doFinal(string.getBytes());
	}

	public String udecrypt(String password, byte[] ciphertext)
			throws Exception, BadPaddingException {
		SecretKey secretKey = generateSecretKey(password);
		userCipher.init(Cipher.DECRYPT_MODE, secretKey, userParamSpec);
		return new String(userCipher.doFinal(ciphertext));
	}

	private SecretKey generateSecretKey(String password) throws Exception {
		return userSecretKeyFactory.generateSecret(new PBEKeySpec(password
				.toCharArray()));
	}


	public Captcha wantToCreateUser(String login, String password)
			throws Exception {
		// TODO check if user already exists ?
		JLG.debug("want to create a user");
		Id key = hash(ucrypt(password, login + password));
		JLG.debug("key = " + key);
		Queue<Contact> contactQueue = makeContactQueue(key);
		JLG.debug("contact queue established.");
		return client.askCaptcha(contactQueue);
	}






	public abstract boolean isConfigFilePresent();


}
