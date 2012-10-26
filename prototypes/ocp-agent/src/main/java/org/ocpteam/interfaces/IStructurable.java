package org.ocpteam.interfaces;

import org.ocpteam.misc.Structure;

public interface IStructurable {
	Structure toStructure() throws Exception;
	void fromStructure(Structure s) throws Exception;
}
