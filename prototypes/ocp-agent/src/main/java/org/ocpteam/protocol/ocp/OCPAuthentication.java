package org.ocpteam.protocol.ocp;

import java.net.URI;

import org.ocpteam.layer.rsp.Authentication;

public class OCPAuthentication extends Authentication {

	public OCPAuthentication(URI uri) {
		super(uri);
	}
	
	@Override
	public boolean allowsUserCreation() {
		return true;
	}
}
