package com.guenego.ocp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.guenego.misc.Id;

public class Contact implements Serializable, Comparable<Contact> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<String> sUrlList;
	public Id id;
	public byte[] publicKey;
	public SortedSet<Id> nodeIdSet;

	public Contact(Id id) {
		this.id = id;
		sUrlList = new ArrayList<String>();
		nodeIdSet = Collections.synchronizedSortedSet(new TreeSet<Id>());
	}


	@Override
	public String toString() {
		String result = id.toString();
		Iterator<String> itp = sUrlList.iterator();
		while (itp.hasNext()) {
			String sUrl = itp.next();
			result += "[" + sUrl.toString() + "]";
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {
			return ((Contact) obj).id.equals(id);
		}
		return false;
	}


	public void addURL(String sUrl) {
		sUrlList.add(sUrl);
	}


	public void copy(Contact c) {
		// for all member, replace
		this.sUrlList = c.sUrlList;
		this.id = c.id;
		this.publicKey = c.publicKey;
		this.nodeIdSet = c.nodeIdSet;
		
	}


	@Override
	public int compareTo(Contact o) {
		return this.id.compareTo(o.id);
	}


}
