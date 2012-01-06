package com.guenego.ocp;

import java.io.Serializable;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Tree implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NavigableMap<String, TreeEntry> entryMap;
	
	
	public Tree() {
		entryMap = new TreeMap<String, TreeEntry>();
	}

	public void checkout(FileSystem fileSystem) {
		// TODO Auto-generated method stub
		
	}

	public void addFile(String name, Pointer p) {
		entryMap.put(name, new TreeEntry(p, TreeEntry.FILE));
		
	}

	public void addTree(String name, Pointer p) {
		entryMap.put(name, new TreeEntry(p, TreeEntry.TREE));
	}

	public NavigableMap<String, TreeEntry> getEntries() {
		return entryMap;
	}


}
