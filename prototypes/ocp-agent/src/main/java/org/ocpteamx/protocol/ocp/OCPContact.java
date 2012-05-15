package org.ocpteamx.protocol.ocp;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ocpteam.misc.Id;
import org.ocpteam.serializable.Contact;


public class OCPContact extends Contact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte[] publicKey;
	public SortedSet<Id> nodeIdSet;
	

	public OCPContact() {
		super();
		nodeIdSet = Collections.synchronizedSortedSet(new TreeSet<Id>());
	}

}
