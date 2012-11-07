package org.ocpteam.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.ocpteam.interfaces.IStructurable;

public class StructureMap {

	private static Map<String, Class<? extends IStructurable>> mapAB = null;
	private static Map<Class<? extends IStructurable>, String> mapBA = null;

	public static Class<? extends IStructurable> get(String name) throws Exception {
		if (mapAB == null) {
			loadMap();
		}
		Class<? extends IStructurable> result = mapAB.get(name);
		if (result == null) {
			throw new Exception("Class not found in structures.properties for name=" + name);
		}
		return result;
	}

	private static void loadMap() throws Exception {
		ResourceBundle p = ResourceBundle.getBundle("structures");

		mapAB = new HashMap<String, Class<? extends IStructurable>>();
		mapBA = new HashMap<Class<? extends IStructurable>, String>();
		for (String key : p.keySet()) {
			@SuppressWarnings("unchecked")
			Class<? extends IStructurable> value = (Class<? extends IStructurable>) Class.forName(p
					.getString(key));
			mapAB.put(key, value);
			mapBA.put(value, key);
		}
	}

	public static String getFromClass(Class<? extends IStructurable> c) throws Exception {
		if (mapBA == null) {
			loadMap();
		}
		
		String result = mapBA.get(c);
		if (result == null) {
			throw new Exception("Class not found in structures.properties for class=" + c);
		}
		return result;
	}

}
