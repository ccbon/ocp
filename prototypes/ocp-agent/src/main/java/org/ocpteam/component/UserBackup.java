package org.ocpteam.component;

import org.ocpteam.interfaces.IUserBackup;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.SecureUser;

public class UserBackup extends DSContainer<AddressDataSource> implements IUserBackup {

	@Override
	public Address[] getAddresses(SecureUser secureUser, Address address) {
		int backup = Integer.parseInt(secureUser.getProperty("backup", "2"));
		Address[] addresses = new Address[backup];
		for (int i = 0; i < backup; i++) {
			addresses[i] = getAddress(secureUser, address, i);
		}
		return addresses;
	}

	private Address getAddress(SecureUser secureUser, Address address, int i) {
		byte[] input = (address.toString() + i).getBytes();
		return new Address(ds().md.hash(input));
	}

}
