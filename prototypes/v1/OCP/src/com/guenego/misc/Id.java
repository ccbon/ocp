package com.guenego.misc;

import java.io.Serializable;
import java.math.BigInteger;


public class Id implements Serializable, Comparable<Id> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] value;
	public Id(byte[] val) {
		value = val;
	}
	
	public Id(String string) throws Exception {
		if (string.length() >= 2) {
			value = JLG.hexToBytes(string);
		} else {
			value = new byte[1];
			value[0] = 0; 
		}
	}

	@Override
	public String toString() {
		return JLG.bytesToHex(value);
	}

	@Override
	public int compareTo(Id o) {
		BigInteger me = new BigInteger(toString(), 16);
		BigInteger it = new BigInteger(o.toString(), 16);
		return me.compareTo(it);
	}
	
	@Override
	public boolean equals(Object obj) {
		//JLG.debug("equals?" + this + "=" + obj);
		return toString().equals(obj.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public byte[] getBytes() {
		return value;
	}
	

}
