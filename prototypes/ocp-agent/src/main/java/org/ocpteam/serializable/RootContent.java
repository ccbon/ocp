package org.ocpteam.serializable;

import java.util.HashMap;
import java.util.Map;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

public class RootContent implements IStructurable {
	private static final long serialVersionUID = 1L;
	Map<String, Address> map;

	public RootContent() {
		map = new HashMap<String, Address>();
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setMapField("map", map);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		map = s.getMapField("map", Address.class);
	}

	public Map<String, Address> getMap() {
		return map;
	}
}
