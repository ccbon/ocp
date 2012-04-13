package org.ocpteam.protocol.map;

import java.util.HashMap;
import java.util.Map;

import org.ocpteam.component.DataSource;
import org.ocpteam.component.MapDataModel;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IDataModel;

public class MapDataSource extends DataSource {

	private Map<String, String> map;
	
	public MapDataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new MapDataModel());
		// for example
		map = new HashMap<String, String>();
		map.put("Hello", "World");
		map.put("Foo", "Bar");
		map.put("Foo", "WWW");
	}
	
	@Override
	public String getProtocolName() {
		return "MAP";
	}
	
	@Override
	public void connect() throws Exception {
		MapDataModel dm = (MapDataModel) getComponent(IDataModel.class);
		dm.setMap(map);
		context = new Context(getComponent(IDataModel.class), null);
	}

}
