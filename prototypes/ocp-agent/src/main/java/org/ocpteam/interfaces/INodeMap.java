package org.ocpteam.interfaces;

import org.ocpteam.core.IComponent;
import org.ocpteam.entity.Address;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Node;


public interface INodeMap extends IComponent {

	void put(Node node, Contact c) throws Exception;

	void remove(Node node);

	void removeAll();

	Contact getPredecessor(Node node) throws Exception;
	
	Contact getSuccessor(Node node) throws Exception;

	boolean isResponsible(Address address) throws Exception;

	void askForNode() throws Exception;

}
