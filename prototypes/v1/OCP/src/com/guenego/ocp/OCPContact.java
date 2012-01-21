package com.guenego.ocp;

import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.guenego.misc.Id;
import com.guenego.misc.URL;
import com.guenego.storage.Contact;

public class OCPContact extends Contact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte[] publicKey;
	public SortedSet<Id> nodeIdSet;
	

	public OCPContact(Id id) {
		super();
		this.id = id;
		nodeIdSet = Collections.synchronizedSortedSet(new TreeSet<Id>());
	}


	@Override
	public String toString() {
		String result = id.toString();
		Iterator<URL> itp = urlList.iterator();
		while (itp.hasNext()) {
			URL url = itp.next();
			result += "[" + url.toString() + "]";
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {
			return ((OCPContact) obj).id.equals(id);
		}
		return false;
	}





	public void copy(OCPContact c) {
		// for all member, replace
		this.urlList = c.urlList;
		this.id = c.id;
		this.publicKey = c.publicKey;
		this.nodeIdSet = c.nodeIdSet;
		this.name = c.name;
	}



	public void updateHost(String host) {
		// foreach url, update the hostname
		Iterator<URL> itp = urlList.iterator();
		while (itp.hasNext()) {
			URL url = itp.next();
			url.setHost(host);
		}
	}




}
