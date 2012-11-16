package org.ocpteamx.protocol.ocp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.Pointer;

public class UserIndex implements IStructurable {

	private static final long serialVersionUID = 1L;

	private NavigableSet<Pointer> set;

	public UserIndex() {
		set = new TreeSet<Pointer>();
	}

	public void add(Pointer pointer) {
		set.add(pointer);

	}

	public Iterator<Pointer> iterator() {
		return set.iterator();
	}

	public void remove(Pointer pointer) {
		set.remove(pointer);
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		List<Structure> list = new ArrayList<Structure>();
		for (Pointer p : set) {
			list.add(p.toStructure());
		}
		result.setStructureListField("set", list);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		List<Structure> list = s.getStructureList("set");
		for (Structure struct : list) {
			add((Pointer) struct.toObject());
		}
	}
}
