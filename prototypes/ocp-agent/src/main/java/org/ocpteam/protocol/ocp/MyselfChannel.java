package org.ocpteam.protocol.ocp;

import org.ocpteam.component.Agent;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.JLGException;
import org.ocpteam.misc.URL;

public class MyselfChannel extends Channel {

	private Agent agent;
	

	public MyselfChannel(URL url, Agent agent) {
		this.agent = agent;
		this.url = url;
	}

	public MyselfChannel() {
	}

	@Override
	public byte[] request(byte[] input) throws Exception {
		JLG.debug("agent=" + agent);
		JLG.debug("agent.ds=" + agent.ds);
		return agent.ds.getDesigner().get(IProtocol.class).process(input, null);
	}

	@Override
	public OCPContact getContact() throws JLGException {
		throw new JLGException("why do you need to call me ?");
	}

	@Override
	public String toString() {
		if (url == null) {
			return "<url_not_specified>";
		}
		return url.toString();
	}

}
