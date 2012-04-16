package org.ocpteam.protocol.ocp;

import java.io.Serializable;
import java.util.Collection;

import org.ocpteam.interfaces.IFile;


public class TreeEntry implements Serializable, IFile {

	private Pointer p;
	private int type;
	private String name;
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

}
