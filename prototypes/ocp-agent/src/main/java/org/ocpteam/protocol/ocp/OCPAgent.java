package org.ocpteam.protocol.ocp;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NavigableMap;
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

import org.ocpteam.component.Agent;
import org.ocpteam.component.Client;
import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.User;
import org.ocpteam.misc.ByteUtil;
import org.ocpteam.misc.Cache;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;

public class OCPAgent extends Agent {

	public static final String DEFAULT_SPONSOR_SERVER_URL = "http://guenego.com/ocp/ocp.php";

	public Id id;
	private String name;

	// symmetric encryption
	public KeyPair keyPair;
	protected SecretKey secretKey;
	protected Cipher cipher;

	// signature
	public String signatureAlgorithm;

	// storage
	public Storage storage;

	// nbr of technical backup
	protected byte backupNbr;

	// user
	protected SecretKeyFactory userSecretKeyFactory;
	protected Cipher userCipher;
	protected PBEParameterSpec userParamSpec;

	@Override
	public DSPDataSource ds() {
		return super.ds();
	}

	public byte[] ucrypt(String password, byte[] input) throws Exception,
			BadPaddingException {
		SecretKey secretKey = generateSecretKey(password);

		userCipher.init(Cipher.ENCRYPT_MODE, secretKey, userParamSpec);
		return userCipher.doFinal(input);
	}

	public byte[] udecrypt(String password, byte[] ciphertext)
			throws Exception, BadPaddingException {
		SecretKey secretKey = generateSecretKey(password);
		userCipher.init(Cipher.DECRYPT_MODE, secretKey, userParamSpec);
		return userCipher.doFinal(ciphertext);
	}

	private SecretKey generateSecretKey(String password) throws Exception {
		return userSecretKeyFactory.generateSecret(new PBEKeySpec(password
				.toCharArray()));
	}

	// these two attributes are corelated
	// all access to them must be synchronized
	public NavigableMap<Id, OCPContact> nodeMap; // nodeid->contact

	// it is a very basic way to improve perf...
	private Cache cache;
	private MessageDigest md;

	public OCPAgent() {
		super();
		nodeMap = new TreeMap<Id, OCPContact>();
		cache = new Cache();
	}

	public Id hash(byte[] input) throws Exception {
		return new Id(md.digest(input));
	}

	public Id generateId() throws Exception {
		MessageDigest md = MessageDigest.getInstance(ds().network.getProperty(
				"hash", "SHA-1"));
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] input = new byte[200];
		random.nextBytes(input);
		return new Id(md.digest(input));
	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ds().network
				.getProperty("PKAlgo", "DSA"));
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		keyGen.initialize(1024, random);
		return keyGen.generateKeyPair();
	}

	public void connect() throws Exception {

		String sId = ds().getProperty("id");
		if (sId == null) {
			id = generateId();
			ds().setProperty("id", id.toString());
		} else {
			id = new Id(sId);
		}
		JLG.debug("agent id = " + id);

		try {
			backupNbr = (byte) Integer.parseInt(ds().network.getProperty(
					"backupNbr", "5"));
		} catch (NumberFormatException e) {
			throw new Exception(
					"network property error: backupNbr must be an integer between 1 and 255.");
		}
		if (backupNbr < 1) {
			throw new Exception(
					"network property error: backupNbr must be an integer between 1 and 255.");
		}

		// message digest for hash
		md = MessageDigest.getInstance(ds().network
				.getProperty("hash", "SHA-1"));

		// all agent must have a PKI
		keyPair = generateKeyPair();
		signatureAlgorithm = ds().network.getProperty("SignatureAlgo",
				"SHA1withDSA");

		// all users use the same algo for symmetric encryption
		userSecretKeyFactory = SecretKeyFactory.getInstance(ds().network
				.getProperty("user.cipher.algo", "PBEWithMD5AndDES"));
		userCipher = Cipher.getInstance(ds().network.getProperty(
				"user.cipher.algo", "PBEWithMD5AndDES"));

		if (ds().getProperty("server", "yes").equals("yes")) {
			storage = new Storage(this);
			storage.attach();
		}
		JLG.debug("end");
	}

	public void disconnect() throws Exception {
		JLG.debug("disconnect");
		if (getServer() != null) {
			getServer().stop();
		}
	}


	@Override
	public String toString() {
		String hasServer = "no";
		if (getServer() != null) {
			hasServer = "yes";
		}
		String result = "Agent " + this.name + ":" + JLG.NL + "agentId=" + id
				+ JLG.NL + "hasServer=" + hasServer + JLG.NL;
		if (getServer() != null) {
			result += getServer().toString() + JLG.NL;
		}
		result += "Contacts:" + JLG.NL;
		ContactMap contactMap = ds().getComponent(ContactMap.class);
		result += "contact map: " + JLG.NL + contactMap;
		result += "Nodes:" + JLG.NL;
		synchronized (this) {
			Iterator<Id> it = nodeMap.keySet().iterator();
			while (it.hasNext()) {
				Id id = it.next();
				OCPContact contact = nodeMap.get(id);
				result += id + "->" + contact + JLG.NL;
			}
		}

		result += "Storage:" + JLG.NL;
		result += storage;

		return result;
	}

	public boolean isResponsible(Address address) throws Exception {
		JLG.debug("address = " + address);
		Id nodeId = getNodeId(address);
		JLG.debug("nodeId = " + nodeId);
		OCPContact contact = getContactFromNodeId(nodeId);
		JLG.debug("contact = " + contact);
		JLG.debug("is responsible: " + contact.getName().equals(id.toString()));
		return contact.getName().equals(id.toString());
		// return true;
	}

	public void remove(Address address, byte[] addressSignature)
			throws Exception {
		// TODO Auto-generated method stub
		// do I have this value on myself ?
		// if (storage.contains(address)) {
		storage.remove(address, addressSignature);
		// }
		if (!isResponsible(address)) {
			getClient().remove(address, addressSignature);
		}

	}

	void store(Address address, Content content) throws Exception {
		if (isResponsible(address)) {
			// local (even if this should be store elsewhere, store this for
			// cache purpose.
			// JLG.debug("local store");
			storage.put(address, content);
		} else {
			// transfer the order to another agent
			JLG.debug("transfert the order");
			Id nodeId = getNodeId(address);
			Queue<Contact> contactQueue = makeContactQueue(nodeId);
			getClient().store(contactQueue, address, content);
		}
	}

	public Content get(Address address) throws Exception {
		// do I have this value on myself ?
		Content result = storage.get(address);
		if (result == null) {
			if (isResponsible(address)) {
				// JLG.debug("address not found on " + id + " : " + address);
				return null;
			} else {
				result = getClient().getFromAddress(address);
			}
		}
		return result;
	}

	public void remove(OCPUser user, Key key) throws Exception {
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

	public void setWithLink(OCPUser user, Content data, Link link)
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
		Key key = data.getKey(this);
		Address[] address = getAddressList(key);
		for (byte i = 0; i < address.length; i++) {
			store(address[i], data);
		}
		return key;
	}

	public Content get(Key key) throws Exception {
		// TODO retrieve the bkp address, then look at their content
		// if the content is a link (LINK:<key>), then recursively retrieve the
		// content
		// of the link.
		// JLG.debug("getFromKey:" + key);
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
			// JLG.debug("key " + key + " is a link on " + link.getTargetKey());
			result = get(link.getTargetKey());
		}
		return result;
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
		return result;
	}

	public Set<Id> getIndex(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pointer set(OCPUser user, Serializable serializable)
			throws Exception {
		JLG.debug("set serializable: " + serializable.getClass());
		return set(user, JLG.serialize(serializable));
	}

	public Pointer set(OCPUser user, byte[] bytes) throws Exception {
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

	private Pointer makePointer(OCPUser user, Key[] keys) throws Exception {
		Data data = new Data(this, user, user.crypt(JLG.serialize(keys)));
		Pointer pointer = new Pointer(set(data).getBytes());
		return pointer;
	}

	public Serializable get(OCPUser user, Pointer pointer) throws Exception {
		return JLG.deserialize(getBytes(user, pointer));
	}

	public byte[] getBytes(OCPUser user, Pointer pointer) throws Exception {
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

	private Key[] getKeys(OCPUser user, Pointer pointer) throws Exception {
		Key pointerKey = pointer.asKey();
		Data data = (Data) get(pointerKey);
		if (data == null) {
			throw new Exception("Cannot get keys for pointer " + pointer);
		}
		byte[] ciphertext = data.getContent();
		byte[] cleartext = user.decrypt(ciphertext);
		Key[] keys = (Key[]) JLG.deserialize(cleartext);
		return keys;
	}

	public void remove(OCPUser user, Pointer pointer) throws Exception {
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

	public UserPublicInfo getUserPublicInfo(byte[] username) throws Exception {
		String sUsername = new String(username);
		UserPublicInfo upi = (UserPublicInfo) cache.get(UserPublicInfo.class,
				sUsername);
		if (upi != null) {
			return upi;
		}
		Key key = UserPublicInfo.getKey(this, sUsername);
		ObjectData data = (ObjectData) get(key);
		if (data == null) {
			throw new Exception("Cannot get the user public info for "
					+ sUsername);
		}
		upi = (UserPublicInfo) data.getObject();
		cache.put(UserPublicInfo.class, sUsername, upi);
		return upi;
	}

	public void createUser(String login, String password, int backupNbr,
			Captcha captcha, String answer) throws Exception {

		OCPUser user = new OCPUser(this, login, backupNbr);
		UserPublicInfo upi = user.getPublicInfo(this);

		ContactMap contactMap = ds().getComponent(ContactMap.class);
		Contact contact = contactMap.getContact(captcha.contactId);

		// 1) create the public part of the user.
		// catpcha is required in order to avoid massive fake user creation
		ObjectData publicUserData = new ObjectData(this, user, upi);
		Link publicUserDataLink = new Link(user, this, UserPublicInfo.getKey(
				this, login), publicUserData.getKey(this));

		getClient().createUser(contact, publicUserData, publicUserDataLink,
				captcha, answer);

		// 2) create the private part of the user.
		// no need captcha because creation of object is checked by the user
		// public info
		Key key = new Key(hash(ucrypt(password, (login + password).getBytes())));
		byte[] content = ucrypt(password, JLG.serialize(user));
		Content privateUserData = new Data(this, user, content);
		Link privateUserDataLink = new Link(user, this, key,
				privateUserData.getKey(this));

		setWithLink(user, privateUserData, privateUserDataLink);

	}

	public Id getNodeId(Address address) throws Exception {
		if (nodeMap.size() == 0) {
			throw new Exception("nodeMap is not populated.");
		}
		JLG.debug("nodeMap=" + nodeMap);
		Id nodeId = nodeMap.floorKey(address);
		if (nodeId == null) {
			nodeId = nodeMap.lastKey();
		}
		// if (nodeId == null) {
		// throw new Exception("nodeMap is not populated at all");
		// }
		return nodeId;
	}

	public OCPContact getContactFromNodeId(Id nodeId) {
		return nodeMap.get(nodeId);
	}

	public void readConfig() throws Exception {

		// debugging aspect
		if (ds().getProperty("debug", "false").equalsIgnoreCase("true")) {
			JLG.debug_on();
			JLG.debug("working directory = " + System.getProperty("user.dir"));
		}

		Iterator<String> it = ds().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = ds().getProperty(key);
			JLG.debug(key + "=" + value);
		}
		name = ds().getProperty("name", "anonymous");

		// each agent has its own symmetric key cipher
		// TODO: test with other algo than AES
		KeyGenerator keyGen = KeyGenerator.getInstance(this.ds().getProperty(
				"cypher.algo", "AES"));
		keyGen.init(Integer.parseInt(this.ds().getProperty("cipher.keysize",
				"128")));
		secretKey = keyGen.generateKey();
		cipher = Cipher
				.getInstance(this.ds().getProperty("cipher.algo", "AES"));

		// user cipher
		byte[] salt = { 1, 1, 1, 2, 2, 2, 3, 3 };
		int count = 20;
		userParamSpec = new PBEParameterSpec(salt, count);

	}

	public Queue<Contact> makeContactQueue(Id key) throws Exception {
		Queue<Contact> contactQueue = new LinkedList<Contact>();
		NavigableMap<Id, OCPContact> nodeMap = new TreeMap<Id, OCPContact>(
				this.nodeMap);
		if (nodeMap.containsKey(key)) {
			contactQueue.offer(nodeMap.get(key));
		}

		NavigableMap<Id, OCPContact> s = nodeMap.headMap(key, false);
		Iterator<Id> it = s.navigableKeySet().descendingIterator();
		while (it.hasNext()) {
			Id nodeId = it.next();
			OCPContact contact = s.get(nodeId);
			if (!contactQueue.contains(contact)) {
				contactQueue.offer(contact);
			}
		}
		s = nodeMap.tailMap(key, false);
		it = s.navigableKeySet().descendingIterator();
		while (it.hasNext()) {
			Id nodeId = it.next();
			OCPContact contact = s.get(nodeId);
			if (!contactQueue.contains(contact)) {
				contactQueue.offer(contact);
			}
		}
		return contactQueue;
	}

	public Queue<Contact> makeContactQueue() throws Exception {
		return makeContactQueue(new Id("0"));
	}

	public void addContact(Contact contact) throws Exception {
		JLG.debug("adding contact: " + contact);
		ds().contactMap.add(contact);
		OCPContact c = (OCPContact) contact;
		if (c.nodeIdSet.size() == 0) {
			throw new Exception("contact without node.");
		}
		Iterator<Id> it = c.nodeIdSet.iterator();
		while (it.hasNext()) {
			Id id = it.next();
			JLG.debug("adding node to nodeMap");
			nodeMap.put(id, c);
		}
	}

	public Contact removeContact(Contact contact) {
		ContactMap contactMap = ds().getComponent(ContactMap.class);
		OCPContact c = (OCPContact) contactMap.remove(contact);
		try {
			Iterator<Id> it = c.nodeIdSet.iterator();
			while (it.hasNext()) {
				Id id = it.next();
				JLG.debug("removing node to nodeMap");
				nodeMap.remove(id);
			}
			return c;
		} catch (Exception e) {
			return null;
		}
	}

	public Captcha wantToCreateUser(String login, String password)
			throws Exception {
		// TODO check if user already exists ?
		JLG.debug("want to create a user");
		Id key = hash(ucrypt(password, (login + password).getBytes()));
		JLG.debug("key = " + key);
		Queue<Contact> contactQueue = makeContactQueue(key);
		JLG.debug("contact queue established.");
		return getClient().askCaptcha(contactQueue);
	}

	public String getName() {
		return name;
	}

	public byte[] crypt(byte[] cleartext) throws Exception, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(cleartext);
	}

	public byte[] decrypt(byte[] ciphertext) throws Exception,
			BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(ciphertext);
	}

	public void removeStorage() {
		storage.removeAll();
	}

	@Override
	public OCPClient getClient() {
		return (OCPClient) ds().getComponent(Client.class);
	}

}
