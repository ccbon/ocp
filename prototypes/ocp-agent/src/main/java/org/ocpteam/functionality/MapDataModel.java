package org.ocpteam.functionality;

import java.util.Map;

import org.ocpteam.core.Container;

public class MapDataModel implements DataModel {

	protected Container parent;
	protected Map<String, byte[]> map;
	
	@Override
	public void setParent(Container parent) {
		this.parent = parent;
	}
	
	public void setMap(Map<String, byte[]> map) {
		this.map = map;
	}

	public Map<String, byte[]> getMap() {
		return this.map;
	}

}
