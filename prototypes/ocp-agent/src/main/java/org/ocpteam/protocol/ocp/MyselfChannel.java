package org.ocpteam.protocol.ocp;

import org.ocpteam.component.Agent;
import org.ocpteam.component.Protocol;
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
		return agent.ds().getComponent(Protocol.class).process(input, null);
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
