package org.ocpteam.serializable;

import java.io.Serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.Structure;

public class InputMessage implements IStructurable {

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

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setField("transid", "int", transid);
		//result.setArray("objects", objects);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		transid = s.getInt("transid");
		objects = s.getArray("objects");
	}
	
}
