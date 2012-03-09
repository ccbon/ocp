package org.ocpteam.functionality;

import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.PersistentFileMap;


public class NaivePersistentMap extends PersistentFileMap implements PersistentMap {

	public DataSource parent;

	@Override
	public void setParent(DataSource parent) {
		this.parent = parent;
	}

}
