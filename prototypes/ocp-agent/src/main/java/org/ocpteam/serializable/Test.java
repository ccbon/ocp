package org.ocpteam.serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ocpteam.component.FListMarshaler;
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
		result.setByteArrayField("b", new byte[] { 23, 45 });

		result.setByteArrayField("bb", generateBin());
		result.setStringField("e", "hello world");
		result.setStringField("eee", generateString());

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
		list.add(null);
		list.add(new Structure("keke"));
		result.setStructureListField("G", list);

		result.setStructureMapField("ff", null);

		Map<String, Structure> map = new HashMap<String, Structure>();
		map.put("key1", substruct);
		map.put("key2", substruct2);
		map.put("key3", null);
		map.put("key4", null);
		result.setStructureMapField("gg", map);
		return result;
	}

	private String generateString() {
		String s = "jfdhkdfhbvosdaifuhgweigfsdgvsfjugbfvjifdihfvkgvishvsbvisbj";
		return ("ćș " + s + s + s + "EOF" + s + s + FListMarshaler.NL + "EOF");
	}

	private byte[] generateBin() {
		String s = "jfdhkdfhbvosdaifuhgweigfsdgvsfjugbfvjifdihfvkgvishvsbvisbj";
		return (s + s + s + s + s).getBytes();
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
	
	}

}
