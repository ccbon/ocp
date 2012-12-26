package org.ocpteam.interfaces;

public interface IUserManagement {

	public void setUsername(String username);
	
	public String getUsername() throws Exception;
	
	public void initFromURI();
	
	public boolean canAutomaticallyLogin();
	
	public void login() throws Exception;
	
	public void logout() throws Exception;
	
}
