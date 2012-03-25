package org.ocpteam.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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

	private boolean bIsMyself = false;
	
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

	public boolean isMyself() {
		return bIsMyself;
	}
	
	public void setMyself(boolean b) {
		this.bIsMyself = b;
	}
	
	public void updateHost(String host) {
		// foreach url, update the hostname
		Iterator<URL> itp = getUrlList().iterator();
		while (itp.hasNext()) {
			URL url = itp.next();
			url.setHost(host);
		}
	}
	
	@Override
	public String toString() {
		String result = getId() + " - " + getName();
		Iterator<URL> itp = getUrlList().iterator();
		while (itp.hasNext()) {
			URL url = itp.next();
			result += "[" + url.toString() + "]";
		}
		return result;
	}

}
