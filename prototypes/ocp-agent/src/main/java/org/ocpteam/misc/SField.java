package org.ocpteam.misc;

import java.io.Serializable;
import java.util.Arrays;

public class SField implements Serializable {
	private static final long serialVersionUID = 1L;
	private String type;
	private Serializable value;

	public SField(String type, Serializable value) {
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		LOG.debug("Testing the field type " + type);
		if (!(obj instanceof SField)) {
			LOG.debug("Not a Field");
			return false;
		}
		SField f = (SField) obj;
		if (!type.equals(f.getType())) {
			LOG.debug("Not same type of Field");
			return false;
		}
		LOG.debug("Type OK: Type=" + type);
		if (value == null && f.getValue() != null) {
			LOG.debug("f1.value=null | f2.value!=null");
			LOG.debug("f2.value=" + f.getValue());
			return false;
		}
		if (type.equals(Structure.TYPE_BYTES)) {
			return Arrays.equals((byte[]) this.value, (byte[]) f.value);
		}
		
		if (value != null && (!value.equals(f.getValue()))) {
			LOG.debug("Not same value of Field");
			LOG.debug("This=" + value);
			LOG.debug("Given=" + f.getValue());
			return false;
		}
		LOG.debug("Value OK: Value=" + value);
		return true;
	}

	@Override
	public String toString() {
		return "Type=" + type + " | Value=" + value;
	}
}
