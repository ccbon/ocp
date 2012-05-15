package org.ocpteam.interfaces;

import org.ocpteam.core.IComponent;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.Node;


public interface INodeMap extends IComponent {

	void put(Node node, Contact c) throws Exception;

	void remove(Node node);

	void removeAll();

	Contact getPredecessor(Node node) throws Exception;
	
	Contact getSuccessor(Node node) throws Exception;

	boolean isResponsible(Address address) throws Exception;

	void askForNode() throws Exception;

}
