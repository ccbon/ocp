package org.ocpteam.protocol.ocp;

import org.ocpteam.component.DSContainer;
import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.interfaces.IUserCreation;

public class OCPUserCreation extends DSContainer<OCPDataSource> implements IUserCreation {

	private String login;
	private String password;
	private int backupNbr;
	private Captcha captcha;
	private String answer;

	@Override
	public ICaptcha getCaptcha() throws Exception {
		return ds().agent.wantToCreateUser(login, password);
	}

	@Override
	public void createUser() throws Exception {
		ds().agent.createUser(login, password, backupNbr, captcha, answer);
	}

}
