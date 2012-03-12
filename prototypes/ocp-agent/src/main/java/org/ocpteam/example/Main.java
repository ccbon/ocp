package org.ocpteam.example;

import org.ocpteam.core.DefaultApplication;

public class Main {
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			DefaultApplication app = new DefaultApplication();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
