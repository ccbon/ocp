package com.guenego.ocp;

import java.io.Serializable;

import com.guenego.storage.Agent;

public abstract class Content implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	protected byte[] signature = null;
	protected byte[] username = null;
	

	public boolean isLink() {
		return this.getClass() == Link.class;
	}


	public abstract Key getKey(Agent agent) throws Exception;


	public abstract byte[] getContent() throws Exception;




}
