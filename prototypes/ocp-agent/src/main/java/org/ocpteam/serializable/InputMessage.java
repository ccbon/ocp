package org.ocpteam.serializable;

import java.io.Serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.Structure;

public class InputMessage implements IStructurable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	transient public ITransaction transaction;
	public int transid;
	public Serializable[] objects;
	
	public InputMessage() {
		
	}

	public InputMessage(ITransaction transaction, Serializable... objects) {
		this.transaction = transaction;
		this.objects = objects;
		this.transid = transaction.getId();
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setIntField("transid", transid);
		LOG.info("objects=" + objects);
		result.setListField("objects", objects);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		transid = s.getIntField("transid");
		objects = s.getListField("objects");
	}
}
