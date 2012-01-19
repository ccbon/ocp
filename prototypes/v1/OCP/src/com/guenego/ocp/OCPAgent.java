package com.guenego.ocp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
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

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEParameterSpec;

import com.guenego.misc.ByteUtil;
import com.guenego.misc.Id;
import com.guenego.misc.JLG;
import com.guenego.misc.URL;
import com.guenego.storage.Agent;
import com.guenego.storage.FileInterface;
import com.guenego.storage.User;

public class OCPAgent extends Agent {
	private static final String AGENT_PROPERTIES_FILE = "agent.properties";
	private static final String NETWORK_PROPERTIES_FILE = "network.properties";

	public Id id;

	// these two attributes are corelated
	// all access to them must be synchronized
	protected Map<Id, Contact> contactMap; // contactid->contact
	protected NavigableMap<Id, Contact> nodeMap; // nodeid->contact

	public OCPAgent() {
		super();
		contactMap = new HashMap<Id, Contact>();
		nodeMap = new TreeMap<Id, Contact>();
	}

	@Override
	public boolean isConfigFilePresent() {
		return JLG.isFile(AGENT_PROPERTIES_FILE);
	}

	@Override
	public File getConfigFile() {
		return new File(AGENT_PROPERTIES_FILE);
	}

	public File getNetworkConfigFile() {
		return new File(NETWORK_PROPERTIES_FILE);
	}

	@Override
	public void start() throws Exception {

		JLG.debug("starting agent " + name);

		if (isFirstAgent()) {
			JLG.debug("This is the first agent on the network");
			if (network == null) {
				network = new Properties();
				network.load(new FileInputStream(NETWORK_PROPERTIES_FILE));
			}
		} else {
			// even if network is not null...
			network = client.getNetworkProperties();
		}

		String sId = p.getProperty("id");
		if (sId == null) {
			id = generateId();
			p.setProperty("id", id.toString());
			JLG.storeConfig(p, getConfigFile().getAbsolutePath());
		} else {
			id = new Id(sId);
		}
		JLG.debug("agent id = " + id);

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

		// all agent must have a PKI
		keyPair = generateKeyPair();
		signatureAlgorithm = network
				.getProperty("SignatureAlgo", "SHA1withDSA");

		// all users use the same algo for symmetric encryption
		userSecretKeyFactory = SecretKeyFactory.getInstance(network
				.getProperty("user.cipher.algo", "PBEWithMD5AndDES"));
		userCipher = Cipher.getInstance(network.getProperty("user.cipher.algo",
				"PBEWithMD5AndDES"));

		if (p.getProperty("server", "yes").equals("yes")) {
			server = new Server(this);
			server.start();
			attach();
			Contact myself = toContactForMyself();
			addContact(myself);

		}

	}
	
	@Override
	public void stop() {
		if (server != null) {
			server.stop();
		}
	}


	public Contact toContact() {
		// convert the agent public information into a contact
		Contact c = new Contact(this.id);
		c.setName(this.name);
		c.publicKey = this.keyPair.getPublic().getEncoded();
		// add the listener url and node id information
		if (server != null) {
			Iterator<Listener> it = server.listenerList.iterator();
			while (it.hasNext()) {
				Listener l = it.next();
				c.addURL(l.getUrl());
			}
			Iterator<Id> itn = storage.nodeSet.iterator();
			while (itn.hasNext()) {
				Id nodeId = (Id) itn.next();
				c.nodeIdSet.add(nodeId);
			}
		}
		return c;
	}

	public Contact toContactForMyself() {
		// convert the agent public information into a contact
		Contact c = new Contact(this.id);
		c.setName(this.name);
		c.publicKey = this.keyPair.getPublic().getEncoded();
		// add the listener url and node id information
		if (server != null) {
			// for myself use the myself protocol (do not serialize...)
			URL url = new URL("myself", "localhost", 0);
			c.addURL(url);
			Iterator<Id> itn = storage.nodeSet.iterator();
			while (itn.hasNext()) {
				Id nodeId = (Id) itn.next();
				c.nodeIdSet.add(nodeId);
			}
		}
		return c;
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

	public boolean isResponsible(Address address) throws Exception {
		Id nodeId = getNodeId(address);
		Contact contact = getContactFromNodeId(nodeId);
		return contact.id.equals(id);
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
			client.store(contactQueue, address, content);
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
				result = client.getFromAddress(address);
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
		return set(user, JLG.serialize(serializable).getBytes());
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
		Data data = new Data(this, user, user.crypt(JLG.serialize(keys)
				.getBytes()));
		Pointer pointer = new Pointer(set(data).getBytes());
		return pointer;
	}

	public Serializable get(OCPUser user, Pointer pointer) throws Exception {
		return JLG.deserialize(new String(getBytes(user, pointer)));
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
		Key[] keys = (Key[]) JLG.deserialize(new String(cleartext));
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
		Key key = UserPublicInfo.getKey(this, new String(username));
		ObjectData data = (ObjectData) get(key);
		if (data == null) {
			throw new Exception("Cannot get the user public info for "
					+ new String(username));
		}
		UserPublicInfo upi = (UserPublicInfo) data.getObject();
		return upi;
	}

	public void createUser(String login, String password, int backupNbr,
			Captcha captcha, String answer) throws Exception {

		OCPUser user = new OCPUser(this, login, backupNbr);
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

	@Override
	public User login(String login, String password) throws Exception {
		try {
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
		} catch (Exception e) {
			JLG.error(e);
			throw e;
		}
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
	protected void readConfig() throws Exception {

		// debugging aspect
		if (p.getProperty("debug", "true").equalsIgnoreCase("true")) {
			JLG.debug_on();
			JLG.debug("working directory = " + System.getProperty("user.dir"));
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

	}

	public Queue<Contact> makeContactQueue(Id key) throws Exception {
		Queue<Contact> contactQueue = new LinkedList<Contact>();
		NavigableMap<Id, Contact> nodeMap = new TreeMap<Id, Contact>(
				this.nodeMap);
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
		LinkedList<Contact> linkedList = new LinkedList<Contact>(
				contactMap.values());
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

	@Override
	public boolean allowsUserCreation() {
		return true;
	}

	@Override
	public void checkout(User user, String dir) throws Exception {
		FileSystem fs = new FileSystem((OCPUser) user, this, dir);
		fs.checkout();
	}

	@Override
	public void commit(User user, String dir) throws Exception {
		try {
			FileSystem fs = new FileSystem((OCPUser) user, this, dir);
			fs.commit();
		} catch (Exception e) {
			JLG.error(e);
		}

		
	}

	@Override
	public void mkdir(User user, String existingParentDir, String newDir)
			throws Exception {
		FileSystem fs = new FileSystem((OCPUser) user, this); 
		fs.createNewDir(existingParentDir, newDir);
	}

	@Override
	public void rm(User user, String existingParentDir, String name) throws Exception {
		FileSystem fs = new FileSystem((OCPUser) user, this);
		fs.deleteFile(existingParentDir, name);
	}

	@Override
	public void rename(User user, String existingParentDir, String oldName,
			String newName) throws Exception {
		FileSystem fs = new FileSystem((OCPUser) user, this);
		fs.renameFile(existingParentDir, oldName, newName);
	}

	@Override
	public FileInterface getDir(User user, String dir) throws Exception {
		FileSystem fs = new FileSystem((OCPUser) user, this);
		return fs.getTree(dir);
	}

	@Override
	public void checkout(User user, String remoteDir, String remoteFilename,
			File localDir) throws Exception {
		FileSystem fs = new FileSystem((OCPUser) user, this);
		TreeEntry te = fs.getTree(remoteDir).getEntry(remoteFilename);
		fs.checkout(te, localDir);
	}

	@Override
	public void commit(User user, String remoteDir, File file) throws Exception {
		FileSystem fs = new FileSystem((OCPUser) user, this);
		fs.commitFile(remoteDir, file);
	}

}
