package org.guenego.ftp;

import org.guenego.misc.Id;
import org.guenego.storage.Contact;


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
