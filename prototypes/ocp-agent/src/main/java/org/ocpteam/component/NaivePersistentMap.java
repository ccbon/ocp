package org.ocpteam.component;

import org.ocpteam.core.IContainer;
import org.ocpteam.misc.PersistentFileMap;


public class NaivePersistentMap extends PersistentFileMap implements PersistentMap {

	public IContainer parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}

}
