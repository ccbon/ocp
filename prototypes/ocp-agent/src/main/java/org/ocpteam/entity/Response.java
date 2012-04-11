package org.ocpteam.entity;

import java.io.Serializable;

public class Response {

	private Contact contact;
	private Serializable response;

	public Response(Serializable response, Contact contact) {
		this.response = response;
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

	public Serializable getObject() {
		return response;
	}

	public void checkForError() throws Exception {
		if (response instanceof Exception) {
			throw (Exception) response;
		}
	}

}
