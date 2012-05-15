package org.ocpteam.interfaces;

import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.SecureUser;
import org.ocpteam.serializable.UserPublicInfo;

public interface ISecurity {

	void generateKeyPair(SecureUser secureUser) throws Exception;

	void generateSecretKey(SecureUser secureUser) throws Exception;

	UserPublicInfo getPublicInfo(SecureUser secureUser);

	byte[] passwordCrypt(String password, byte[] value) throws Exception;

	byte[] passwordDecrypt(String password, byte[] value) throws Exception;
	
	byte[] crypt(SecureUser secureUser, byte[] value) throws Exception;

	byte[] decrypt(SecureUser secureUser, byte[] value) throws Exception;
	
	byte[] sign(SecureUser secureUser, byte[] value) throws Exception;
	
	boolean verify(UserPublicInfo upi, byte[] value, byte[] signature) throws Exception;

	void putUPI(SecureUser secureUser) throws Exception;
	
	UserPublicInfo getUPI(String username) throws Exception;
	
	void putUser(SecureUser secureUser, String password) throws Exception;
	
	SecureUser getUser(String username, String password) throws Exception;

	void put(SecureUser secureUser, Address address, byte[] value) throws Exception;
	
	byte[] get(SecureUser secureUser, Address address) throws Exception;

}
