package org.ocpteam.protocol.map;

import java.util.HashMap;
import java.util.Map;

import org.ocpteam.functionality.DataModel;
import org.ocpteam.functionality.DataSource;
import org.ocpteam.functionality.MapDataModel;
import org.ocpteam.layer.rsp.Context;

public class MapDataSource extends DataSource {

	private Map<byte[], byte[]> map;
	
	public MapDataSource() throws Exception {
		super();
		getDesigner().add(DataModel.class, new MapDataModel());
		// for example
		map = new HashMap<byte[], byte[]>();
		map.put("Hello".getBytes(), "World".getBytes());
		map.put("Foo".getBytes(), "Bar".getBytes());
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
