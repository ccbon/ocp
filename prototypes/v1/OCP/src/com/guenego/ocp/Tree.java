package com.guenego.ocp;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Tree implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Set<TreeEntry> entrySet;
	
	
	public Tree() {
		entrySet = new HashSet<TreeEntry>();
	}

	public void checkout(FileSystem fileSystem) {
		// TODO Auto-generated method stub
		
	}

	public void addFile(String name, Pointer p) {
		entrySet.add(new TreeEntry(name, p, TreeEntry.FILE));
		
	}

	public void addTree(String name, Pointer p) {
		entrySet.add(new TreeEntry(name, p, TreeEntry.TREE));
	}

	public Set<TreeEntry> getEntries() {
		return entrySet;
	}

}
