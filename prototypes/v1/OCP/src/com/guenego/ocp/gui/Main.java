package com.guenego.ocp.gui;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.OCPAgent;
import com.guenego.ocp.UserInterface;
import com.guenego.ocp.gui.install.ConfigWizard;

public class Main {
	public static void main(String[] args) {
		try {
			if (!JLG.isFile(Agent.AGENT_PROPERTIES_FILE)) {
				ConfigWizard.start();
			}
			if (!JLG.isFile(Agent.AGENT_PROPERTIES_FILE)) {
				return;
			}
			Agent agent = new OCPAgent();
			agent.loadAgentConfig();
			UserInterface ui = new GraphicalUI(agent);
			agent.start();
			(new Thread(ui)).start();
		} catch (Exception e) {
			JLG.error(e);
		}
	}

}
