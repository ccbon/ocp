package org.ocpteam.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.component.FListMarshaler;
import org.ocpteam.interfaces.IStructurable;

public class Structure implements Serializable {

	public static class StructureMap {
		private static Map<String, Class<? extends IStructurable>> mapAB = null;
		private static Map<Class<? extends IStructurable>, String> mapBA = null;

		private static void loadMap() throws Exception {
			ResourceBundle p = ResourceBundle.getBundle("structures");

			mapAB = new HashMap<String, Class<? extends IStructurable>>();
			mapBA = new HashMap<Class<? extends IStructurable>, String>();
			for (String key : p.keySet()) {
				@SuppressWarnings("unchecked")
				Class<? extends IStructurable> value = (Class<? extends IStructurable>) Class
						.forName(p.getString(key));
				mapAB.put(key, value);
				mapBA.put(value, key);
			}
		}

		public static Class<? extends IStructurable> getClassFromName(
				String name) throws Exception {
			if (mapAB == null) {
				loadMap();
			}
			Class<? extends IStructurable> result = mapAB.get(name);
			if (result == null) {
				throw new Exception(
						"Class not found in structures.properties for name="
								+ name);
			}
			return result;
		}

		public static String getNameFromClass(Class<? extends IStructurable> c)
				throws Exception {
			if (mapBA == null) {
				loadMap();
			}

			String result = mapBA.get(c);
			if (result == null) {
				throw new Exception(
						"Class not found in structures.properties for class="
								+ c);
			}
			return result;
		}
	}

	private static final long serialVersionUID = 1L;
	public static final String TYPE_INT = "int";
	public static final String TYPE_DECIMAL = "dec";
	public static final String TYPE_STRING = "str";
	public static final String TYPE_BYTES = "bin";

	public static final String TYPE_SUBSTRUCT = "substr";
	public static final String TYPE_LIST = "list";
	public static final String TYPE_MAP = "map";

	public static final String NAME_PROPERTIES = "properties";
	public static final String NAME_PROPENTRY = "prop_entry";
	public static final String FIELDNAME_PROPENTRY = "prop_entry";

	public static final String NAME_SIMPLE = "simple";
	public static final String FIELDNAME_SIMPLE = "field";
	public static final String NAME_MAPENTRY = "map_entry";

	public static final String FIELDNAME_KEY = "key";
	public static final String FIELDNAME_VALUE = "value";

	private String name;
	private Map<String, SField> fields = new HashMap<String, SField>();

	public Structure() {
	}

	public Structure(String name) {
		setName(name);
	}

	public Structure(Class<? extends IStructurable> c) throws Exception {
		String name = StructureMap.getNameFromClass(c);
		setName(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(Class<? extends IStructurable> c) throws Exception {
		setName(StructureMap.getNameFromClass(c));
	}

	public String getName() {
		return name;
	}

	private void setField(String name, String type, Serializable value) {
		fields.put(name, new SField(type, value));
	}

	public void setIntField(String name, int value) {
		setField(name, TYPE_INT, value);
	}

	public void setDecimalField(String name, double value) {
		setField(name, TYPE_DECIMAL, value);
	}

	public void setStringField(String name, String value) {
		setField(name, TYPE_STRING, value);
	}

	public void setBinField(String name, byte[] bytearray) {
		setField(name, TYPE_BYTES, bytearray);
	}

	public void setSubstructField(String name, Serializable o) throws Exception {
		if (o == null) {
			setStructureToSubstructField(name, null);
		} else if (o instanceof IStructurable) {
			IStructurable value = (IStructurable) o;
			setStructureToSubstructField(name, value.toStructure());
		} else {
			Structure s = new Structure(NAME_SIMPLE);
			if (o instanceof Integer) {
				s.setIntField(FIELDNAME_SIMPLE, (Integer) o);
			} else if (o instanceof Double) {
				s.setDecimalField(FIELDNAME_SIMPLE, (Double) o);
			} else if (o instanceof String) {
				s.setStringField(FIELDNAME_SIMPLE, (String) o);
			} else if (o instanceof byte[]) {
				s.setBinField(FIELDNAME_SIMPLE, (byte[]) o);
			}
			setStructureToSubstructField(name, s);
		}
	}

	@SuppressWarnings("unchecked")
	public void setListField(String name, Serializable[] objects)
			throws Exception {
		if (objects == null || objects.length == 0) {
			setField(name, TYPE_LIST, null);
			return;
		}
		List<Structure> list = new ArrayList<Structure>();
		for (Serializable o : objects) {
			if (o == null) {
				list.add(null);
			} else if (o instanceof IStructurable) {
				IStructurable s = (IStructurable) o;
				list.add(s.toStructure());
			} else {
				Structure s = new Structure(NAME_SIMPLE);
				if (o instanceof Integer) {
					s.setIntField(FIELDNAME_SIMPLE, (Integer) o);
				} else if (o instanceof String) {
					s.setStringField(FIELDNAME_SIMPLE, (String) o);
				} else if (o instanceof byte[]) {
					s.setBinField(FIELDNAME_SIMPLE, (byte[]) o);
				} else if (o instanceof Double) {
					s.setDecimalField(FIELDNAME_SIMPLE, (Double) o);
				} else if (o instanceof List<?>) {
					s.setStructureToListField(FIELDNAME_SIMPLE,
							(List<Structure>) o);
				} else if (o instanceof Map<?, ?>) {
					s.setStructureToMapField(FIELDNAME_SIMPLE,
							(Map<String, Structure>) o);
				}
				list.add(s);
			}
		}
		setField(name, TYPE_LIST, (Serializable) list);
	}

	public void setMapField(String name, Map<String, ? extends Serializable> map)
			throws Exception {
		Map<String, Structure> m = new HashMap<String, Structure>();
		for (String key : map.keySet()) {
			Serializable o = map.get(key);
			if (o instanceof IStructurable) {
				IStructurable s = (IStructurable) o;
				m.put(key, s.toStructure());
			} else {
				Structure s = new Structure(NAME_SIMPLE);
				if (o instanceof Integer) {
					s.setIntField(FIELDNAME_SIMPLE, (Integer) o);
				} else if (o instanceof String) {
					s.setStringField(FIELDNAME_SIMPLE, (String) o);
				} else if (o instanceof byte[]) {
					s.setBinField(FIELDNAME_SIMPLE, (byte[]) o);
				} else if (o instanceof Double) {
					s.setDecimalField(FIELDNAME_SIMPLE, (Double) o);
				}
				m.put(key, s);
			}
		}
		fields.put(name, new SField(TYPE_MAP, (Serializable) m));

	}

	public void setProprietiesField(String name, Properties properties) {
		if (properties == null) {
			setStructureToSubstructField(name, null);
			return;
		}
		Structure s = new Structure(NAME_PROPERTIES);
		for (Object o : properties.keySet()) {
			String key = (String) o;
			s.setStringField(key, properties.getProperty(key));
		}
		setStructureToSubstructField(name, s);
	}

	public void setStructureToSubstructField(String name, Structure value) {
		setField(name, TYPE_SUBSTRUCT, value);
	}

	public void setStructureToListField(String name, List<Structure> l) {
		setField(name, TYPE_LIST, (Serializable) l);
	}

	public void setStructureToMapField(String name, Map<String, Structure> map) {
		setField(name, TYPE_MAP, (Serializable) map);
	}

	public void addStructureToListField(String name, Structure substr, int eltid) {
		List<Structure> list = getStructureFromListField(name);
		if (list == null) {
			JLG.debug("List is null");
			list = new ArrayList<Structure>();
			setStructureToListField(name, list);
		}

		list.add(eltid, substr);
	}

	public void addStructureToMapField(String name, Structure substr, String key) {
		Map<String, Structure> map = getStructureFromMapField(name);
		if (map == null) {
			JLG.debug("map is null");
			map = new HashMap<String, Structure>();
			setStructureToMapField(name, map);
		}

		map.put(key, substr);
	}

	// ////////////////////////////////////////////////

	public SField getField(String name) {
		return fields.get(name);
	}

	public Serializable getFieldValue(String name) {
		SField f = getField(name);
		if (f == null) {
			return null;
		}
		return f.getValue();
	}

	public Map<String, SField> getFields() {
		return fields;
	}

	public int getIntField(String name) {
		if (getField(name) == null) {
			return 0;
		}
		return (Integer) getField(name).getValue();
	}

	public long getDecimalField(String name) {
		if (getField(name) == null) {
			return 0;
		}
		return (Long) getField(name).getValue();
	}

	public String getStringField(String name) {
		if (getField(name) == null) {
			return null;
		}
		return (String) getField(name).getValue();
	}

	public byte[] getBinField(String name) {
		if (getField(name) == null) {
			return null;
		}
		return (byte[]) getField(name).getValue();
	}

	public Serializable[] getListField(String name) throws Exception {
		JLG.debug("this=" + this);
		List<Structure> value = getStructureFromListField(name);
		if (value == null) {
			return null;
		}
		Serializable[] result = new Serializable[value.size()];
		for (int i = 0; i < value.size(); i++) {
			Structure s = value.get(i);
			if (s == null) {
				result[i] = null;
			} else if (s.getName().equals(NAME_SIMPLE)) {
				SField f = value.get(i).getField(FIELDNAME_SIMPLE);
				JLG.debug("field=" + f);
				String type = f.getType();
				if (type.equals(TYPE_STRING)) {
					result[i] = (Serializable) f.getValue();
				} else {
					result[i] = (Serializable) f.getValue();
				}

			} else {
				Object o = value.get(i).toStructurable();
				result[i] = (IStructurable) o;
			}

		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T extends IStructurable> Map<String, T> getMapField(String name,
			Class<T> c) throws Exception {
		SField f = getField(name);
		if (f == null) {
			return null;
		}
		Map<String, Structure> m = (Map<String, Structure>) f.getValue();
		Map<String, T> result = new HashMap<String, T>();
		for (String key : m.keySet()) {
			result.put(key, (T) m.get(key).toStructurable());
		}
		return result;
	}

	public Properties getProperties(String name) {
		Structure s = getStructureFromSubstructField(name);
		if (s == null) {
			return null;
		}
		Properties p = new Properties();
		for (String fname : s.getFields().keySet()) {
			p.setProperty(fname, s.getStringField(fname));
		}
		return p;
	}

	public Structure getStructureFromSubstructField(String name) {
		SField f = getField(name);
		if (f == null) {
			return null;
		}
		return (Structure) f.getValue();
	}

	@SuppressWarnings("unchecked")
	public List<Structure> getStructureFromListField(String name) {
		SField f = getField(name);
		if (f == null) {
			return null;
		}
		return (List<Structure>) f.getValue();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Structure> getStructureFromMapField(String name) {
		SField f = getField(name);
		if (f == null) {
			return null;
		}
		return (Map<String, Structure>) f.getValue();
	}

	// ///////////////////////////////////////////////////////////////////

	public IStructurable toStructurable() throws Exception {
		try {
			IStructurable result = null;
			result = StructureMap.getClassFromName(name).newInstance();
			result.fromStructure(this);
			JLG.debug("result=" + result);
			return result;
		} catch (Exception e) {
			JLG.debug("ERROR=" + e);
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public String toString() {
		try {
			return FListMarshaler.NL
					+ new String(new FListMarshaler().marshal(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Structure)) {
			JLG.debug("Not a Structure");
			return false;
		}
		Structure s = (Structure) obj;
		if (!this.getName().equals(s.getName())) {
			JLG.debug("Not same Name");
			return false;
		}
		return this.contains(s) && s.contains(this);
	}

	public boolean contains(Structure s) {
		if (s == null) {
			JLG.debug("Not a Structure");
			return false;
		}
		if (!this.getName().equals(s.getName())) {
			JLG.debug("Not same Name");
			return false;
		}
		for (String fname : s.getFields().keySet()) {
			JLG.debug("Testing the field " + fname);
			SField f1 = this.getField(fname);
			JLG.debug("f1=" + f1);
			SField f2 = s.getField(fname);
			JLG.debug("f2=" + f2);
			if (!f2.equals(f1)) {
				JLG.debug("Fields are not equal");
				return false;
			}
		}
		return true;
	}

	public boolean hasField(String name) {
		return fields.containsKey(name);
	}
}
