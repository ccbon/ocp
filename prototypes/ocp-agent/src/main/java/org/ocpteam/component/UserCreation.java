package org.ocpteam.component;

import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.interfaces.IUserCreation;

/**
 * Component for user creation.
 *
 */
public class UserCreation extends DSContainer<DataSource> implements IUserCreation {

	@Override
	public ICaptcha getCaptcha() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createUser() throws Exception {
	}
	
}
