package org.ocpteam.serializable;

import java.io.Serializable;

import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

public class InputFlow implements IStructurable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient public IActivity activity;
	public int activityid;
	public Serializable[] objects;
	
	public InputFlow() {
		
	}

	public InputFlow(IActivity activity, IStructurable... objects) {
		this.activity = activity;
		this.activityid = activity.getId();
		this.objects = objects;
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setIntField("activityid", activityid);
		result.setListField("objects", objects);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		activityid = s.getIntField("activityid");
		objects = s.getListField("objects");
	}

}
