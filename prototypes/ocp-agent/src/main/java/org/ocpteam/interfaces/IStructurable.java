package org.ocpteam.interfaces;

import java.io.Serializable;

import org.ocpteam.misc.Structure;

public interface IStructurable extends Serializable {
	Structure toStructure() throws Exception;
	void fromStructure(Structure s) throws Exception;
}
