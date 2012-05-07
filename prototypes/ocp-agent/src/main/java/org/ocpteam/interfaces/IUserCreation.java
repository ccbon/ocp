package org.ocpteam.interfaces;



public interface IUserCreation {
	
	void createUser() throws Exception;

	/**
	 * Set a captcha before creating user if needed.
	 * @param captcha
	 */
	void setCaptcha(ICaptcha captcha);

	void setUser(String string) throws Exception;

	void setPassword(String password);

	String getPassword();

	IUser getUser();

	ICaptcha getCaptcha() throws Exception;

	boolean needsCaptcha();
	
	String getAnswer();

	void setAnswer(String answer);
	
}
