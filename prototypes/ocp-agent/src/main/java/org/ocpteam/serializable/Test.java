package org.ocpteam.serializable;

import java.util.ArrayList;
import java.util.List;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

public class Test implements IStructurable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setField("a", "int", 1);
		result.setField("b", "bytes", new byte[]{0,1,2,3});
		result.setField("c", "substruct", new Address("0123").toStructure());
		List<Structure> list = new ArrayList<Structure>();
		list.add(new Address("0123").toStructure());
		list.add(new Address("2345").toStructure());
	
		result.setField("d", "list", list);
		result.setField("e", "string", "hello\\world");
		result.setField("f", "decimal", 12345.1234567);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
