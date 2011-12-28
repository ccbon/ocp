package com.guenego.ocp;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.guenego.misc.Id;
import com.guenego.misc.URL;

public class Contact implements Serializable, Comparable<Contact> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<URL> urlList;
	public Id id;
	public byte[] publicKey;
	public SortedSet<Id> nodeIdSet;
	private String name;

	public Contact(Id id) {
		this.id = id;
		urlList = new ArrayList<URL>();
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
			return ((Contact) obj).id.equals(id);
		}
		return false;
	}


	public void addURL(URL url) {
		urlList.add(url);
	}


	public void copy(Contact c) {
		// for all member, replace
		this.urlList = c.urlList;
		this.id = c.id;
		this.publicKey = c.publicKey;
		this.nodeIdSet = c.nodeIdSet;
		this.name = c.name;
	}


	@Override
	public int compareTo(Contact o) {
		return this.id.compareTo(o.id);
	}

	public void updateHost(String host) {
		// foreach url, update the hostname
		Iterator<URL> itp = urlList.iterator();
		while (itp.hasNext()) {
			URL url = itp.next();
			url.setHost(host);
		}
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
		
	}
}
