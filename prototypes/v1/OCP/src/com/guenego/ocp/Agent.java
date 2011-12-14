package com.guenego.ocp;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.guenego.misc.ByteUtil;
import com.guenego.misc.Id;
import com.guenego.misc.JLG;
import com.guenego.misc.JLGException;

public class Agent {

	public Id id;
	private String name;

	public KeyPair keyPair;
	private SecretKey secretKey;
	private Cipher cipher;
	public String signatureAlgorithm;

	public Storage storage;

	public Server server;
	public Properties p;
	public Client client;
	public Properties network;

	// these two attributes are corelated
	// all access to them must be synchronized
	private Map<Id, Contact> contactMap; // contactid->contact
	private NavigableMap<Id, Contact> nodeMap; // nodeid->contact

	private SecretKeyFactory userSecretKeyFactory;
	private Cipher userCipher;
	private PBEParameterSpec userParamSpec;
	private byte backupNbr;

	public static void main(String[] args) {
		try {
			Agent agent = new Agent();
			agent.start();
		} catch (Exception e) {
			JLG.error(e);
		}
	}
	
	public Agent() throws Exception {
		this(null);
	}

	public Agent(Properties properties) throws Exception {
		contactMap = new HashMap<Id, Contact>();
		nodeMap = new TreeMap<Id, Contact>();

		if (properties == null) {
			p = new Properties();
			try {
				this.p.load(new FileInputStream("agent.properties"));
			} catch (IOException e) {
			}
		} else {
			p = properties;
		}
		
		

		for (Enumeration<Object> e = p.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = p.getProperty(key);
			JLG.debug(key + "=" + value);
		}
		client = new Client(this);
		name = p.getProperty("name", "anonymous");

		// each agent has its own symmetric key cipher
		KeyGenerator keyGen = KeyGenerator.getInstance(this.p.getProperty(
				"cypher.algo", "AES"));
		keyGen.init(Integer.parseInt(this.p
				.getProperty("cipher.keysize", "128")));
		secretKey = keyGen.generateKey();
		cipher = Cipher.getInstance(this.p.getProperty("cipher.algo", "AES"));

		// user cipher
		byte[] salt = { 1, 1, 1, 2, 2, 2, 3, 3 };
		int count = 20;
		userParamSpec = new PBEParameterSpec(salt, count);

		// Storage
		storage = new Storage(this);
		
		// start an icontray or a commandline listener
		UserInterface ui = null;
		if (p.getProperty("gui", "true").equalsIgnoreCase("true")) {
			// gui mode
			ui = new GraphicalUI(this);
		} else {
			ui = new CommandLine(this);
		}
		(new Thread(ui)).start();
	}

	public void start() throws JLGException {
		JLG.debug("starting agent " + name);
		try {
			if (isFirstAgent()) {
				JLG.debug("This is the first agent on the network");
				if (network == null) {
					network = new Properties();
					network.load(new FileInputStream("network.properties"));
				}
			} else {
				// even if network is not null...
				network = client.getNetworkProperties();
			}

			try {
				backupNbr = (byte) Integer.parseInt(network.getProperty(
						"backupNbr", "5"));
			} catch (NumberFormatException e) {
				throw new Exception(
						"network property error: backupNbr must be an integer between 1 and 255.");
			}
			if (backupNbr < 1) {
				throw new Exception(
						"network property error: backupNbr must be an integer between 1 and 255.");
			}

			id = generateId();
			JLG.debug("agent id = " + id);

			// all agent must have a PKI
			keyPair = generateKeyPair();
			signatureAlgorithm = network.getProperty("SignatureAlgo",
					"SHA1withDSA");

			// all users use the same algo for symmetric encryption
			userSecretKeyFactory = SecretKeyFactory.getInstance(network
					.getProperty("user.cipher.algo", "PBEWithMD5AndDES"));
			userCipher = Cipher.getInstance(network.getProperty(
					"user.cipher.algo", "PBEWithMD5AndDES"));

			if (p.getProperty("server", "yes").equals("yes")) {
				server = new Server(this);
				server.start();
				attach();
				Contact myself = toContact();
				addContact(myself);

			}
		} catch (Exception e) {
			throw new JLGException(e);
		}
	}

	private void attach() throws Exception {
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

	public synchronized Queue<Contact> makeContactQueue(Id key)
			throws Exception {
		Queue<Contact> contactQueue = new LinkedList<Contact>();
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
		return contactMap.values().iterator();
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

	public String getName() {
		return name;
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

	public Contact toContact() {
		// convert the agent public information into a contact
		Contact c = new Contact(this.id);
		c.publicKey = this.keyPair.getPublic().getEncoded();
		// add the listener url and node id information
		if (server != null) {
			Iterator<Listener> it = server.listenerList.iterator();
			while (it.hasNext()) {
				Listener l = it.next();
				c.addURL(l.getUrl().toString());
			}
			Iterator<Id> itn = storage.nodeSet.iterator();
			while (itn.hasNext()) {
				Id nodeId = (Id) itn.next();
				c.nodeIdSet.add(nodeId);
			}
		}
		return c;
	}

	public Captcha wantToCreateUser(String login, String password)
			throws Exception {
		JLG.debug("want to create a user");
		Id key = hash(ucrypt(password, login + password));
		JLG.debug("key = " + key);
		Queue<Contact> contactQueue = makeContactQueue(key);
		JLG.debug("contact queue established.");
		return client.askCaptcha(contactQueue);
	}

	public void createUser(String login, String password, int backupNbr,
			Captcha captcha, String answer) throws Exception {

		User user = new User(this, login, backupNbr);
		UserPublicInfo upi = user.getPublicInfo(this);

		Contact contact = getContact(captcha.contactId);

		// 1) create the public part of the user.
		// catpcha is required in order to avoid massive fake user creation
		ObjectData publicUserData = new ObjectData(this, user, upi);
		Link publicUserDataLink = new Link(user, this, UserPublicInfo.getKey(
				this, login), publicUserData.getKey(this));

		client.createUser(contact, publicUserData, publicUserDataLink, captcha,
				answer);

		// 2) create the private part of the user.
		// no need captcha because creation of object is checked by the user
		// public info
		Key key = new Key(hash(ucrypt(password, login + password)));
		byte[] content = ucrypt(password, JLG.serialize(user));
		Content privateUserData = new Data(this, user, content);
		Link privateUserDataLink = new Link(user, this, key,
				privateUserData.getKey(this));

		setWithLink(user, privateUserData, privateUserDataLink);

	}

	public User login(String login, String password) throws Exception {
		Id key = hash(ucrypt(password, login + password));
		byte[] content = client.getUser(key);
		if (content == null || content.length == 0) {
			throw new Exception("user unknown");
		}
		User user = (User) JLG.deserialize(udecrypt(password, content));
		if (user == null) {
			throw new Exception("user unknown");
		}
		return user;
	}

	public synchronized Contact getContact(Id contactId) throws Exception {
		Contact contact = contactMap.get(contactId);
		if (contact == null) {
			throw new Exception("contact is null");
		}
		return contact;
	}

	public Id getNodeId(Address address) throws Exception {
		if (nodeMap.size() == 0) {
			throw new Exception("nodeMap is not populated.");
		}
		Id nodeId = nodeMap.floorKey(address);
		if (nodeId == null) {
			nodeId = nodeMap.lastKey();
		}
		// if (nodeId == null) {
		// throw new Exception("nodeMap is not populated at all");
		// }
		return nodeId;
	}

	public Contact getContactFromNodeId(Id nodeId) {
		return nodeMap.get(nodeId);
	}

	public void declareContact() throws Exception {
		Contact contact = toContact();
		client.sendAll(Protocol.DECLARE_CONTACT + ":" + JLG.serialize(contact));

	}

	@Override
	public String toString() {
		String hasServer = "no";
		if (server != null) {
			hasServer = "yes";
		}
		String result = "Agent " + this.name + ":" + JLG.NL + "agentId=" + id
				+ JLG.NL + "hasServer=" + hasServer + JLG.NL;
		if (server != null) {
			result += server.toString() + JLG.NL;
		}
		result += "Contacts:" + JLG.NL;
		synchronized (this) {
			Iterator<Id> it = contactMap.keySet().iterator();
			while (it.hasNext()) {
				Id id = (Id) it.next();
				Contact contact = contactMap.get(id);
				result += id + "->" + contact + JLG.NL;
			}
		}
		result += "Nodes:" + JLG.NL;
		synchronized (this) {
			Iterator<Id> it = nodeMap.keySet().iterator();
			while (it.hasNext()) {
				Id id = (Id) it.next();
				Contact contact = nodeMap.get(id);
				result += id + "->" + contact + JLG.NL;
			}
		}

		result += "Storage:" + JLG.NL;
		result += storage;

		return result;
	}

	public void setWithLink(User user, Content data, Link link)
			throws Exception {
		// if a link already exists with the same key, then delete it.
		Key key = link.getKey();
		if (exists(key)) {
			// remove both the link and its targets recursively
			remove(user, key);
		}

		Key targetKey = link.getTargetKey();
		if (exists(targetKey)) {
			remove(user, targetKey);
		}

		// check that the link targetKey is the data key
		checkLink(data, link);

		Address[] address = getAddressList(key);
		for (byte i = 0; i < address.length; i++) {
			store(address[i], link);
		}

		address = getAddressList(targetKey);
		for (byte i = 0; i < address.length; i++) {
			store(address[i], data);
		}
	}

	public boolean exists(Key key) throws Exception {
		Address[] address = getAddressList(key);
		for (int i = 0; i < address.length; i++) {
			if (get(address[i]) != null) {
				return true;
			}
		}
		return false;
	}

	private void checkLink(Content data, Link link) throws Exception {
		if (!data.getKey(this).equals(link.getTargetKey())) {
			throw new Exception("Link corrupted.");
		}

	}

	private Key set(Data data) throws Exception {
		Key contentKey = data.getKey(this);
		Address[] address = getAddressList(contentKey);
		for (byte i = 0; i < address.length; i++) {
			store(address[i], data);
		}
		return contentKey;
	}

	void store(Address address, Content content) throws Exception {
		if (isResponsible(address)) {
			// local (even if this should be store elsewhere, store this for
			// cache purpose.
			JLG.debug("local store");
			storage.put(address, content);
		} else {
			// transfer the order to another agent
			JLG.debug("transfert the order");
			Id nodeId = getNodeId(address);
			Queue<Contact> contactQueue = makeContactQueue(nodeId);
			client.store(contactQueue, address, content);
		}
	}

	public Content get(Key key) throws Exception {
		// TODO retrieve the bkp address, then look at their content
		// if the content is a link (LINK:<key>), then recursively retrieve the
		// content
		// of the link.
		JLG.debug("getFromKey:" + key);
		Content result = null;
		Queue<Address> queue = new LinkedList<Address>();
		Address[] address = getAddressList(key);
		Content[] contentArray = new Content[address.length];
		for (int i = 0; i < address.length; i++) {
			contentArray[i] = get(address[i]);
			if (contentArray[i] == null) {
				queue.offer(address[i]);
				// TODO reassign content to address
			} else {
				result = contentArray[i];
			}
		}
		if (result == null) {
			return null;
		}
		if (result.isLink()) {
			Link link = (Link) result;
			JLG.debug("key " + key + " is a link on " + link.getTargetKey());
			result = get(link.getTargetKey());
		}
		return result;
	}

	public Content get(Address address) throws Exception {
		// do I have this value on myself ?
		Content result = storage.get(address);
		if (result == null) {
			if (isResponsible(address)) {
				JLG.debug("address not found on " + id + " : " + address);
				JLG.debug(this.toString());
				return null;
			} else {
				result = client.getFromAddress(address);
			}
		}
		return result;
	}

	public boolean isResponsible(Address address) throws Exception {
		Id nodeId = getNodeId(address);
		Contact contact = getContactFromNodeId(nodeId);
		return contact.id.equals(id);
	}

	public synchronized Id getNext(Id nodeId) {
		Id nextNodeId = nodeMap.navigableKeySet().higher(nodeId);
		if (nextNodeId == null) {
			nextNodeId = nodeMap.navigableKeySet().first();
		}
		return nextNodeId;
	}

	private Address[] getAddressList(Key key) throws Exception {
		Address[] result = new Address[backupNbr];
		for (byte i = 0; i < backupNbr; i++) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(i);
			baos.write(key.getBytes());
			Address address = new Address(hash(baos.toByteArray()));
			baos.close();
			result[i] = address;
		}
		JLG.debug("address from key " + key + ":" + JLG.NL
				+ JLG.join(JLG.NL, (Object[]) result));
		return result;
	}

	public Set<Id> getIndex(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pointer set(User user, Serializable serializable) throws Exception {
		return set(user, JLG.serialize(serializable).getBytes());
	}

	public Pointer set(User user, byte[] bytes) throws Exception {
		// 1) Create all the data objects
		Key[] keys = new Key[user.getBackupNbr()];
		for (int i = 0; i < user.getBackupNbr(); i++) {
			byte[] bkp = new byte[1];
			bkp[0] = (byte) i;
			byte[] bkpBytes = ByteUtil.concat(bkp, bytes);
			byte[] ciphertext = user.crypt(bkpBytes);
			Data data = new Data(this, user, ciphertext);
			keys[i] = set(data);
		}
		// 2) create the pointer link
		Pointer pointer = makePointer(user, keys);
		// 3 ) add the pointer to the user index.
		user.add(this, pointer);
		return pointer;
	}

	private Pointer makePointer(User user, Key[] keys) throws Exception {
		Data data = new Data(this, user, user.crypt(JLG.serialize(keys)
				.getBytes()));
		Pointer pointer = new Pointer(set(data).getBytes());
		return pointer;
	}

	public Serializable get(User user, Pointer pointer) throws Exception {
		return JLG.deserialize(new String(getBytes(user, pointer)));
	}

	public byte[] getBytes(User user, Pointer pointer) throws Exception {
		// 1) retrieve the key list from pointer
		Key[] keys = getKeys(user, pointer);
		// 2) from each key retrieve the object
		if (keys.length != user.getBackupNbr()) {
			throw new Exception("nbr of keys different from user backup nbr.");
		}
		byte[] result = null;
		// TODO : check if all bkp are identical and repair if necessary
		for (int i = 0; i < keys.length; i++) {
			Data data = (Data) get(keys[i]);
			if (data != null) {
				byte[] ciphertext = data.getContent();
				byte[] cleartext = user.decrypt(ciphertext);
				result = ByteUtil.sub(cleartext, 1);

				break;
			}
		}
		return result;
	}

	private Key[] getKeys(User user, Pointer pointer) throws Exception {
		Key pointerKey = pointer.asKey();
		Data data = (Data) get(pointerKey);
		if (data == null) {
			throw new Exception("Cannot get keys for pointer " + pointer);
		}
		byte[] ciphertext = data.getContent();
		byte[] cleartext = user.decrypt(ciphertext);
		Key[] keys = (Key[]) JLG.deserialize(new String(cleartext));
		return keys;
	}

	public void remove(User user, Pointer pointer) throws Exception {
		// 1) retrieve and remove the key list from pointer
		Key[] keys = getKeys(user, pointer);
		for (int i = 0; i < keys.length; i++) {
			remove(user, keys[i]);
		}
		// 2) remove the pointer as a key
		remove(user, pointer.asKey());
		// 3) remove the pointer from the user index
		user.remove(this, pointer);
	}

	public void remove(User user, Key key) throws Exception {
		if (isLink(key)) {
			Link link = getLink(key);
			remove(user, link.getTargetKey());
		}
		// remove the address corresponding to the key.
		Address[] address = getAddressList(key);
		for (byte i = 0; i < address.length; i++) {
			// sign the address
			byte[] addressSignature = user.sign(this, address[i].getBytes());
			remove(address[i], addressSignature);
		}
	}

	private Link getLink(Key key) throws Exception {
		Address[] addresses = getAddressList(key);
		Content content = null;
		Iterator<Address> it = Arrays.asList(addresses).iterator();
		while (content == null && it.hasNext()) {
			Address address = it.next();
			content = get(address);
		}
		if (content == null) {
			throw new Exception("no data found for key = " + key);
		} else if (content.isLink()) {
			return (Link) content;
		} else {
			throw new Exception("data found but it's not a link. for key = "
					+ key);
		}
	}

	private boolean isLink(Key key) throws Exception {
		Address[] addresses = getAddressList(key);
		Content content = null;
		Iterator<Address> it = Arrays.asList(addresses).iterator();
		while (content == null && it.hasNext()) {
			Address address = it.next();
			content = get(address);
		}
		if (content == null) {
			return false;
		} else {
			return content.isLink();
		}
	}

	public void remove(Address address, byte[] addressSignature)
			throws Exception {
		// TODO Auto-generated method stub
		// do I have this value on myself ?
		if (storage.contains(address)) {
			storage.remove(address, addressSignature);
		}
		if (!isResponsible(address)) {
			client.remove(address, addressSignature);
		}

	}

	public UserPublicInfo getUserPublicInfo(byte[] username) throws Exception {
		Key key = UserPublicInfo.getKey(this, new String(username));
		ObjectData data = (ObjectData) get(key);
		if (data == null) {
			throw new Exception("Cannot get the user public info for "
					+ new String(username));
		}
		UserPublicInfo upi = (UserPublicInfo) data.getObject();
		return upi;
	}

}
