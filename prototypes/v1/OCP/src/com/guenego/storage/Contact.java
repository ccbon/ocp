package com.guenego.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.guenego.misc.Id;
import com.guenego.misc.URL;

public class Contact implements Serializable, Comparable<Contact> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Id id;
	protected String name;
	public List<URL> urlList;
	
	public Contact() {
		this.urlList = new ArrayList<URL>();
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;	
	}
	@Override
	public int compareTo(Contact o) {
		if (id == null) {
			if (o.id == null) {
				return 0;
			}
			return -1;
		}
		return this.id.compareTo(o.id);
	}
	
	public void addURL(URL url) {
		urlList.add(url);
	}

	public String getId() {
		return id.toString();
	}

}
