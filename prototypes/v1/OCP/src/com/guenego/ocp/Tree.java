package com.guenego.ocp;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Tree implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, TreeEntry> entryMap;
	
	
	public Tree() {
		entryMap = new HashMap<String, TreeEntry>();
	}

	public void addFile(String name, Pointer p) {
		entryMap.put(name, new TreeEntry(name, p, TreeEntry.FILE));
		
	}

	public void addTree(String name, Pointer p) {
		entryMap.put(name, new TreeEntry(name, p, TreeEntry.TREE));
	}

	public Collection<TreeEntry> getEntries() {
		return entryMap.values();
	}

	public TreeEntry getEntry(String name) {
		return entryMap.get(name);
	}

}
