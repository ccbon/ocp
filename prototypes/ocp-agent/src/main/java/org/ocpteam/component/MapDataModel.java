package org.ocpteam.component;

import java.util.Map;

import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IDataModel;

public class MapDataModel implements IDataModel {

	protected IContainer parent;
	protected Map<String, byte[]> map;
	
	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}
	
	@Override
	public IContainer getParent() {
		return parent;
	}
	
	public void setMap(Map<String, byte[]> map) {
		this.map = map;
	}

	public Map<String, byte[]> getMap() {
		return this.map;
	}

}
