package org.ocpteam.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ocpteam.misc.URL;


/**
 * A Contact reflects the public information that an agent can gives to the
 * others members of the distributed network.
 *
 */
public class Contact implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private List<URL> urlList;

	private boolean bIsMyself = false;
	
	public Contact() {
		this.urlList = new ArrayList<URL>();
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
		String result = getName();
		if (isMyself()) {
			result += "<myself>";
		}
//		Iterator<URL> itp = getUrlList().iterator();
//		while (itp.hasNext()) {
//			URL url = itp.next();
//			result += "[" + url.toString() + "]";
//		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {
			return ((Contact) obj).getName().equals(getName());
		}
		return false;
	}


}
