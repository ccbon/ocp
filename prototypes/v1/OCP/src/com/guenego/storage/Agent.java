package com.guenego.storage;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import com.guenego.misc.Id;
import com.guenego.misc.JLG;

public abstract class Agent {

	public Properties p;
	
	protected Map<Id, Contact> contactMap; // contactid->contact

	public FileSystem fs;

	public Agent() {
		contactMap = new HashMap<Id, Contact>();
	}
	
	public abstract boolean isConfigFilePresent();

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
	
	public abstract void stop();

	
	
	
	
	
	public boolean isFirstAgent() {
		if (p == null) {
			JLG.debug("p is null");
		}
		String s = p.getProperty("server", "no");
		return s.equalsIgnoreCase("yes")
				&& p.getProperty("server.isFirstAgent", "no").equalsIgnoreCase(
						"yes");
	}

	

	public abstract boolean allowsUserCreation();

	public abstract User login(String login, String password) throws Exception;

	
	
	
	
	public abstract void refreshContactList() throws Exception;

	public List<Contact> getContactSnapshotList() {
		// we return a snapshot and not the modifiable contact list
		return new LinkedList<Contact>(
				contactMap.values());
		
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

	
	public abstract FileSystem getFileSystem(User user);

}
