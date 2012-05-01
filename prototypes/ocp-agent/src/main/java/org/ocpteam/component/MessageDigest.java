package org.ocpteam.component;


public class MessageDigest {

	private java.security.MessageDigest md;

	public byte[] hash(byte[] input) {
		return md.digest(input);
	}

	public void setAlgo(String algo) throws Exception {
		md = java.security.MessageDigest.getInstance(algo);
	}

}
