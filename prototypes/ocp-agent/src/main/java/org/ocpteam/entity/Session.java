package org.ocpteam.entity;

import java.net.Socket;

import org.ocpteam.component.DataSource;

public class Session {

	private Socket socket;
	private DataSource ds;

	public Session(DataSource ds, Socket socket) {
		this.ds = ds;
		this.socket = socket;
	}

	public Socket getSocket() {
		return this.socket;
	}
	
	public DataSource ds() {
		return this.ds;
	}

}
