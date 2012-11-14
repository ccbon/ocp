package org.ocpteam.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ocpteam.component.FListMarshaler;
import org.ocpteam.interfaces.IStructurable;

public class Structure {
	public static final String TYPE_INT = "int";
	public static final String TYPE_SUBSTRUCT = "substr";
	public static final String TYPE_LIST = "list";
	public static final String TYPE_DECIMAL = "dec";
	public static final String TYPE_STRING = "str";
	public static final String TYPE_PROPERTIES = "properties";
	public static final String TYPE_MAP = "map";
	public static final String TYPE_BYTES = "bin";
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

	private void setField(String name, String type, Object value) {
		fields.put(name, new SField(type, value));
	}

	private void setField(String name, Properties properties) {
		Structure s = new Structure(properties);
		fields.put(name, new SField(TYPE_SUBSTRUCT, s));
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

	public Object getFieldValue(String name) {
		return fields.get(name).getValue();
	}

	public SField getField(String name) {
		return fields.get(name);
	}

	public IStructurable toObject() throws Exception {
		IStructurable result = null;
		result = getClassFromName().newInstance();
		result.fromStructure(this);
		return result;
	}

	private Class<? extends IStructurable> getClassFromName() throws Exception {
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
		return (String) fields.get(name).getValue();
	}

	public int getInt(String name) {
		return (Integer) fields.get(name).getValue();
	}

	public byte[] getByteArray(String name) {
		return (byte[]) fields.get(name).getValue();
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
					s.setByteArrayField("field", (byte[]) o);
				} else if (o instanceof Double) {
					s.setDecimalField("field", (Double) o);
				}
				m.put(key, s);
			}
		}
		fields.put(name, new SField(TYPE_MAP, m));

	}

	public void setStructureMapField(String name, Map<String, Structure> map) {
		setField(name, TYPE_MAP, map);
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
	public void setListField(String string, Serializable[] objects)
			throws Exception {
		List<Structure> list = new ArrayList<Structure>();
		for (Serializable o : objects) {
			if (o instanceof IStructurable) {
				IStructurable s = (IStructurable) o;
				list.add(s.toStructure());
			} else {
				Structure s = new Structure("simple");
				if (o instanceof Integer) {
					s.setIntField("field", (Integer) o);
				} else if (o instanceof String) {
					s.setStringField("field", (String) o);
				} else if (o instanceof byte[]) {
					s.setByteArrayField("field", (byte[]) o);
				} else if (o instanceof Double) {
					s.setDecimalField("field", (Double) o);
				} else if (o instanceof List<?>) {
					s.setStructureListField("field", (List<Structure>) o);
				} else if (o instanceof Map<?, ?>) {
					s.setStructureMapField("field", (Map<String, Structure>) o);
				}
				list.add(s);
			}
		}
		setField(string, TYPE_LIST, list);
	}

	public Serializable[] getArray(String name) throws Exception {
		@SuppressWarnings("unchecked")
		List<Structure> value = (List<Structure>) getField(name).getValue();
		Serializable[] result = new Serializable[value.size()];
		for (int i = 0; i < value.size(); i++) {
			Structure s = value.get(i);
			if (s.getName().equals("simple")) {
				SField f = value.get(i).getField("field");
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

	public void setByteArrayField(String name, byte[] bytearray) {
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
		}
		if (o instanceof IStructurable) {
			IStructurable value = (IStructurable) o;
			setField(name, TYPE_SUBSTRUCT, value.toStructure());
		} else {
			Structure s = new Structure("simple");
			if (o instanceof Integer) {
				s.setIntField("field", (Integer) o);
			} else if (o instanceof String) {
				s.setStringField("field", (String) o);
			} else if (o instanceof byte[]) {
				s.setByteArrayField("field", (byte[]) o);
			} else if (o instanceof Double) {
				s.setDecimalField("field", (Double) o);
			}
			setField(name, TYPE_SUBSTRUCT, s);
		}
	}

	public void setStructureListField(String name, List<Structure> l) {
		setField(name, TYPE_LIST, l);
	}

	public void setDecimalField(String name, double value) {
		setField(name, TYPE_DECIMAL, value);
	}

	public void setStringField(String name, String value) {
		setField(name, TYPE_STRING, value);

	}

	public void setProprietiesField(Properties properties) {
		setField(TYPE_PROPERTIES, properties);
	}

	public void setNullField(String name, String type) {
		setField(name, type, null);
	}

	public void addStructureListField(String name, Structure substr,
			int eltid) {
		List<Structure> list = getStructureList(name);
		if (list == null) {
			JLG.debug("List is null");
			list = new ArrayList<Structure>();
			setStructureListField(name, list);
		}
		
		list.add(eltid, substr);
	}

	public void addStructureMapField(String name, Structure substr,
			String key) {
		Map<String, Structure> map = getStructureMap(name); 
		if (map == null) {
			JLG.debug("map is null");
			map = new HashMap<String, Structure>();
			setStructureMapField(name, map);
		}
		
		map.put(key, substr);		
	}

}
