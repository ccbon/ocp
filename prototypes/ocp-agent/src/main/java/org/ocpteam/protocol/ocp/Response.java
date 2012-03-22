package org.ocpteam.protocol.ocp;

public class Response {

	private OCPContact contact;
	private byte[] response;

	public Response(byte[] response, OCPContact contact) {
		this.response = response;
		this.contact = contact;
	}

	public OCPContact getContact() {
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

	public boolean isSuccess() {
		return new String(response).equals(new String(OCPProtocol.SUCCESS));
	}


}
