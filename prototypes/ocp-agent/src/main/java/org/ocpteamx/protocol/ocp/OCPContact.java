package org.ocpteamx.protocol.ocp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ocpteam.misc.Id;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.Contact;

public class OCPContact extends Contact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte[] publicKey;
	public SortedSet<Id> nodeIdSet;

	public OCPContact() {
		super();
		nodeIdSet = Collections.synchronizedSortedSet(new TreeSet<Id>());
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = super.toStructure();
		result.setBinField("publicKey", publicKey);
		List<Structure> list = new ArrayList<Structure>();
		for (Id id : nodeIdSet) {
			list.add(id.toStructure());
		}
		result.setStructureListField("nodeIdSet", list);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		super.fromStructure(s);
		List<Structure> list = s.getStructureList("nodeIdSet");
		if (list != null) {
			for (Structure struct : list) {
				nodeIdSet.add((Id) struct.toObject());
			}
		}
		publicKey = s.getBin("publicKey");
	}

}
