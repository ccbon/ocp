package org.ocpteam.interfaces;

import java.util.Queue;

import org.ocpteam.core.IComponent;
import org.ocpteam.entity.Contact;
import org.ocpteam.entity.Node;
import org.ocpteam.misc.Id;


public interface INodeMap extends IComponent {

	void put(Node node, Contact c);

	void remove(Node node);

	void removeAll();

	Contact getPredecessor() throws Exception;

	boolean isResponsible(Id address) throws Exception;

	Queue<Contact> getContactQueue(Id address) throws Exception;

}
