package org.ocpteam.interfaces;

import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.SecureUser;

public interface IUserBackup {

	Address[] getAddresses(SecureUser secureUser, Address address) throws Exception;

}
