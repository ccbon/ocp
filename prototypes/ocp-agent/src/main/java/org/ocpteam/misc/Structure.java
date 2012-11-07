package org.ocpteam.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ocpteam.component.JSONMarshaler;
import org.ocpteam.interfaces.IStructurable;

public class Structure {
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
		for (Object o : properties.keySet()) {
			String key = (String) o;
			setField(name, "string", properties.getProperty(key));
		}
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
		fields.put(name, new SField("substruct", s));
	}

	@Override
	public String toString() {
		try {
			return new String(new JSONMarshaler().marshal(this));
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
		if (name.equals("simple")) {
			// SField field = getField("field");
			// String type = field.getType();
			// if (type.equals("string")) {
			// result = new StructString((String) getFieldValue("field"));
			// } else if (type.equals("bytes")) {
			// result = new StructByteArray((byte[]) getFieldValue("field"));
			// }
		} else {
			result = getClassFromName().newInstance();
			result.fromStructure(this);
		}
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
		return (Structure) fields.get(name).getValue();
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
		Properties p = new Properties();
		for (String fname : s.getFields().keySet()) {
			p.setProperty(fname, s.getString(fname));
		}
		return p;
	}

	public void setMapField(String name,
			Map<String, ? extends IStructurable> map) throws Exception {
		Map<String, Structure> m = new HashMap<String, Structure>();
		for (String key : map.keySet()) {
			m.put(key, map.get(key).toStructure());
		}
		fields.put(name, new SField("map", m));
	}

	public void setStructureMapField(String name, Map<String, Structure> map) {
		setField(name, "map", map);
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
	public void setArray(String string, Serializable[] objects)
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
					s.setBytesField("field", (byte[]) o);
				} else if (o instanceof Double) {
					s.setDecimalField("field", (Double) o);
				} else if (o instanceof List<?>) {
					s.setListField("field", (List<Structure>) o);
				} else if (o instanceof Map<?, ?>) {
					s.setStructureMapField("field", (Map<String, Structure>) o);
				}
				list.add(s);
			}
		}
		setField(string, "list", list);
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
				if (type.equals("string")) {
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
			JLG.debug("Testing the field " + this.getField(fname));
			SField f1 = this.getField(fname);
			if (f1 == null) {
				JLG.debug("Field is empty");
				return false;
			}
			SField f2 = s.getField(fname);
			if (!f2.equals(f1)) {
				JLG.debug("Fields are not equal");
				return false;
			}
		}
		return true;
	}

	public void setBytesField(String name, byte[] bytes) {
		setField(name, "bytes", bytes);
	}

	public void setIntField(String name, int value) {
		setField(name, "int", value);
	}

	public void setSubstructField(String name, Structure value) {
		setField(name, "substruct", value);
	}

	public void setListField(String name, List<Structure> l) {
		setField(name, "list", l);
	}

	public void setDecimalField(String name, double value) {
		setField(name, "decimal", value);
	}

	public void setStringField(String name, String value) {
		setField(name, "string", value);

	}

	public void setFieldField(String name, Structure fromJson) {
		setField(name, "field", fromJson);
	}

	public void setProprietiesField(Properties properties) {
		setField("proprieties", properties);
	}
}
