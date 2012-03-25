package org.ocpteam.entity;

import java.io.Serializable;

import org.ocpteam.interfaces.ITransaction;

public class InputMessage {

	public ITransaction transaction;
	public Serializable[] objects;

	public InputMessage(ITransaction transaction, Serializable... objects) {
		this.transaction = transaction;
		this.objects = objects;
	}
	
}
