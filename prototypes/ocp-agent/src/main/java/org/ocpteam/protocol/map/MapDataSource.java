package org.ocpteam.protocol.map;

import java.util.HashMap;
import java.util.Map;

import org.ocpteam.component.DataModel;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.MapDataModel;
import org.ocpteam.layer.rsp.Context;

public class MapDataSource extends DataSource {

	private Map<String, byte[]> map;
	
	public MapDataSource() throws Exception {
		super();
		getDesigner().add(DataModel.class, new MapDataModel());
		// for example
		map = new HashMap<String, byte[]>();
		map.put("Hello", "World".getBytes());
		map.put("Foo", "Bar".getBytes());
		map.put("Foo", "WWW".getBytes());
	}
	
	@Override
	public String getProtocol() {
		return "MAP";
	}
	
	@Override
	public void open() throws Exception {
		MapDataModel dm = (MapDataModel) getDesigner().get(DataModel.class);
		dm.setMap(map);
		context = new Context(null, getDesigner().get(DataModel.class), null);
	}

}
