package org.ocpteam.layer.rsp;

import org.ocpteam.functionality.Agent;
import org.ocpteam.functionality.DataModel;

public class Context {

	public Agent agent;
	public DataModel dataModel;

	public Context(Agent agent, DataModel dataModel, String location) {
		this.agent = agent;
		this.dataModel = dataModel;
		
	}

}
