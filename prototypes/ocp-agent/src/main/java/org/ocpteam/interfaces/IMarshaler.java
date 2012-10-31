package org.ocpteam.interfaces;

import org.ocpteam.misc.Structure;

public interface IMarshaler {
	byte[] marshal(Structure s) throws Exception;
	Structure unmarshal(byte[] array) throws Exception;
}
