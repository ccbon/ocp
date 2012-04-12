package org.ocpteam.interfaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

import org.ocpteam.component.Protocol;
import org.ocpteam.entity.Session;

public interface IActivity {

	void run(Session session, Serializable[] objects,
			DataInputStream in, DataOutputStream out, Protocol protocol) throws Exception;
	
	int getId();

}
