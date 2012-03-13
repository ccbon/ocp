package org.ocpteam.functionality;

import org.ocpteam.core.Container;
import org.ocpteam.misc.PersistentFileMap;


public class NaivePersistentMap extends PersistentFileMap implements PersistentMap {

	public Container parent;

	@Override
	public void setParent(Container parent) {
		this.parent = parent;
	}

}
