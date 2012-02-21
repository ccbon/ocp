package org.ocpteam.protocol.ocp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.ocpteam.misc.JLG;


public class CommandLine implements UserInterface {
	private String prompt = "?>";

	private OCPAgent agent;

	public CommandLine(OCPAgent agent) {
		this.agent = agent;
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
			String login = ask("login");
			String password = ask("password");
			Captcha captcha = agent.wantToCreateUser(login, password);
			String answer = ask("captcha->" + captcha.challengeObject);
			int backupNbr = askInteger("backup nbr");
			agent.createUser(login, password, backupNbr, captcha, answer);
		} catch (Exception e) {
			JLG.error(e);
		}

	}

	private int askInteger(String string) throws Exception {
		System.out.print(string + ":");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		return Integer.parseInt(br.readLine());
	}

	private String ask(String string) throws Exception {
		System.out.print(string + ":");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		return br.readLine();
	}

	private void badCommand(String s) {
		JLG.println("Bad command. (" + s + ")");
	}

	private void printHelp() {
		JLG.println("Command List:\n" + "q, quit: quit the application.\n"
				+ "h, help: print this message.\n" + "c, create: create a user");

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
