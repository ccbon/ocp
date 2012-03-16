package org.ocpteam.component;

import java.util.Map;

import org.ocpteam.core.IContainer;

public class MapDataModel implements DataModel {

	protected IContainer parent;
	protected Map<String, byte[]> map;
	
	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}
	
	public void setMap(Map<String, byte[]> map) {
		this.map = map;
	}

	public Map<String, byte[]> getMap() {
		return this.map;
	}

}
