package org.ocpteam.interfaces;

import org.ocpteam.core.IComponent;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Node;
import org.ocpteam.misc.Id;


public interface INodeMap extends IComponent {

	void put(Node node, Contact c) throws Exception;

	void remove(Node node);

	void removeAll();

	Contact getPredecessor() throws Exception;

	boolean isResponsible(Id address) throws Exception;

}
