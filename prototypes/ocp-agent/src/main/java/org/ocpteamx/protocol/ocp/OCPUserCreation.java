package org.ocpteamx.protocol.ocp;

import org.ocpteam.component.DSContainer;
import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.interfaces.IUser;
import org.ocpteam.interfaces.IUserCreation;

public class OCPUserCreation extends DSContainer<OCPDataSource> implements
		IUserCreation {

	private IUser user;
	private String password;
	private Captcha captcha;
	private String answer;

	@Override
	public void createUser() throws Exception {
		
		ds().agent.createUser(user.getUsername(), password, 2, captcha,
				answer);
	}

	@Override
	public void setCaptcha(ICaptcha captcha) {
		this.captcha = (Captcha) captcha;

	}

	@Override
	public void setUser(String username) throws Exception {
		this.user = new OCPUser(ds().agent, username, 2);
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public IUser getUser() {
		return user;
	}

	@Override
	public ICaptcha getCaptcha() throws Exception {
		return ds().agent.wantToCreateUser(user.getUsername(), password);
	}

	@Override
	public boolean needsCaptcha() {
		return true;
	}

	@Override
	public String getAnswer() {
		return answer;
	}

	@Override
	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
