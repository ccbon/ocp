package org.ocpteam.component;

import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.PersistentFileMap;


public class NaivePersistentMap extends PersistentFileMap implements IPersistentMap {

	public IContainer parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}
	
	@Override
	public IContainer getParent() {
		return parent;
	}

	@Override
	public IContainer getRoot() {
		return parent.getRoot();
	}

	@Override
	public void init() {
	}

}
