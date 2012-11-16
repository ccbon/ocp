package org.ocpteamx.protocol.ocp;

import org.ocpteam.interfaces.IStructurable;

public abstract class Content implements IStructurable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	protected byte[] signature = null;
	protected byte[] username = null;
	

	public boolean isLink() {
		return this.getClass() == Link.class;
	}


	public abstract Key getKey(OCPAgent agent) throws Exception;


	public abstract byte[] getContent() throws Exception;




}
