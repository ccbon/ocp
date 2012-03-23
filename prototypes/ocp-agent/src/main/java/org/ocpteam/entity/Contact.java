package org.ocpteam.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ocpteam.misc.Id;
import org.ocpteam.misc.URL;


/**
 * A Contact reflects the public information that an agent can gives to the
 * others members of the distributed network.
 *
 */
public class Contact implements Serializable, Comparable<Contact> {

	private static final long serialVersionUID = 1L;
	
	private Id id;
	private String name;
	private List<URL> urlList;
	
	public Contact() {
		this.urlList = new ArrayList<URL>();
	}
	
	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;	
	}
	
	public List<URL> getUrlList() {
		return urlList;
	}
	
	public void setUrlList(List<URL> urlList) {
		this.urlList = urlList;
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

}