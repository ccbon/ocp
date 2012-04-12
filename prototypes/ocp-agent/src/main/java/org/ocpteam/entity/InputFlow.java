package org.ocpteam.entity;

import java.io.Serializable;

import org.ocpteam.interfaces.IActivity;

public class InputFlow implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient public IActivity activity;
	public int activityid;
	public Serializable[] objects;

	public InputFlow(IActivity activity, Serializable... objects) {
		this.activity = activity;
		this.activityid = activity.getId();
		this.objects = objects;
	}

}
