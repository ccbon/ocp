package org.ocpteam.protocol.map;

import java.util.HashMap;
import java.util.Map;

import org.ocpteam.layer.rsp.Context;
import org.ocpteam.layer.rsp.DataModel;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.layer.rsp.MapDataModel;

public class MapDataSource extends DataSource {

	private Map<byte[], byte[]> map;
	
	public MapDataSource() throws Exception {
		super();
		getDesigner().add(DataModel.class, new MapDataModel());
	}
	
	@Override
	public String getProtocol() {
		return "MAP";
	}
	
	@Override
	public void open() throws Exception {
		map = new HashMap<byte[], byte[]>();
		MapDataModel dm = (MapDataModel) getDesigner().get(DataModel.class);
		dm.setMap(map);
	}
	
	@Override
	public Context getContext() {
		return new Context(null, getDesigner().get(DataModel.class), null);
	}

}
