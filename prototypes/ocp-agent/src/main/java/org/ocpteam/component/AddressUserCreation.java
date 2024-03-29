package org.ocpteam.component;

import java.io.Serializable;

import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.ISecurity;
import org.ocpteam.interfaces.IUser;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.AddressUser;
import org.ocpteam.serializable.SecureUser;
import org.ocpteam.serializable.UserPublicInfo;

/**
 * Component for user creation.
 * 
 */
public class AddressUserCreation extends DSContainer<AddressDataSource>
		implements IUserCreation, IAuthenticable {

	private IUser user;
	private IAddressMap map;
	private String password;

	@Override
	public void init() throws Exception {
		super.init();
		map = ds().getComponent(IAddressMap.class);
	}

	@Override
	public void createUser() throws Exception {
		if (user == null) {
			throw new Exception("user is null");
		}

		// Is the user already existing on the system?

		if (userExists(user)) {
			throw new Exception("User already exists");
		}

		if (ds().usesComponent(ISecurity.class)) {
			ISecurity security = ds().getComponent(ISecurity.class);
			SecureUser secureUser = (SecureUser) user;
			security.generateKeyPair(secureUser);
			security.generateSecretKey(secureUser);
			security.putUPI(secureUser);

			security.putUser(secureUser, getPassword());
		} else {
			Address address = getUserAddress(user.getUsername(), getPassword());
			byte[] value = ds().serializer.serialize(user);
			map.put(address, value);
		}
	}

	private boolean userExists(IUser user) throws Exception {

		if (ds().usesComponent(ISecurity.class)) {
			ISecurity security = ds().getComponent(ISecurity.class);
			SecureUser secureUser = (SecureUser) user;
			try {
				UserPublicInfo upi = security.getUPI(secureUser.getUsername());
				return (upi != null);
			} catch (Exception e) {
				return false;
			}

		} else {
			Address address = getUserAddress(user.getUsername(), getPassword());
			byte[] value = map.get(address);
			if (value == null) {
				return false;
			}
			try {
				Serializable s = ds().serializer.deserialize(value);
				return (s instanceof IUser);
			} catch (Exception e) {
				return false;
			}
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setCaptcha(ICaptcha captcha) {
	}

	@Override
	public void setUser(String username) throws Exception {
		this.user = ds().getComponent(IUser.class).getClass().newInstance();
		this.user.setUsername(username);
		if (user instanceof AddressUser) {
			AddressUser addressUser = (AddressUser) user;
			addressUser.setRootAddress(new Address(ds().random.generate()));
		}
	}

	@Override
	public IUser getUser() {
		return user;
	}

	@Override
	public ICaptcha getCaptcha() {
		return null;
	}

	@Override
	public boolean needsCaptcha() {
		return false;
	}

	@Override
	public String getAnswer() {
		return null;
	}

	@Override
	public void setAnswer(String answer) {
	}

	@Override
	public void authenticate() throws Exception {
		IUserManagement um = ds().getComponent(IUserManagement.class);
		String username = um.getUsername();
		IAuthenticable a = ds().getComponent(IAuthenticable.class);
		String password = (String) a.getChallenge();
		LOG.info("username=" + username);
		LOG.info("password=" + password);
		if (ds().usesComponent(ISecurity.class)) {
			ISecurity security = ds().getComponent(ISecurity.class);
			this.user = security.getUser(username, password);
			if (user == null) {
				throw new Exception("Bad login/password");
			}
		} else {
			Address address = getUserAddress(username, password);
			byte[] value = map.get(address);
			if (value == null) {
				throw new Exception(
						"The user with this password does not exist.");
			}
			this.user = (IUser) ds().serializer.deserialize(value);
		}
		ds().setContext(new Context(user, ds().getComponent(IDataModel.class)));
	}

	private Address getUserAddress(String username, String password) {
		return new Address(ds().md.hash((username + password).getBytes()));
	}

	@Override
	public void setChallenge(Object challenge) {
		this.password = (String) challenge;
	}

	@Override
	public Object getChallenge() {
		return password;
	}

	@Override
	public void unauthenticate() throws Exception {
		setChallenge(null);
	}

}
