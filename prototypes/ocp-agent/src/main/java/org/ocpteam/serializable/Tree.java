package org.ocpteam.serializable;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ocpteam.interfaces.IFile;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

/**
 * A Tree is a map<filename, TreeEntry> structure. It is used to form a file
 * system from a map. And even better it is used to form a versioned file
 * system.
 * 
 */
public class Tree implements Serializable, IFile, IStructurable {

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
	public Collection<? extends IFile> listFiles() {
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

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setMapField("entryMap", entryMap);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		entryMap = (Map<String, TreeEntry>) s.getMapField("entryMap", TreeEntry.class);
	}
	
	@Override
	public long getSize() {
		return -1;
	}

}
