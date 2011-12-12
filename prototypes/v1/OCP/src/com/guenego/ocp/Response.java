package com.guenego.ocp;

public class Response {

	private Contact contact;
	private String response;

	public Response(String sResponse, Contact contact) {
		this.response = sResponse;
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

	public String getString() {
		return response;
	}

	public void checkForError() throws Exception {
		if (response.startsWith(Protocol.ERROR)) {
			String[] al = response.split(":");
			if (al.length > 2) {
				throw new Exception("Error from server: " + al[1]);
			} else {
				throw new Exception("Error from server.");
			}
		}
	}


}
