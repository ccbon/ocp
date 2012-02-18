package org.ocpteam.ocp;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ocpteam.rsp.FileInterface;


public class Tree implements Serializable, FileInterface {

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

	public void removeEntry(String name) {
		entryMap.remove(name);
	}

	public void renameEntry(String oldName, String newName) {
		TreeEntry te = entryMap.remove(oldName);
		te.setName(newName);
		entryMap.put(newName, te);
	}

	@Override
	public Collection<? extends FileInterface> listFiles() {
		return getEntries();
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public String getName() {
		return null;
	}

}
