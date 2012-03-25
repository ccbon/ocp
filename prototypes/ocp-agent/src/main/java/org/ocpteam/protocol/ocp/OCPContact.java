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
		setId(id);
		nodeIdSet = Collections.synchronizedSortedSet(new TreeSet<Id>());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {
			return ((OCPContact) obj).getId().equals(getId());
		}
		return false;
	}


	public void copy(OCPContact c) {
		// for all member, replace
		this.setUrlList(c.getUrlList());
		setId(c.getId());
		this.publicKey = c.publicKey;
		this.nodeIdSet = c.nodeIdSet;
		setName(c.getName());
	}








}
