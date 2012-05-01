package org.ocpteam.component;


public class MessageDigest extends DSContainer<DSPDataSource> {

	private java.security.MessageDigest md;

	public void readNetworkConfig() throws Exception {
		setAlgo(ds().network.getProperty("hash", "SHA-1"));
	}
	
	public byte[] hash(byte[] input) {
		return md.digest(input);
	}

	public void setAlgo(String algo) throws Exception {
		md = java.security.MessageDigest.getInstance(algo);
	}

}
