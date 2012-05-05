package org.ocpteam.interfaces;

public interface IUserCreation {
	
	ICaptcha getCaptcha() throws Exception;
	
	void createUser() throws Exception;
}
