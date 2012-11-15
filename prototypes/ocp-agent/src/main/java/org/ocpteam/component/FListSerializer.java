package org.ocpteam.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.ocpteam.interfaces.ISerializer;
import org.ocpteam.interfaces.IStructurable;
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
			struct.setByteArrayField(Structure.FIELDNAME_SIMPLE, (byte[]) s);
			return struct;
		}
		if (s instanceof Double) {
			Structure struct = new Structure(Structure.NAME_SIMPLE);
			struct.setDecimalField(Structure.FIELDNAME_SIMPLE, (Double) s);
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
		throw new Exception(
				"Serializable object not convertible into Structure. s=" + s.getClass());
	}

	@Override
	public Serializable deserialize(byte[] input) throws Exception {
		Structure s = new FListMarshaler().unmarshal(input);
		return toSerializable(s);
	}

	private Serializable toSerializable(Structure s) throws Exception {
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
		} else {
			return s.toObject();
		}
	}

}
