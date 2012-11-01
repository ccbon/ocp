package org.ocpteam.serializable;

import java.security.PublicKey;

import org.ocpteam.interfaces.IStructurable;

public class UserPublicInfo extends User implements IStructurable {

	private static final long serialVersionUID = 1L;
	private PublicKey publicKey;

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
}
