package com.guenego.ocp;

import java.io.Serializable;

public class TreeEntry implements Serializable {

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
	
	
	public boolean isFile() {
		return type == FILE;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
