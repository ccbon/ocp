package org.ocpteam.fs;

import java.util.Collection;

import org.ocpteam.interfaces.IFile;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;


public class TreeEntry implements IStructurable, IFile {

	private Pointer p;
	private int type;
	private String name;
	
	public TreeEntry() {
		
	}
	
	public TreeEntry(String name, Pointer p, int type) {
		this.setName(name);
		this.p = p;
		this.type = type;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int FILE = 1;
	public static final int TREE = 2;
	public boolean isTree() {
		return type == TREE;
	}
	public Pointer getPointer() {
		return p;
	}
	
	
	@Override
	public boolean isFile() {
		return type == FILE;
	}
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Collection<IFile> listFiles() {
		return null;
	}
	@Override
	public boolean isDirectory() {
		return isTree();
	}
	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure("FSTreeEntry");
		result.setStringField("name", name);
		result.setStringField("type", (type == TREE) ? "DIR" : "FILE");
		result.setStructureToSubstructField("p", p.toStructure());
		return result;
	}
	@Override
	public void fromStructure(Structure s) throws Exception {
		setName(s.getStringField("name"));
		String stype = s.getStringField("type");
		
		type = stype.equals("DIR") ? TREE : FILE; 
		p = (Pointer) s.getStructureFromSubstructField("p").toStructurable();
	}
	
	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		return -1;
	}

}
