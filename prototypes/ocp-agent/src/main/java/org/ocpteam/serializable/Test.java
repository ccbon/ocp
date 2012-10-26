package org.ocpteam.serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

public class Test implements IStructurable {

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure("Test");
		result.setField("tt", "int", 1);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
