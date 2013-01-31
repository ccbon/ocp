package org.ocpteam.serializable;

import java.io.Serializable;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.Structure;

/**
 * This class indicates that we reached the EOM (End Of Message)
 *
 */
public class EOMObject implements Serializable, IStructurable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(EOMObject.class);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		LOG.info("End Of Message");
	}

}
