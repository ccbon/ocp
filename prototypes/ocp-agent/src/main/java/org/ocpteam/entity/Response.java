package org.ocpteam.entity;

import org.ocpteam.protocol.ocp.OCPProtocol;

public class Response {

	private Contact contact;
	private byte[] response;

	public Response(byte[] response, Contact contact) {
		this.response = response;
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

	public byte[] getBytes() {
		return response;
	}

	public void checkForError() throws Exception {
		if (new String(response).startsWith(new String(OCPProtocol.ERROR))) {
			String[] al = new String(response).split(":");
			if (al.length > 2) {
				throw new Exception("Error from server: " + al[1]);
			} else {
				throw new Exception("Error from server.");
			}
		}
	}

}
