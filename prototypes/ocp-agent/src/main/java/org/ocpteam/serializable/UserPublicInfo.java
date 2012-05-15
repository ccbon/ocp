package org.ocpteam.serializable;

import java.security.PublicKey;

public class UserPublicInfo extends User {

	private static final long serialVersionUID = 1L;
	private PublicKey publicKey;
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

}
