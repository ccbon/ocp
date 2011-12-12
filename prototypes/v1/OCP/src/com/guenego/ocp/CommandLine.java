package com.guenego.ocp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.guenego.misc.JLG;

public class CommandLine implements UserInterface {
	private String prompt = "?>";

	private Client client;

	private Agent agent;

	public CommandLine(Agent _agent) {
		agent = _agent;
		client = _agent.client;
	}

	public void run() {
		boolean keepGoing = true;
		while (keepGoing) {
			System.out.print(prompt);
			try {
				InputStreamReader isr = new InputStreamReader(System.in);
				BufferedReader br = new BufferedReader(isr);
				String s = br.readLine();
				JLG.debug(s);
				keepGoing = process(s);
			} catch (Exception e) {
				JLG.error(e);
			}
		}
	}

	private boolean process(String s) {
		if (s.equalsIgnoreCase("q") || s.equalsIgnoreCase("quit")) {
			agent.stop();
			return false;
		}

		if (s.equalsIgnoreCase("h") || s.equalsIgnoreCase("help")) {
			printHelp();
		} else if (s.equalsIgnoreCase("c") || s.equalsIgnoreCase("create")) {
			createUser();
		} else {
			badCommand(s);
		}
		return true;

	}

	private void createUser() {
		try {
			// provide the hash(login + password)
			// only the server storing your key can create the captcha
			// user key = hash(login + password)
			// backup key backup(key) = [
			// key1=hash(login+password)
			// key2=hash(key+login+password)
			// etc.
			
		} catch (Exception e) {
			JLG.error(e);
		}

	}

	private void badCommand(String s) {
		JLG.println("Bad command. (" + s + ")");
	}

	private void printHelp() {
		JLG.println("Command List:\n" + "q, quit: quit the application.\n"
				+ "h, help: print this message.\n" + "c, create: create a user");

	}
}
