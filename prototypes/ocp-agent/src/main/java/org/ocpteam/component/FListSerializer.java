package org.ocpteam.component;

import java.io.Serializable;
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
			for (Object o : props.keySet()) {
				String key = (String) o;
				struct.setStringField(key, props.getProperty(key));
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

	private Serializable toSerializable(Structure s) throws Exception {
		if (s.getName().equals(Structure.NAME_SIMPLE)) {
			return s.getFieldValue(Structure.FIELDNAME_SIMPLE);
		} else if (s.getName().equals(Structure.NAME_PROPERTIES)) {
			Properties p = new Properties();
			for (String fieldname : s.getFields().keySet()) {
				p.setProperty(fieldname, s.getStringField(fieldname));
			}
			return p;
		} else {
			return s.toStructurable();
		}
	}
}
