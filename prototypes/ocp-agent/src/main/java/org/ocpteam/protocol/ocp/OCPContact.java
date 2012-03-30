package org.ocpteam.protocol.ocp;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ocpteam.entity.Contact;
import org.ocpteam.misc.Id;


public class OCPContact extends Contact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte[] publicKey;
	public SortedSet<Id> nodeIdSet;
	

	public OCPContact(Id id) {
		super();
		setName(id.toString());
		nodeIdSet = Collections.synchronizedSortedSet(new TreeSet<Id>());
	}

	public void copy(OCPContact c) {
		// for all member, replace
		this.setUrlList(c.getUrlList());
		setName(c.getName());
		this.publicKey = c.publicKey;
		this.nodeIdSet = c.nodeIdSet;
	}








}
