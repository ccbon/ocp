package org.ocpteam.component;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ocpteam.interfaces.ISerializer;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;

public class FListSerializer implements ISerializer {

	@Override
	public byte[] serialize(Serializable s) throws Exception {
		Structure struct = toStructure(s);
		return new FListMarshaler().marshal(struct);
	}

	private Structure toStructure(Serializable s) throws Exception {
		if (s instanceof IStructurable) {
			Structure struct = ((IStructurable) s).toStructure();
			return struct;
		}
		if (s instanceof Integer) {
			Structure struct = new Structure(Structure.NAME_SIMPLE);
			struct.setIntField(Structure.FIELDNAME_SIMPLE, (Integer) s);
			return struct;
		}
		if (s instanceof String) {
			Structure struct = new Structure(Structure.NAME_SIMPLE);
			struct.setStringField(Structure.FIELDNAME_SIMPLE, (String) s);
			return struct;
		}
		if (s instanceof byte[]) {
			Structure struct = new Structure(Structure.NAME_SIMPLE);
			struct.setBinField(Structure.FIELDNAME_SIMPLE, (byte[]) s);
			return struct;
		}
		if (s instanceof Double) {
			Structure struct = new Structure(Structure.NAME_SIMPLE);
			struct.setDecimalField(Structure.FIELDNAME_SIMPLE, (Double) s);
			return struct;
		}
		if (s instanceof Properties) {
			Structure struct = new Structure(Structure.NAME_PROPERTIES);
			Properties props = (Properties) s;
			int i = 0;
			for (Object o : props.keySet()) {
				Structure substr = new Structure(Structure.NAME_PROPENTRY);
				String key = (String) o;
				substr.setStringField("key", key);
				substr.setStringField("value", props.getProperty(key));
				struct.addStructureListField(Structure.FIELDNAME_PROPENTRY,
						substr, i);
				i++;
			}
			return struct;
		}
		if (s instanceof Map<?, ?>) {
			Structure struct = new Structure(Structure.NAME_MAP);
			@SuppressWarnings("unchecked")
			Map<Serializable, Serializable> map = (Map<Serializable, Serializable>) s;
			int i = 0;
			for (Object o : map.keySet()) {
				Serializable key = (Serializable) o;
				Serializable value = map.get(key);
				Structure substr = new Structure(Structure.NAME_MAPENTRY);
				substr.setSubstructField("key", key);
				substr.setSubstructField("value", value);
				struct.addStructureListField(Structure.FIELDNAME_MAPENTRY,
						substr, i);
				i++;
			}
			return struct;
		}
		if (s.getClass().isArray()) {
			JLG.debug("s.getClass()=" + s.getClass());
			Structure struct = new Structure(Structure.NAME_LIST);
			for (int i = 0; i < Array.getLength(s); i++) {
				Structure substr = toStructure((Serializable) Array.get(s, i));
				struct.addStructureListField(Structure.FIELDNAME_LISTENTRY,
						substr, i);
			}
			return struct;
		}
		throw new Exception(
				"Serializable object not convertible into Structure. s="
						+ s.getClass());
	}

	@Override
	public Serializable deserialize(byte[] input) throws Exception {
		if (input == null) {
			JLG.debug("Want to dezerialize null input");
		}
		Structure s = new FListMarshaler().unmarshal(input);
		return toSerializable(s);
	}

	private Serializable toSerializable(Structure s)
			throws Exception {
		if (s.getName().equals(Structure.NAME_SIMPLE)) {
			return s.getFieldValue(Structure.FIELDNAME_SIMPLE);
		} else if (s.getName().equals(Structure.NAME_MAP)) {
			Map<Serializable, Serializable> map = new HashMap<Serializable, Serializable>();
			if (s.getStructureList(Structure.FIELDNAME_MAPENTRY) != null) {
				for (Structure substr : s
						.getStructureList(Structure.FIELDNAME_MAPENTRY)) {
					Serializable key = toSerializable(substr
							.getSubstruct("key"));
					Serializable value = toSerializable(substr
							.getSubstruct("value"));
					map.put(key, value);
				}
			}
			return (Serializable) map;
		} else if (s.getName().equals(Structure.NAME_PROPERTIES)) {
			Properties p = new Properties();
			if (s.getStructureList(Structure.FIELDNAME_PROPENTRY) != null) {
				for (Structure substr : s
						.getStructureList(Structure.FIELDNAME_PROPENTRY)) {
					String key = substr.getString("key");
					String value = substr.getString("value");
					p.setProperty(key, value);
				}
			}
			return p;
		} else if (s.getName().equals(Structure.NAME_LIST)) {
			List<Serializable> list = new ArrayList<Serializable>();
			Class<?> c = null;
			if (s.getStructureList(Structure.FIELDNAME_LISTENTRY) != null) {
				for (Structure substr : s
						.getStructureList(Structure.FIELDNAME_LISTENTRY)) {
					Serializable item = toSerializable(substr);
					JLG.debug("item.class=" + item.getClass());
					c = item.getClass();
					list.add(item);
				}
			} else {
				if (c == null) {
					throw new Exception(
							"Cannot guess the cast for a null list.");
				}
			}
			Serializable result = (Serializable) Array.newInstance(c,
					list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(result, i, list.get(i));
			}
			return result;
		} else {
			return s.toObject();
		}
	}
}
