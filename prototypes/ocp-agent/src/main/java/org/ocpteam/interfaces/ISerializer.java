package org.ocpteam.interfaces;

import java.io.Serializable;

public interface ISerializer {
	byte[] serialize(Serializable s) throws Exception;

	Serializable deserialize(byte[] input) throws Exception;
}
