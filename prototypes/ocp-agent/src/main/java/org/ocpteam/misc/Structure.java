package org.ocpteam.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ocpteam.component.FListMarshaler;
import org.ocpteam.interfaces.IStructurable;

public class Structure implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TYPE_INT = "int";
	public static final String TYPE_SUBSTRUCT = "substr";
	public static final String TYPE_LIST = "list";
	public static final String TYPE_DECIMAL = "dec";
	public static final String TYPE_STRING = "str";
	public static final String TYPE_MAP = "map";
	public static final String TYPE_BYTES = "bin";
	public static final String NAME_SIMPLE = "simple";
	public static final String NAME_MAP = "map";
	public static final String FIELDNAME_MAPENTRY = "map_entry";
	public static final String NAME_MAPENTRY = "map_entry";
	public static final String FIELDNAME_SIMPLE = "field";
	public static final String FIELDNAME_MAPSIGNATURE = "map_signature";
	public static final String NAME_MAPSIGNATURE = "map_signature";
	public static final String NAME_PROPERTIES = "properties";
	public static final String NAME_PROPENTRY = "prop_entry";
	public static final String FIELDNAME_PROPENTRY = "prop_entry";
	public static final String NAME_LIST = "list";
	public static final String FIELDNAME_LISTENTRY = "list_entry";
	public static final String FIELDNAME_TYPE = "field_type";
	public static final String NAME_INT = "int";
	public static final String NAME_LONG = "long";

	private String name;
	private Map<String, SField> fields = new HashMap<String, SField>();

	public Structure(Class<? extends IStructurable> c) throws Exception {
		String name = StructureMap.getFromClass(c);
		setName(name);
	}

	public Structure(String name) {
		setName(name);
	}

	public Structure(Properties properties) {
		setName("Properties");
		if (properties == null) {
			return;
		}
		for (Object o : properties.keySet()) {
			String key = (String) o;
			setField(name, TYPE_STRING, properties.getProperty(key));
		}
	}

	public Structure(String name, Serializable keyPair) {
		setName(name);
	}

	public Structure() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private void setField(String name, String type, Serializable value) {
		fields.put(name, new SField(type, value));
	}

	public Serializable getFieldValue(String name) {
		SField f = getField(name);
		if (f == null) {
			return null;
		}
		return f.getValue();
	}

	public SField getField(String name) {
		return fields.get(name);
	}

	public IStructurable toObject() throws Exception {
		try {
			IStructurable result = null;
			result = getClassFromName().newInstance();
			result.fromStructure(this);
			JLG.debug("result=" + result);
			return result;
		} catch (Exception e) {
			JLG.debug("ERROR=" + e);
			e.printStackTrace();
			throw e;
		}
	}

	private Class<? extends IStructurable> getClassFromName() throws Exception {
		JLG.debug("class=" + StructureMap.get(name));
		return StructureMap.get(name);
	}

	public Map<String, SField> getFields() {
		return fields;
	}

	public void rename(Class<? extends IStructurable> c) throws Exception {
		setName(StructureMap.getFromClass(c));
	}

	public Structure getSubstruct(String name) {
		SField f = getField(name);
		if (f == null) {
			return null;
		}
		return (Structure) f.getValue();
	}

	@SuppressWarnings("unchecked")
	public List<Structure> getStructureList(String name) {
		SField f = getField(name);
		if (f == null) {
			return null;
		}
		return (List<Structure>) f.getValue();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Structure> getStructureMap(String name) {
		SField f = getField(name);
		if (f == null) {
			return null;
		}
		return (Map<String, Structure>) f.getValue();
	}

	public String getString(String name) {
		if (getField(name) == null) {
			return null;
		}
		return (String) getField(name).getValue();
	}

	public int getInt(String name) {
		if (getField(name) == null) {
			return 0;
		}
		return (Integer) getField(name).getValue();
	}

	public byte[] getBin(String name) {
		if (getField(name) == null) {
			return null;
		}
		return (byte[]) getField(name).getValue();
	}

	public Properties getProperties(String name) {
		Structure s = getSubstruct(name);
		if (s == null) {
			return null;
		}
		Properties p = new Properties();
		for (String fname : s.getFields().keySet()) {
			p.setProperty(fname, s.getString(fname));
		}
		return p;
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
				Structure s = new Structure("simple");
				if (o instanceof Integer) {
					s.setIntField("field", (Integer) o);
				} else if (o instanceof String) {
					s.setStringField("field", (String) o);
				} else if (o instanceof byte[]) {
					s.setBinField("field", (byte[]) o);
				} else if (o instanceof Double) {
					s.setDecimalField("field", (Double) o);
				}
				m.put(key, s);
			}
		}
		fields.put(name, new SField(TYPE_MAP, (Serializable) m));

	}

	public void setStructureMapField(String name, Map<String, Structure> map) {
		setField(name, TYPE_MAP, (Serializable) map);
	}

	@SuppressWarnings("unchecked")
	public <T extends IStructurable> Map<String, T> getMap(String name,
			Class<T> c) throws Exception {
		Map<String, Structure> m = (Map<String, Structure>) getField(name)
				.getValue();
		Map<String, T> result = new HashMap<String, T>();
		for (String key : m.keySet()) {
			result.put(key, (T) m.get(key).toObject());
		}
		return result;
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
					s.setStructureListField(FIELDNAME_SIMPLE,
							(List<Structure>) o);
				} else if (o instanceof Map<?, ?>) {
					s.setStructureMapField(FIELDNAME_SIMPLE,
							(Map<String, Structure>) o);
				}
				list.add(s);
			}
		}
		setField(name, TYPE_LIST, (Serializable) list);
	}

	public Serializable[] getArray(String name) throws Exception {
		JLG.debug("this=" + this);
		@SuppressWarnings("unchecked")
		List<Structure> value = (List<Structure>) getField(name).getValue();
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
				Object o = value.get(i).toObject();
				result[i] = (IStructurable) o;
			}

		}
		return result;
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

	public void setBinField(String name, byte[] bytearray) {
		setField(name, TYPE_BYTES, bytearray);
	}

	public void setIntField(String name, int value) {
		setField(name, TYPE_INT, value);
	}

	public void setStructureSubstructField(String name, Structure value) {
		setField(name, TYPE_SUBSTRUCT, value);
	}

	public void setSubstructField(String name, Serializable o) throws Exception {
		if (o == null) {
			setField(name, TYPE_SUBSTRUCT, null);
		} else if (o instanceof IStructurable) {
			IStructurable value = (IStructurable) o;
			setField(name, TYPE_SUBSTRUCT, value.toStructure());
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
			setField(name, TYPE_SUBSTRUCT, s);
		}
	}

	public void setStructureListField(String name, List<Structure> l) {
		setField(name, TYPE_LIST, (Serializable) l);
	}

	public void setDecimalField(String name, double value) {
		setField(name, TYPE_DECIMAL, value);
	}

	public void setStringField(String name, String value) {
		setField(name, TYPE_STRING, value);

	}

	public void setProprietiesField(String name, Properties properties) {
		if (properties == null) {
			setStructureSubstructField(name, null);
			return;
		}
		Structure s = new Structure("properties");
		for (Object o : properties.keySet()) {
			String key = (String) o;
			s.setStringField(key, properties.getProperty(key));
		}
		setStructureSubstructField(name, s);
	}

	public void setNullField(String name, String type) {
		setField(name, type, null);
	}

	public void addStructureListField(String name, Structure substr, int eltid) {
		List<Structure> list = getStructureList(name);
		if (list == null) {
			JLG.debug("List is null");
			list = new ArrayList<Structure>();
			setStructureListField(name, list);
		}

		list.add(eltid, substr);
	}

	public void addStructureMapField(String name, Structure substr, String key) {
		Map<String, Structure> map = getStructureMap(name);
		if (map == null) {
			JLG.debug("map is null");
			map = new HashMap<String, Structure>();
			setStructureMapField(name, map);
		}

		map.put(key, substr);
	}

	public long getDecimal(String name) {
		if (getField(name) == null) {
			return 0;
		}
		return (Long) getField(name).getValue();
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


}
