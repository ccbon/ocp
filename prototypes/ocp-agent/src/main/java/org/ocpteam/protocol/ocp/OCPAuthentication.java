package org.ocpteam.protocol.ocp;

import org.ocpteam.component.Authentication;

public class OCPAuthentication extends Authentication {

	public OCPAuthentication() {
		super();
	}
	
	@Override
	public boolean allowsUserCreation() {
		return true;
	}
}
