package org.ocpteam.functionality;

import java.util.Map;

import org.ocpteam.core.Container;

public class MapDataModel implements DataModel {

	protected Container parent;
	protected Map<byte[], byte[]> map;
	
	@Override
	public void setParent(Container parent) {
		this.parent = parent;
	}
	
	public void setMap(Map<byte[], byte[]> map) {
		this.map = map;
	}

}
