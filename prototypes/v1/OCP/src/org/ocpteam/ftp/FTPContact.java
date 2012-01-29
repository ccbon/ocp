package org.ocpteam.ftp;

import org.ocpteam.misc.Id;
import org.ocpteam.storage.Contact;


public class FTPContact extends Contact {

	public FTPContact(String string) {
		super();
		this.id = new Id(string.getBytes());
		this.name = string;
	}

	@Override
	public String getId() {
		return "FTP";
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
