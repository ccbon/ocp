package org.ocpteam.misc;

import java.util.HashMap;
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

	public Properties getProperties(String name) {
		Structure s = getSubstruct(name);
		Properties p = new Properties();
		for (String fname : s.getFields().keySet()) {
			p.setProperty(fname, s.getString(fname));
		}
		return p;
	}

	
}
