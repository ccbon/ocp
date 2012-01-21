package com.guenego.storage;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import com.guenego.misc.Id;
import com.guenego.misc.JLG;

public abstract class Agent {

	public Properties p;
	
	protected Map<Id, Contact> contactMap; // contactid->contact

	public Agent() {
		contactMap = new HashMap<Id, Contact>();
	}

	public void loadConfig() throws Exception {
		if (!isConfigFilePresent()) {
			throw new Exception("Config file is not found. Expected Path: "
					+ getConfigFile().getAbsolutePath());
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

	public boolean isFirstAgent() {
		if (p == null) {
			JLG.debug("p is null");
		}
		String s = p.getProperty("server", "no");
		return s.equalsIgnoreCase("yes")
				&& p.getProperty("server.isFirstAgent", "no").equalsIgnoreCase(
						"yes");
	}

	public abstract void stop();





	public abstract boolean isConfigFilePresent();

	public abstract boolean allowsUserCreation();

	public abstract User login(String login, String password) throws Exception;

	public abstract void checkout(User user, String localDir) throws Exception;

	public abstract void commit(User user, String localDir) throws Exception;

	public abstract void mkdir(User user, String existingParentDir,
			String newDir) throws Exception;

	public abstract void rm(User user, String existingParentDir, String name)
			throws Exception;

	public abstract void rename(User user, String existingParentDir,
			String oldName, String newName) throws Exception;

	public abstract FileInterface getDir(User user, String dir)
			throws Exception;

	public abstract void checkout(User user, String remoteDir,
			String remoteFilename, File localDir) throws Exception;

	public abstract void commit(User user, String remoteDir, File file) throws Exception;

	public abstract void refreshContactList() throws Exception;

	public Iterator<Contact> getContactIterator() {
		// we return a snapshot and not the modifiable contact list
		LinkedList<Contact> linkedList = new LinkedList<Contact>(
				contactMap.values());
		return linkedList.iterator();
	}
	
	public void addContact(Contact contact) throws Exception {
		contactMap.put(contact.id, contact);
	}
	
	public Contact removeContact(Contact contact) {
		return contactMap.remove(contact.id);
	}

	
	public Contact getContact(Id contactId) throws Exception {
		Contact contact = contactMap.get(contactId);
		if (contact == null) {
			throw new Exception("contact not found in my contact list.");
		}
		return contact;
	}
	
	public boolean hasNoContact() {
		return contactMap.size() == 0;
	}
	
	public boolean hasContact(Contact contact) {
		return contactMap.containsValue(contact);
	}
	
	public abstract Queue<Contact> makeContactQueue() throws Exception;
	
	public abstract Contact toContact();


	public abstract String getProtocolName();

	public abstract String getName();

	public String getHelpURL() {
		return "http://code.google.com/p/ocp/wiki/Help";
	}

	public abstract boolean hasStorage();

	public abstract void removeStorage();

}
