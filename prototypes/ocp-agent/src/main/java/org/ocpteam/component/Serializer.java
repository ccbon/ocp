package org.ocpteam.component;

import java.io.Serializable;

import org.ocpteam.interfaces.ISerializer;
import org.ocpteam.misc.JLG;

public class Serializer implements ISerializer {

	@Override
	public byte[] serialize(Serializable s) throws Exception {
		return JLG.serialize(s);
	}

	@Override
	public Serializable deserialize(byte[] input) throws Exception {
		return JLG.deserialize(input);
	}

}
