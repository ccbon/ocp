package org.ocpteam.entity;

import java.io.Serializable;

import org.ocpteam.interfaces.ITransaction;

public class InputMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	transient public ITransaction transaction;
	public int transid;
	public Serializable[] objects;

	public InputMessage(ITransaction transaction, Serializable... objects) {
		this.transaction = transaction;
		this.objects = objects;
		this.transid = transaction.getId();
	}
	
}
