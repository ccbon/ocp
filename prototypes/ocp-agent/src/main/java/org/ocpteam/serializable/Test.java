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
	int a;
	String e;
	byte[] b;

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setIntField("a", 1);
		result.setByteArrayField("b", new byte[] {23,45});
		result.setStringField("e", "hello world");
		
		Structure substruct = new Structure("coucou");
		substruct.setDecimalField("XX", 12.34);
		substruct.setDecimalField("YY", 12.34);
		
		Structure substruct2 = new Structure("coucou2");
		substruct2.setDecimalField("XXX", 56.78);
		
		substruct.setStructureSubstructField("DDDD", substruct2);
		
		result.setStructureSubstructField("dd", substruct);
		result.setStructureSubstructField("ee", null);
		
		result.setStructureListField("F", null);
		
		List<Structure> list = new ArrayList<Structure>();
		list.add(new Structure("kiki"));
		list.add(new Structure("keke"));
		result.setStructureListField("G", list );
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		e = s.getString("e");
		a = s.getInt("a");
		b = s.getByteArray("b");
	}

}
