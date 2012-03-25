package org.ocpteam.interfaces;

import java.io.Serializable;

import org.ocpteam.entity.Session;

public interface ITransaction {
	Serializable run(Session session, Serializable[] objects) throws Exception;

	int getId();
}
