package org.ocpteam.component;

import java.util.Map;

import org.ocpteam.interfaces.IDataModel;

public class MapDataModel extends DataSourceComponent implements IDataModel {

	protected Map<String, byte[]> map;
	
	public void setMap(Map<String, byte[]> map) {
		this.map = map;
	}

	public Map<String, byte[]> getMap() {
		return this.map;
	}

}
