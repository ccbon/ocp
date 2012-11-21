package org.ocpteam.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

public class ContactList implements IStructurable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Contact> list;

	public ContactList() {

	}

	public ContactList(Contact[] contacts) {
		list = Arrays.asList(contacts);
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		Serializable[] list = new Serializable[this.list.size()];
		int i = 0;
		for (Contact c : this.list) {
			list[i] = c;
			i++;
		}
		result.setListField("list", list);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		list = new ArrayList<Contact>();
		Serializable[] array = s.getArray("list");
		if (array != null) {
			for (Serializable ser : array) {
				list.add((Contact) ser);
			}
		}
	}

	public List<Contact> getList() {
		return list;
	}

}
