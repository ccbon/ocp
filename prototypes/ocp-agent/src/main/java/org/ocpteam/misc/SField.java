package org.ocpteam.misc;

import java.io.Serializable;
import java.util.Arrays;

public class SField {
	private String type;
	private Object value;

	public SField(String type, Object value) {
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		JLG.debug("Testing the field type " + type);
		if (!(obj instanceof SField)) {
			JLG.debug("Not a Field");
			return false;
		}
		SField f = (SField) obj;
		if (!type.equals(f.getType())) {
			JLG.debug("Not same type of Field");
			return false;
		}
		JLG.debug("Type OK: Type=" + type);
		if (type.equals("bytes")) {
			return Arrays.equals((byte[]) this.value, (byte[]) f.value);
		}
		if (!value.equals(f.getValue())) {
			JLG.debug("Not same value of Field");
			JLG.debug("This=" + value);
			JLG.debug("Given=" + f.getValue());
			return false;
		}
		JLG.debug("Value OK: Value=" + value);
		return true;
	}

	@Override
	public String toString() {
		return "Type=" + type + " | Value=" + value;
	}
}
