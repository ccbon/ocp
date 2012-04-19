package org.ocpteam.component;

import java.util.Queue;

import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Node;
import org.ocpteam.interfaces.INodeMap;
import org.ocpteam.misc.Id;

public class RingNodeMap extends DataSourceContainer implements INodeMap {

	@Override
	public void put(Node node, Contact c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Contact getPredecessor() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isResponsible(Id address) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Queue<Contact> getContactQueue(Id address) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
