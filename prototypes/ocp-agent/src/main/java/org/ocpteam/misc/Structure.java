package org.ocpteam.misc;

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

	public void setField(String name, String type, Object value) {
		fields.put(name, new SField(type, value));
	}

	public void setField(String name, Properties properties) {
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
		IStructurable result = getClassFromName().newInstance();
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

	public void setArray(String string, IStructurable[] objects) throws Exception {
		List<Structure> list = new ArrayList<Structure>();
		for (IStructurable o : objects) {
			list.add(o.toStructure());
		}
		setField(string, "list", list);
	}

	public IStructurable[] getArray(String name) throws Exception {
		@SuppressWarnings("unchecked")
		List<Structure> value = (List<Structure>) getField(name).getValue();
		IStructurable[] result = new IStructurable[value.size()];
		for (int i = 0; i < value.size(); i++) {
			result[i] = value.get(i).toObject();
		}
		return result;
	}

}
