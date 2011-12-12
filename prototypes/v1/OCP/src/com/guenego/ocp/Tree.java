package com.guenego.ocp;

import java.io.Serializable;
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

	public void checkout(FileSystem fileSystem) {
		// TODO Auto-generated method stub
		
	}

	public void addFile(String name, Pointer p) {
		entryMap.put(name, new TreeEntry(p, TreeEntry.FILE));
		
	}

	public void addTree(String name, Pointer p) {
		entryMap.put(name, new TreeEntry(p, TreeEntry.TREE));
	}

	public Map<String, TreeEntry> getEntries() {
		return entryMap;
	}


}
