package org.ocpteam.interfaces;

/**
 * A class may implements this interface to indicate that user must prove they
 * are really themselves with a challenge process (generally a password).
 * 
 * 
 */
public interface IAuthenticable {

	void login() throws Exception;

	void logout() throws Exception;

}
