package org.ocpteam.ocp;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

public class UserIndex implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private NavigableSet<Pointer> set;
	
	public UserIndex() {
		set = new TreeSet<Pointer>();
	}
	
	
	
	
	

	public void add(Pointer pointer) {
		set.add(pointer);
		
	}

	public Iterator<Pointer> iterator() {
		return set.iterator();
	}

	public void remove(Pointer pointer) {
		set.remove(pointer);
	}
	
	
	
}
